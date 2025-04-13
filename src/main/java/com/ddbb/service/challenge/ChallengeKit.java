package com.ddbb.service.challenge;

import com.ddbb.config.ChallengeConfig;
import com.ddbb.mongo.entity.Challenge;
import com.ddbb.mongo.entity.User;
import com.ddbb.mongo.repo.ChallengeRepo;
import com.ddbb.mongo.repo.UserRepo;
import com.ddbb.utils.DateUtilPlus;
import com.ddbb.utils.DdbbUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Supplier;

@Service
public class ChallengeKit {
    @Resource
    private ChallengeConfig challengeConfig;
    @Resource
    private UserRepo userRepo;
    @Resource
    private ChallengeRepo challengeRepo;

    /**
     * <div>获取当前可预约的最小时间和最大时间</div>
     * <div>left：最小时间，今天的下一个整点</div>
     * <div>right：最大时间，后天的23点</div>
     * @return
     */
    public Pair<LocalDateTime,LocalDateTime> getAvailableOrderTime(){
        LocalDateTime now = LocalDateTime.now();
        //拿到今天的下一个整点
        LocalDateTime min = now.with(LocalTime.of(DateUtilPlus.getNextHour(), 0,0));
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
        List<Challenge> list = challengeRepo.getAllChallengeByDate(qid,dateStr);
        if(CollectionUtils.isEmpty(list)){
            return true;
        }
        int[] requestRange = {startTime,endTime};
        for(Challenge c: list){
            int[] existsRange = {c.getStartTime(),c.getEndTime()};
            if(DdbbUtil.isOverlap(requestRange,existsRange,false)){
                return false;
            }
        }
        return true;
    }

    public Map<String,ChallengeScheduleDO> getSchedule(Long qid){
        Map<String,ChallengeScheduleDO> ret = new LinkedHashMap<>();
        //今天及以后发起的挑战
        Map<String,List<Integer>> occupationLaunched = getOccupationTime(()->challengeRepo.getLaunchedTodayAndAfterChallenge(qid));
        //今天及以后收到的挑战
        Map<String,List<Integer>> occupationReceived = getOccupationTime(()->challengeRepo.getReceivedTodayAndAfterChallenge(qid));

        User user = userRepo.findByQid(qid);
        int workTimeStart = user.getWorkTimeStart();
        int workTimeEnd = user.getWorkTimeEnd();

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

            //循环每天的每个小时
            List<HourDO> hourList = new ArrayList<>();
            for(int hour = workTimeStart;hour<workTimeEnd;hour++){
                HourDO hourDO= new HourDO();
                hourDO.setStart(hour);
                hourDO.setEnd(hour + 1);

                int code = 0;
                String hourDesc = "空闲";
                //是否空闲
                if(occupationLaunched.get(dateStr)!=null){
                    if(occupationLaunched.get(dateStr).contains(hour)){
                        code = 1;
                        hourDesc = "向别人发起了挑战";
                    }
                }else if(occupationReceived.get(dateStr)!=null){
                    if(occupationReceived.get(dateStr).contains(hour)){
                        code = 2;
                        hourDesc = "别人向TA发起了挑战";
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
    public Map<String,List<Integer>> getOccupationTime(Supplier<List<Challenge>> func){
        Map<String,List<Integer>> ret = new HashMap<>();
        List<Challenge> futureChallenge = func.get();
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



}
