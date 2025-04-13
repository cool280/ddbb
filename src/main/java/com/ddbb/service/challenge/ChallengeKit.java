package com.ddbb.service.challenge;

import com.ddbb.config.ChallengeConfig;
import com.ddbb.mongo.entity.Challenge;
import com.ddbb.mongo.entity.User;
import com.ddbb.mongo.repo.ChallengeRepo;
import com.ddbb.mongo.repo.UserRepo;
import com.ddbb.utils.DateUtilPlus;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.LongFunction;
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

    public void getSchedule(Long qid){
        //发起的挑战
        Map<String,List<Integer>> occupationLaunched = getOccupationTimeCausedByReceiveChallenge(()->challengeRepo.getLaunchedFutureChallenge(qid));
        //收到的挑战
        Map<String,List<Integer>> occupationReceived = getOccupationTimeCausedByReceiveChallenge(()->challengeRepo.getReceivedFutureChallenge(qid));

        User user = userRepo.findByQid(qid);
        int workTimeStart = user.getWorkTimeStart();
        int workTimeEnd = user.getWorkTimeEnd();

        //循环查找今天、明天、后天的行程表
        //Pair<LocalDateTime, LocalDateTime> availableOrderTime = getAvailableOrderTime();
        LocalDateTime loop = LocalDateTime.now();
        for(int i=0;i<challengeConfig.getAfterDays();i++){
            if(i!=0){
                loop = loop.plusDays(i);
            }
            String dateStr = DateUtilPlus.localDateTime2StringYmd(loop);
            //循环每天的每个小时
            Map<Integer,Integer> hourMap = new LinkedHashMap<>();
            for(int hour = 0;hour<24;hour++){
                if(hour<workTimeStart || hour > workTimeEnd){
                    hourMap.put(hour,-1);
                }else{
                    hourMap.put(hour,0);
                }
            }
        }


    }

    /**
     * 获取qid作为被挑战方，未来的占用时间: 14表示14点-15点有约
     * @param func
     * @return {"2015-03-01":[14,15,16],"2015-03-02":[20,21],"2015-03-03":[]}
     */
    public Map<String,List<Integer>> getOccupationTimeCausedByReceiveChallenge(Supplier<List<Challenge>> func){
        Map<String,List<Integer>> ret = new HashMap<>();
        List<Challenge> futureChallenge = func.get();
        if(CollectionUtils.isEmpty(futureChallenge)){
            return ret;
        }

        futureChallenge.forEach(c->{
            String ymd = DateUtilPlus.utilDate2StringYmd(c.getChallengeDate());
            List<Integer> list = ret.get(ymd);
            if(list == null){
                list = new ArrayList<>();
                ret.put(ymd,list);
            }
            for(int hour = c.getStartTime();hour<c.getEndTime();hour++){
                list.add(hour);
            }

        });
        return ret;
    }
}
