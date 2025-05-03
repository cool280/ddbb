package com.ddbb.infra.data.mongo.repo;

import com.ddbb.infra.data.mongo.MongoBaseRepository;
import com.ddbb.infra.data.mongo.entity.CoachWorkplaceEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 助教可出台球房表
 db.coach_workplace.createIndex({"coordinate":"2dsphere"},{"name":"idx_coordinate_2dsphere","background":true})
 */
@Repository
public class CoachWorkplaceReop extends MongoBaseRepository<CoachWorkplaceEntity> {
    @Override
    public String getCollectionName() {
        return "coach_workplace";
    }

    /**
     * 根据qid查她所有可出台球房
     * @param qid
     * @return
     */
    public List<CoachWorkplaceEntity> findByQid(Long qid){
        Criteria criteria = Criteria.where("qid").is(qid);
        return findAll(criteria);
    }
}
