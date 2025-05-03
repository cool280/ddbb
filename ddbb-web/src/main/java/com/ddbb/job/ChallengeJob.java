package com.ddbb.job;

import com.ddbb.infra.data.mongo.entity.ChallengeEntity;
import com.ddbb.infra.data.mongo.repo.ChallengeRepo;
import com.ddbb.service.challenge.ChallengeConfig;
import com.ddbb.service.challenge.ChallengeKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ChallengeJob extends BaseJob{
    @Autowired
    private ChallengeRepo challengeRepo;
    @Autowired
    private ChallengeKit challengeKit;
    @Autowired
    private ChallengeConfig challengeConfig;
    /**
     * 关闭结束时间超过24h的挑战
     */
    @Scheduled(fixedRate = 1000 * 60 * 5)
    public void closeChallenge(){
        String queryId = UUID.randomUUID().toString().replace("-","");
        while (true){
            List<ChallengeEntity> list = challengeRepo.pagingQuery(queryId, 50);
            if(CollectionUtils.isEmpty(list)){
                break;
            }
            closeChallenge(list);
        }
    }

    private void closeChallenge(List<ChallengeEntity> list){
        int seconds = challengeConfig.getCloseWhenSecondsAfterEndTime();

        //去重
        Map<String,ChallengeEntity> map = list.stream().collect(Collectors.toMap(ChallengeEntity::getChallengeId, Function.identity(),(k,v)->v));

        map.values().stream().filter(c->c.getAlive() == null || c.getAlive().equals(true))
                .forEach(c->{
            Pair<LocalDateTime, LocalDateTime> pair = challengeKit.getChallengeStartAndEnd(c);
            LocalDateTime end = pair.getRight();
            LocalDateTime now = LocalDateTime.now();

            Duration duration = Duration.between(end, now);
            if(Math.abs(duration.getSeconds()) >= seconds){
                challengeRepo.closeChallenge(c.getChallengeId());
                log.info("[closeChallenge] >>> challengeId:{} has been closed",c.getChallengeId());
            }
        });
    }

}
