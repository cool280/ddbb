package com.ddbb.service.challenge;

import com.ddbb.infra.data.mongo.entity.ChallengeEntity;
import com.ddbb.infra.data.mongo.entity.CoachWorkplaceEntity;
import com.ddbb.infra.data.mongo.entity.UserEntity;
import com.ddbb.infra.data.mongo.repo.ChallengeRepo;
import com.ddbb.infra.data.mongo.repo.CoachWorkplaceReop;
import com.ddbb.infra.data.mongo.repo.HallRepo;
import com.ddbb.infra.data.mongo.repo.UserRepo;
import com.ddbb.service.hall.WorkplaceVO;
import com.ddbb.internal.utils.DateUtilPlus;
import com.ddbb.internal.utils.DdbbUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChallengeKit {
    @Resource
    private ChallengeConfig challengeConfig;
    @Resource
    private UserRepo userRepo;
    @Resource
    private HallRepo hallRepo;
    @Resource
    private ChallengeRepo challengeRepo;
    @Resource
    private CoachWorkplaceReop coachWorkplaceReop;

    /**
     * <div>获取当前可预约的最小时间和最大时间</div>
     * <div>left：最小时间，今天的下一个整点</div>
     * <div>right：最大时间，后天的23点</div>
     * @return
     */
    public Pair<LocalDateTime,LocalDateTime> getAvailableOrderTime(){
        LocalDateTime now = LocalDateTime.now();
        //拿到今天的下一个整点
        LocalDateTime min = now.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        //拿到后天的23点
        int afterDays = challengeConfig.getAfterDays();
        LocalDateTime max =now.plusDays(afterDays).with(LocalTime.of(23, 0,0));

        return Pair.of(min,max);
    }

    /**
     * 检查一个人在给定的条件下是否空闲，不能有发起的挑战，不能有接受的挑战
     * @param qid
     * @param dateStr
     * @param startTime
     * @param endTime
     * @return
     */
    public boolean isFree(Long qid,String dateStr,Integer startTime,Integer endTime){
        List<ChallengeEntity> list = challengeRepo.getAllChallengeByDate(qid,dateStr);
        if(CollectionUtils.isEmpty(list)){
            return true;
        }
        int[] requestRange = {startTime,endTime};
        for(ChallengeEntity c: list){
            int[] existsRange = {c.getStartTime(),c.getEndTime()};
            if(DdbbUtil.isOverlap(requestRange,existsRange,false)){
                return false;
            }
        }
        return true;
    }

    /**
     * 获取一个人今天及以后的行程
     * @param qid
     * @return
     */
    public Map<String, ChallengeScheduleDO> getSchedule(Long qid){
        Map<String,ChallengeScheduleDO> ret = new LinkedHashMap<>();
        if(qid == null){
            return ret;
        }
        //今天及以后发起的挑战
        Map<String,List<Integer>> occupationLaunched = getOccupationTime(()->challengeRepo.getLaunchedTodayAndAfterChallenge(qid));
        //今天及以后收到的挑战
        Map<String,List<Integer>> occupationReceived = getOccupationTime(()->challengeRepo.getReceivedTodayAndAfterChallenge(qid));

        UserEntity user = userRepo.findByQid(qid);

        //周几上班
        String workWeekDayStr = StringUtils.isBlank(user.getWorkWeekDay())?"1,2,3,4,5,6,7":user.getWorkWeekDay();
        List<Integer> workWeekDayList = Stream.of(workWeekDayStr.split(",")).map(String::trim)
                    .map(Integer::parseInt).collect(Collectors.toList());
        //每天上班具体时间
        int workTimeStart = user.getWorkTimeStart() == null?challengeConfig.getDefaultWorkTimeStart():user.getWorkTimeStart();
        int workTimeEnd = user.getWorkTimeEnd() == null?challengeConfig.getDefaultWorkTimeEnd():user.getWorkTimeEnd();

        //循环查找今天、明天、后天的行程表
        LocalDateTime loop = LocalDateTime.now();
        for(int i=0;i<=challengeConfig.getAfterDays();i++){
            //时间放在最上！！！
            if(i!=0){//i=0表示今天，之后loop每次+1天
                loop = loop.plusDays(1);
            }

            ChallengeScheduleDO challengeScheduleDO = new ChallengeScheduleDO();
            String dateStr = DateUtilPlus.localDateTime2StringYmd(loop);
            String dateDesc="";
            if(i == 0){
                dateDesc = "今天";
            }else if(i==1){
                dateDesc = "明天";
            }else if(i==2){
                dateDesc = "后天";
            }else{
                dateDesc = i+"天后";
            }

            int week = loop.getDayOfWeek().getValue();

            challengeScheduleDO.setDateStr(dateStr);
            challengeScheduleDO.setDateDesc(dateDesc);
            challengeScheduleDO.setWeek(week);

            if(workWeekDayList.contains(week)){//全天休息
                challengeScheduleDO.setAtWork(true);
            }else{
                challengeScheduleDO.setAtWork(false);
                ret.put(dateStr,challengeScheduleDO);
                continue;
            }

            //循环每天的每个小时
            List<HourDO> hourList = new ArrayList<>();
            for(int hour = 0;hour<24;hour++){
                HourDO hourDO= new HourDO();
                hourDO.setStart(hour);
                hourDO.setEnd(hour + 1);

                int code = 0;
                String hourDesc = "空闲";
                if(hour < workTimeStart || hour >= workTimeEnd){
                    code = -1;
                    hourDesc = "休息";
                }else{
                    //是否空闲
                    if(occupationLaunched.get(dateStr)!=null){
                        //检查该时间段是否发起了挑战
                        if(occupationLaunched.get(dateStr).contains(hour)){
                            code = 1;
                            hourDesc = "向别人发起了挑战";
                        }
                    }else if(occupationReceived.get(dateStr)!=null){
                        //检查该时间段是否接受了挑战
                        if(occupationReceived.get(dateStr).contains(hour)){
                            code = 2;
                            hourDesc = "别人向TA发起了挑战";
                        }
                    }
                }

                hourDO.setCode(code);
                hourDO.setHourDesc(hourDesc);

                hourList.add(hourDO);
            }

            challengeScheduleDO.setHourList(hourList);

            ret.put(dateStr,challengeScheduleDO);
        }

        return ret;
    }

    /**
     * 获取qid未来的占用时间: 14表示14点-15点有约
     * @param func
     * @return {"2015-03-01":[14,15,16],"2015-03-02":[20,21],"2015-03-03":[]}
     */
    public Map<String,List<Integer>> getOccupationTime(Supplier<List<ChallengeEntity>> func){
        Map<String,List<Integer>> ret = new HashMap<>();
        List<ChallengeEntity> futureChallenge = func.get();
        if(CollectionUtils.isEmpty(futureChallenge)){
            return ret;
        }

        futureChallenge.forEach(c->{
            String ymd = c.getChallengeDateStr();
            List<Integer> list = ret.get(ymd);
            if(list == null){
                list = new ArrayList<>();
                ret.put(ymd,list);
            }
            for(int hour = c.getStartTime();hour<c.getEndTime();hour++){
                list.add(hour);
            }
            Collections.sort(list);
        });
        return ret;
    }

    /**
     * 获取助教可出台球房
     * @param qid
     * @return
     */
    public List<WorkplaceVO> getSomeoneWorkplace(Long qid){
        List<CoachWorkplaceEntity> list = coachWorkplaceReop.findByQid(qid);
        if(CollectionUtils.isEmpty(list)){
            return Collections.emptyList();
        }

        return list.stream().map(e->hallRepo.findByHallId(e.getHallId()))
                .map(hall->{
                    WorkplaceVO vo = new WorkplaceVO();
                    BeanUtils.copyProperties(hall,vo);
                    return vo;
                }).collect(Collectors.toList());
    }

    /**
     * 获取一个挑战书的起止时间
     * @param entity
     * @return left - 开始时间，right - 结束时间
     */
    public Pair<LocalDateTime,LocalDateTime> getChallengeStartAndEnd(ChallengeEntity entity){
        LocalDateTime start = DateUtilPlus.string2LocalDateTime(entity.getChallengeDateStr()+" "+ StringUtils.leftPad(entity.getStartTime().toString(),2,"0")+":00:00");
        LocalDateTime end = DateUtilPlus.string2LocalDateTime(entity.getChallengeDateStr()+" "+ StringUtils.leftPad(entity.getEndTime().toString(),2,"0")+":00:00");
        return Pair.of(start,end);
    }
}
