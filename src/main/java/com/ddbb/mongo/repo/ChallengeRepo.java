package com.ddbb.mongo.repo;

import com.ddbb.enums.ChallengeStatus;
import com.ddbb.mongo.MongoBaseRepository;
import com.ddbb.mongo.entity.ChallengeEntity;
import com.ddbb.utils.DateUtilPlus;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 */
@Repository
public class ChallengeRepo extends MongoBaseRepository<ChallengeEntity> {
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
    public List<ChallengeEntity> getAllChallengeByDate(Long qid, String dateStr){
        Criteria criteria = Criteria.where("owner").is(qid).and("challengeDateStr").is(dateStr);
        List<ChallengeEntity> all = findAll(criteria);
        return all;
    }
    /**
     * 获取date那天收到的挑战书
     * @param qid
     * @param dateStr yyyy-MM-dd
     * @return
     */
    public List<ChallengeEntity> getReceivedChallengeByDate(Long qid, String dateStr){
        Criteria criteria = Criteria.where("owner").is(qid).and("to").is(qid).and("challengeDateStr").is(dateStr);
        List<ChallengeEntity> all = findAll(criteria);
        return all;
    }
    /**
     * 获取date那天发出的挑战书
     * @param qid
     * @param dateStr yyyy-MM-dd
     * @return
     */
    public List<ChallengeEntity> getLaunchedChallengeByDate(Long qid, String dateStr){
        Criteria criteria = Criteria.where("owner").is(qid).and("from").is(qid).and("challengeDateStr").is(dateStr);
        List<ChallengeEntity> all = findAll(criteria);
        return all;
    }

    /**
     * 获取发出及收到的大于等于今天的挑战书
     * @param qid
     * @return
     */
    public List<ChallengeEntity> getAllTodayAndAfterChallenge(Long qid){
        int today = DateUtilPlus.getCurrentDateInt();
        Criteria criteria = Criteria.where("owner").is(qid).and("challengeDateInt").gte(today);
        List<ChallengeEntity> all = findAll(criteria);
        return all;
    }
    /**
     * 获取发出的大于等于今天的挑战书
     * @param qid
     * @return
     */
    public List<ChallengeEntity> getLaunchedTodayAndAfterChallenge(Long qid){
        int today = DateUtilPlus.getCurrentDateInt();
        Criteria criteria = Criteria.where("owner").is(qid).and("from").is(qid).and("challengeDateInt").gte(today);
        List<ChallengeEntity> all = findAll(criteria);
        return all;
    }

    /**
     * 获取收到的大于等于今天的挑战书
     * @param qid
     * @return
     */
    public List<ChallengeEntity> getReceivedTodayAndAfterChallenge(Long qid){
        int today = DateUtilPlus.getCurrentDateInt();
        Criteria criteria = Criteria.where("owner").is(qid).and("to").is(qid).and("challengeDateInt").gte(today);
        List<ChallengeEntity> all = findAll(criteria);
        return all;
    }

    /**
     * 根据challengeId查询，会返回一对儿
     * @param challengeId
     * @return
     */
    public List<ChallengeEntity> findByChallengeId(String challengeId){
        Criteria criteria = Criteria.where("challengeId").is(challengeId);
        List<ChallengeEntity> all = findAll(criteria);
        return all;
    }
    /**
     * 根据challengeId和from查询
     * @param challengeId
     * @return
     */
    public ChallengeEntity findByChallengeIdAndFrom(String challengeId,Long from){
        List<ChallengeEntity> list = findByChallengeId(challengeId);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.stream().filter(e->e.getFrom().equals(from)).findFirst().orElse(null);
    }
    /**
     * 根据challengeId和from查询
     * @param challengeId
     * @return
     */
    public ChallengeEntity findByChallengeIdAndTo(String challengeId,Long to){
        List<ChallengeEntity> list = findByChallengeId(challengeId);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.stream().filter(e->e.getTo().equals(to)).findFirst().orElse(null);
    }

    /**
     * 根据challengeId更新两条记录的状态
     * @param challengeId
     */
    public void updateChallengeStatus(String challengeId,ChallengeStatus status){
        Criteria criteria = Criteria.where("challengeId").is(challengeId);
        Query q = new Query(criteria);

        Update u = new Update().set("status", status.getCode());
        mongoTemplate.updateMulti(q,u,getCollectionName());
    }
}
