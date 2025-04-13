package com.ddbb.mongo.repo;

import com.ddbb.config.ChallengeConfig;
import com.ddbb.mongo.MongoBaseRepository;
import com.ddbb.mongo.entity.Challenge;
import com.ddbb.mongo.entity.Hall;
import com.ddbb.mongo.entity.User;
import com.ddbb.service.challenge.ChallengeKit;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
@Repository
public class ChallengeRepo extends MongoBaseRepository<Challenge> {
    @Resource
    private UserRepo userRepo;


    @Override
    public String getCollectionName() {
        return "challenge";
    }

    /**
     * 获取发出的大于等于今天的挑战书
     * @param qid
     * @return
     */
    public List<Challenge> getLaunchedFutureChallenge(Long qid){
        Criteria criteria = Criteria.where("from").is(qid).and("owner").is(qid).and("challengeDate").gte(new Date());
        List<Challenge> all = findAll(criteria);
        return all;
    }

    /**
     * 获取收到的大于等于今天的挑战书
     * @param qid
     * @return
     */
    public List<Challenge> getReceivedFutureChallenge(Long qid){
        Criteria criteria = Criteria.where("to").is(qid).and("owner").is(qid).and("challengeDate").gte(new Date());
        List<Challenge> all = findAll(criteria);
        return all;
    }


}
