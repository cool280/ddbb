package com.ddbb.test.challenge;

import com.ddbb.service.challenge.ChallengeConfig;
import com.ddbb.mongo.repo.ChallengeRepo;
import com.ddbb.service.challenge.ChallengeKit;
import com.ddbb.service.challenge.ChallengeScheduleDO;
import com.ddbb.utils.ObjectConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class ChallengeTest {
    @Autowired
    private ChallengeConfig challengeConfig;
    @Autowired
    private ChallengeKit challengeKit;
    @Resource
    private ChallengeRepo challengeRepo;

    @Test
    public void testConfig(){
        System.out.println("afterDays: "+challengeConfig.getAfterDays());
    }
    @Test
    public void testGetOccupationTime(){
        Map<String, List<Integer>> map = challengeKit.getOccupationTime(()->challengeRepo.getLaunchedTodayAndAfterChallenge(134L));
        System.out.println(ObjectConverter.o2s(map));
        map = challengeKit.getOccupationTime(()->challengeRepo.getReceivedTodayAndAfterChallenge(134L));
        System.out.println(ObjectConverter.o2s(map));
    }

    @Test
    public void testIsFree(){
        int start = 0;
        int end = 10;
        boolean b = challengeKit.isFree(134L,"2025-04-13",start,end);
        System.out.println(start+" - "+end+" is free: "+b);


        start = 0;
        end = 14;
        b = challengeKit.isFree(134L,"2025-04-13",start,end);
        System.out.println(start+" - "+end+" is free: "+b);

        start = 0;
        end = 15;
        b = challengeKit.isFree(134L,"2025-04-13",start,end);
        System.out.println(start+" - "+end+" is free: "+b);

        start = 14;
        end = 19;
        b = challengeKit.isFree(134L,"2025-04-13",start,end);
        System.out.println(start+" - "+end+" is free: "+b);

        start = 17;
        end = 19;
        b = challengeKit.isFree(134L,"2025-04-13",start,end);
        System.out.println(start+" - "+end+" is free: "+b);

        start = 22;
        end = 24;
        b = challengeKit.isFree(134L,"2025-04-13",start,end);
        System.out.println(start+" - "+end+" is free: "+b);
    }
    @Test
    public void testGetSchedule(){
        Map<String, ChallengeScheduleDO> schedule = challengeKit.getSchedule(134L);
        System.out.println(ObjectConverter.o2s(schedule));
    }
}
