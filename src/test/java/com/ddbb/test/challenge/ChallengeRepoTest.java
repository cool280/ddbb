package com.ddbb.test.challenge;

import com.ddbb.mongo.entity.ChallengeEntity;
import com.ddbb.utils.ObjectConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class ChallengeRepoTest {
    @Resource
    private com.ddbb.mongo.repo.ChallengeRepo challengeRepo;

    @Test
    public void testAllChallengeByDate(){
        List<ChallengeEntity> allChallengeByDate = challengeRepo.getAllChallengeByDate(134L, "2025-04-13");
        System.out.println(allChallengeByDate.size()+" records: "+ObjectConverter.o2s(allChallengeByDate));

        allChallengeByDate = challengeRepo.getAllChallengeByDate(176L, "2025-04-11");
        System.out.println(allChallengeByDate.size()+" records: "+ObjectConverter.o2s(allChallengeByDate));
    }

    @Test
    public void testChallengeByDate(){
        List<ChallengeEntity> list = challengeRepo.getLaunchedChallengeByDate(176L, "2025-04-11");
        System.out.println(list.size()+" records: "+ObjectConverter.o2s(list));

        list = challengeRepo.getReceivedChallengeByDate(134L, "2025-04-13");
        System.out.println(list.size()+" records: "+ObjectConverter.o2s(list));
    }

    @Test
    public void testAllTodayAndAfterChallenge(){
        List<ChallengeEntity> list = challengeRepo.getAllTodayAndAfterChallenge(134L);
        System.out.println(list.size()+" records: "+ObjectConverter.o2s(list));

        list = challengeRepo.getAllTodayAndAfterChallenge(176L);
        System.out.println(list.size()+" records: "+ObjectConverter.o2s(list));
    }

    @Test
    public void testTodayAndAfterChallenge(){
        List<ChallengeEntity> list = challengeRepo.getReceivedTodayAndAfterChallenge(134L);
        System.out.println(list.size()+" records: "+ObjectConverter.o2s(list));

        list = challengeRepo.getLaunchedTodayAndAfterChallenge(176L);
        System.out.println(list.size()+" records: "+ObjectConverter.o2s(list));
    }
}
