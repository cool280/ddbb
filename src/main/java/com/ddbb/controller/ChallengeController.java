package com.ddbb.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.annotate.DdbbController;
import com.ddbb.config.ChallengeConfig;
import com.ddbb.controller.request.*;
import com.ddbb.enums.UserType;
import com.ddbb.mongo.entity.User;
import com.ddbb.mongo.repo.UserRepo;
import com.ddbb.service.challenge.ChallengeKit;
import com.ddbb.service.challenge.ChallengeService;
import com.ddbb.utils.DateUtilPlus;
import com.ddbb.utils.ObjectConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@DdbbController
@RequestMapping("/challenge")
@Slf4j
public class ChallengeController extends BaseController {
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ChallengeKit challengeKit;

    /**
     * 发起挑战
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/launchChallenge")
    public JSONObject launchChallenge(@RequestBody LaunchChallengeRequest request){
        log.info("[launchChallenge] >>> start: {}", ObjectConverter.o2s(request));
        try{
            //0. 身份校验
            User from = userRepo.findByQid(request.getFrom());
            if(from.getUserType() == UserType.ASSISTANT_COACH.getCode()){
                return ERROR("助教不可发起挑战");
            }
            //1. 参数校验
            if(request.getFrom() == null || request.getTo() == null || request.getHallId()==null||
                    request.getStartTime()==null||request.getEndTime()==null||
                    StringUtils.isBlank(request.getChallengeDateStr())){
                return ERROR("param error");
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
                return ERROR("开始时间必须为下一个整点小时");
            }
            if(startFromRequest.isAfter(availableOrderTime.getRight())){
                return ERROR("开始时间不能超过后天的23点");
            }

            //3. 校验to在该时段是否空闲

            //4. 发起挑战
            if(challengeService.launchChallenge(request)){
                return OK;
            }
            return ERROR("发起挑战失败，请稍后重试");
        }catch (Exception e){
            log.error("[launchChallenge] with error:{}",e.getMessage(),e);
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
