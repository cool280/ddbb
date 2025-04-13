package com.ddbb.mongo.repo;

import com.ddbb.config.ChallengeConfig;
import com.ddbb.mongo.MongoBaseRepository;
import com.ddbb.mongo.entity.Challenge;
import com.ddbb.mongo.entity.Hall;
import com.ddbb.mongo.entity.User;
import com.ddbb.service.challenge.ChallengeKit;
import com.ddbb.utils.DateUtilPlus;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.LocalDate;
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
     * 获取date那天发出及收到的挑战书
     * @param qid
     * @param dateStr yyyy-MM-dd
     * @return
     */
    public List<Challenge> getAllChallengeByDate(Long qid, String dateStr){
        Criteria criteria = Criteria.where("owner").is(qid).and("challengeDateStr").is(dateStr);
        List<Challenge> all = findAll(criteria);
        return all;
    }
    /**
     * 获取date那天收到的挑战书
     * @param qid
     * @param dateStr yyyy-MM-dd
     * @return
     */
    public List<Challenge> getReceivedChallengeByDate(Long qid, String dateStr){
        Criteria criteria = Criteria.where("owner").is(qid).and("to").is(qid).and("challengeDateStr").is(dateStr);
        List<Challenge> all = findAll(criteria);
        return all;
    }
    /**
     * 获取date那天发出的挑战书
     * @param qid
     * @param dateStr yyyy-MM-dd
     * @return
     */
    public List<Challenge> getLaunchedChallengeByDate(Long qid, String dateStr){
        Criteria criteria = Criteria.where("owner").is(qid).and("from").is(qid).and("challengeDateStr").is(dateStr);
        List<Challenge> all = findAll(criteria);
        return all;
    }

    /**
     * 获取发出及收到的大于等于今天的挑战书
     * @param qid
     * @return
     */
    public List<Challenge> getAllTodayAndAfterChallenge(Long qid){
        int today = DateUtilPlus.getCurrentDateInt();
        Criteria criteria = Criteria.where("owner").is(qid).and("challengeDateInt").gte(today);
        List<Challenge> all = findAll(criteria);
        return all;
    }
    /**
     * 获取发出的大于等于今天的挑战书
     * @param qid
     * @return
     */
    public List<Challenge> getLaunchedTodayAndAfterChallenge(Long qid){
        int today = DateUtilPlus.getCurrentDateInt();
        Criteria criteria = Criteria.where("owner").is(qid).and("from").is(qid).and("challengeDateInt").gte(today);
        List<Challenge> all = findAll(criteria);
        return all;
    }

    /**
     * 获取收到的大于等于今天的挑战书
     * @param qid
     * @return
     */
    public List<Challenge> getReceivedTodayAndAfterChallenge(Long qid){
        int today = DateUtilPlus.getCurrentDateInt();
        Criteria criteria = Criteria.where("owner").is(qid).and("to").is(qid).and("challengeDateInt").gte(today);
        List<Challenge> all = findAll(criteria);
        return all;
    }


}
