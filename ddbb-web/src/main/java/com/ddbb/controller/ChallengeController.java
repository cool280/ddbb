package com.ddbb.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.controller.request.challenge.*;
import com.ddbb.internal.annotate.DdbbController;
import com.ddbb.service.challenge.ChallengeConfig;
import com.ddbb.controller.request.*;
import com.ddbb.internal.enums.UserType;
import com.ddbb.infra.data.mongo.entity.UserEntity;
import com.ddbb.infra.data.mongo.repo.UserRepo;
import com.ddbb.service.challenge.ChallengeKit;
import com.ddbb.service.challenge.ChallengeScheduleDO;
import com.ddbb.service.challenge.ChallengeService;
import com.ddbb.service.hall.WorkplaceVO;
import com.ddbb.service.user.UserVO;
import com.ddbb.internal.utils.DateUtilPlus;
import com.ddbb.internal.utils.ObjectConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@DdbbController
@RequestMapping("/challenge")
@Slf4j
@Api(tags = "挑战相关")
public class ChallengeController extends BaseController {
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ChallengeKit challengeKit;
    @Autowired
    private ChallengeConfig challengeConfig;
    /**
     * 发起挑战前的数据准备
     * @param request
     * @return
     */
    @ApiOperation(value = "发起挑战前的数据准备，获取助教行程日历等",position = 0)
    @ResponseBody
    @PostMapping("/prepareChallenge")
    public BaseResult prepareChallenge(HttpServletRequest httpServletRequest, @RequestBody PrepareChallengeRequest request){
        log.info("[prepareChallenge] >>> start: {}", ObjectConverter.o2s(request));
        JSONObject data = new JSONObject();
        try{
            //1. 参数校验
            Long to = request.getToUid();
            if(to == null){
                return ERROR("toUid cannot be null");
            }
            UserEntity userInfo = userRepo.findByUid(to);
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(userInfo,userVO);
            userVO.setAvatar(getImageAbsoluteUrl(httpServletRequest,userVO.getAvatar()));

            Map<String, ChallengeScheduleDO> schedule = challengeKit.getSchedule(to);

            List<WorkplaceVO> workplaceList = challengeKit.getSomeoneWorkplace(to);


            data.put("userInfo",userVO);
            data.put("schedule",schedule);
            data.put("workplace",workplaceList);
            data.put("message",ChallengeConfig.CHALLENGE_MESSAGE_MAP);
            return OK(data);
        }catch (Exception e){
            log.error("[launchChallenge] with error:{}",e.getMessage(),e);
            return ERROR;
        }

    }
    /**
     * 发起挑战
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/launchChallenge")
    @ApiOperation(value = "球友向助教发起挑战",position = 1)
    public BaseResult launchChallenge(@RequestBody LaunchChallengeRequest request){
        log.info("[launchChallenge] >>> start: {}", ObjectConverter.o2s(request));
        try{
            //0. 身份校验
            UserEntity from = userRepo.findByUid(request.getFrom());
            if(from == null){
                return ERROR("from uid is wrong");
            }
            if(from.getUserType() == UserType.ASSISTANT_COACH.getCode()){
                if(!challengeConfig.getAssistantCoachAllowLaunch()){
                    return ERROR("助教不可发起挑战");
                }
            }
            //1. 参数校验
            if(request.getFrom() == null || request.getTo() == null || request.getHallId()==null||
                    request.getStartTime()==null||request.getEndTime()==null||
                    StringUtils.isBlank(request.getChallengeDateStr())){
                return ERROR("param error");
            }
            if(request.getFrom().equals(request.getTo())){
                return ERROR("不能挑战自己");
            }
            int startTime = request.getStartTime();
            int endTime = request.getEndTime();
            if(!(startTime >=0 && startTime<=23)){
                return ERROR("startTime must be in range [0,23]");
            }
            if(!(endTime >=1 && startTime<=24)){
                return ERROR("endTime must be in range [1,24]");
            }
            if(endTime <= startTime){
                return ERROR("endTime must > startTime");
            }

            String regex = "\\d{4}-\\d{2}-\\d{2}";
            boolean isValidDate1 = request.getChallengeDateStr().matches(regex); // true 表示格式正确
            if(!isValidDate1){
                return ERROR("challenge date format error, must be yyyy-MM-dd");
            }

            //2. 校验时间合理性
            LocalDateTime startFromRequest = DateUtilPlus.string2LocalDateTime(request.getChallengeDateStr()+" "+ StringUtils.leftPad(request.getStartTime().toString(),2,"0")+":00:00");
            Pair<LocalDateTime, LocalDateTime> availableOrderTime = challengeKit.getAvailableOrderTime();
            if(startFromRequest.isBefore(availableOrderTime.getLeft())){
                return ERROR("开始时间最早必须为下一个整点小时");
            }
            if(startFromRequest.isAfter(availableOrderTime.getRight())){
                return ERROR("开始时间不能超过后天的23点");
            }

            //3. 校验from在该时段是否空闲
            boolean isFromFree = challengeKit.isFree(request.getFrom(),request.getChallengeDateStr(),request.getStartTime(),request.getEndTime());
            if(!isFromFree){
                return ERROR("您自己所选的时间段内已经有其他安排，请换个时间");
            }
            //4. 检验to在该时段是否空闲
            boolean isToFree = challengeKit.isFree(request.getTo(),request.getChallengeDateStr(),request.getStartTime(),request.getEndTime());
            if(!isToFree){
                return ERROR("该助教在您所选的时间段内已经有其他安排，请换个时间");
            }
            //5. 发起挑战
            Pair<Boolean,String> p = challengeService.launchChallenge(request);
            if(p.getLeft()){
                return OK(new HashMap<String,String>(){{put("challengeId",p.getRight());}},"发起挑战成功");
            }
            return ERROR("发起挑战失败，请稍后重试");
        }catch (Exception e){
            log.error("[launchChallenge] with error:{}",e.getMessage(),e);
            return ERROR;
        }

    }

    /**
     * 获取某人的行程表，从今天开始。uid放在from或to字段都可以，from优先
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/getSomeoneSchedule")
    public BaseResult getSomeoneSchedule(@RequestBody ChallengeRequest request){
        log.info("[getSomeoneSchedule] >>> start: {}", ObjectConverter.o2s(request));
        try{
            Long uid = request.getUid();
            Map<String, ChallengeScheduleDO> map = challengeKit.getSchedule(uid);
            return OK(map);
        }catch (Exception e){
            log.error("[getSomeoneSchedule] with error:{}",e.getMessage(),e);
            return ERROR;
        }
    }
    /**
     * 获取某人的可出台球房
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/getSomeoneWorkplace")
    public BaseResult getSomeoneWorkplace(@RequestBody ChallengeRequest request){
        log.info("[getSomeoneWorkplace] >>> start: {}", ObjectConverter.o2s(request));
        try{
            Long uid = request.getUid();
            List<WorkplaceVO> list = challengeKit.getSomeoneWorkplace(uid);
            return OK(list);
        }catch (Exception e){
            log.error("[getSomeoneWorkplace] with error:{}",e.getMessage(),e);
            return ERROR;
        }
    }


    /**
     * to接受挑战
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/acceptChallenge")
    @ApiOperation(value = "助教接受挑战",position = 6)
    public BaseResult acceptChallenge(@RequestBody AcceptChallengeRequest request){
        log.info("[acceptChallenge] >>> start: {}", ObjectConverter.o2s(request));
        try{
            Long uid = request.getUid();
            String challengeId = request.getChallengeId();
            if(uid == null || StringUtils.isBlank(challengeId)){
                return ERROR("param error");
            }
            Pair<Boolean,String> p = challengeService.acceptChallenge(request);
            if(p.getLeft()){
                return OK(null,"您已接受挑战，请按时赴约");
            }
            return ERROR(p.getRight());
        }catch (Exception e){
            log.error("[acceptChallenge] with error:{}",e.getMessage(),e);
            return ERROR;
        }
    }
    /**
     * to拒绝挑战
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/refuseChallenge")
    @ApiOperation(value = "助教拒绝挑战",position = 5)
    public BaseResult refuseChallenge(@RequestBody RefuseChallengeRequest request){
        log.info("[refuseChallenge] >>> start: {}", ObjectConverter.o2s(request));
        try{
            Long uid = request.getUid();
            String challengeId = request.getChallengeId();
            if(uid == null || StringUtils.isBlank(challengeId)){
                return ERROR("param error");
            }
            Pair<Boolean,String> p = challengeService.refuseChallenge(request);
            if(p.getLeft()){
                return OK("您已拒绝了该挑战");
            }
            return ERROR(p.getRight());
        }catch (Exception e){
            log.error("[refuseChallenge] with error:{}",e.getMessage(),e);
            return ERROR;
        }
    }

    /**
     * 取消挑战
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/cancelChallenge")
    @ApiOperation(value = "发起方（球友）或接受方（助教）取消挑战",position = 2)
    public BaseResult cancelChallenge(@RequestBody CancelChallengeRequest request){
        log.info("[cancelChallenge] >>> start: {}", ObjectConverter.o2s(request));
        try{
            Long uid = request.getUid();
            String challengeId = request.getChallengeId();
            if(uid == null || StringUtils.isBlank(challengeId)){
                return ERROR("param error");
            }
            Pair<Boolean,String> p = challengeService.cancelChallenge(request);
            if(p.getLeft()){
                return OK("您已取消了该挑战");
            }
            return ERROR(p.getRight());
        }catch (Exception e){
            log.error("[cancelChallenge] with error:{}",e.getMessage(),e);
            return ERROR;
        }
    }

    /**
     * 签到
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/signIn")
    @ApiOperation(value = "球友或助教签到",position = 5)
    public BaseResult signIn(@RequestBody SignInChallengeRequest request){
        log.info("[signIn] >>> start: {}", ObjectConverter.o2s(request));
        try{
            Long uid = request.getUid();
            String challengeId = request.getChallengeId();
            if(uid == null || StringUtils.isBlank(challengeId)){
                return ERROR("param error");
            }
            if(StringUtils.isBlank(request.getLongitude())
                    || StringUtils.isBlank(request.getLatitude())){
                return ERROR("经纬度不能为空");
            }
            Pair<Boolean,String> p = challengeService.signIn(request);
            if(p.getLeft()){
                return OK("您已签到，可电话联系对方");
            }
            return ERROR(p.getRight());
        }catch (Exception e){
            log.error("[signIn] with error:{}",e.getMessage(),e);
            return ERROR;
        }
    }


    @ResponseBody
    @PostMapping("/saveScore")
    @ApiOperation(value = "球友或助教记录比分")
    public BaseResult saveScore(@RequestBody SaveScoreRequest request){
        log.info("[saveScore] >>> start: {}", ObjectConverter.o2s(request));
        try{
            Long uid = request.getUid();
            String challengeId = request.getChallengeId();
            if(uid == null || StringUtils.isBlank(challengeId) ||
                    request.getFromWinRound() == null || request.getToWinRound() == null){
                return ERROR("param error");
            }
            Pair<Boolean,String> p = challengeService.saveScore(request);
            if(p.getLeft()){
                return OK("登记比分完成");
            }
            return ERROR(p.getRight());
        }catch (Exception e){
            log.error("[saveScore] with error:{}",e.getMessage(),e);
            return ERROR;
        }
    }

    /**
     * 评价助教
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/commentCoach")
    @ApiOperation(value = "球友评价助教")
    public BaseResult commentCoach(@RequestBody CommentCoachRequest request){
        //TODO 需高总的评价接口，这里只做状态的改变
        log.info("[commentCoach] >>> start: {}", ObjectConverter.o2s(request));
        try{
            Long uid = request.getUid();
            String challengeId = request.getChallengeId();
            if(uid == null || StringUtils.isBlank(challengeId)){
                return ERROR("param error");
            }
            Pair<Boolean,String> p = challengeService.commentCoach(request);
            if(p.getLeft()){
                return OK("助教评价完成");
            }
            return ERROR(p.getRight());
        }catch (Exception e){
            log.error("[commentCoach] with error:{}",e.getMessage(),e);
            return ERROR;
        }
    }

    /**
     * 评价球房
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/commentHall")
    @ApiOperation(value = "球友评价球房")
    public BaseResult commentHall(@RequestBody CommentHallRequest request){
        //TODO 需高总的评价接口，这里只做状态的改变
        log.info("[commentHall] >>> start: {}", ObjectConverter.o2s(request));
        try{
            Long uid = request.getUid();
            String challengeId = request.getChallengeId();
            if(uid == null || StringUtils.isBlank(challengeId)){
                return ERROR("param error");
            }
            Pair<Boolean,String> p = challengeService.commentHall(request);
            if(p.getLeft()){
                return OK("球房评价完成");
            }
            return ERROR(p.getRight());
        }catch (Exception e){
            log.error("[commentHall] with error:{}",e.getMessage(),e);
            return ERROR;
        }
    }
    public static void main(String[] args) {
        //拿到今天的下一个整点
        LocalDateTime min = LocalDateTime.now().with(LocalTime.of(DateUtilPlus.getNextHour(), 0,0));
        LocalDateTime max = LocalDateTime.now().plusDays(2).with(LocalTime.of(23, 59,59));
        System.out.println("min: "+DateUtilPlus.localDateTime2StringYmdHms(min));
        System.out.println("max: "+DateUtilPlus.localDateTime2StringYmdHms(max));
    }
}
