package com.ddbb;

import com.ddbb.config.ChallengeConfig;
import com.ddbb.mongo.repo.ChallengeRepo;
import com.ddbb.service.challenge.ChallengeKit;
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
        Map<String, List<Integer>> map = challengeKit.getOccupationTimeCausedByReceiveChallenge(()->challengeRepo.getLaunchedFutureChallenge(134L));
        System.out.println(ObjectConverter.o2s(map));
    }
}
