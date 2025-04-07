package com.ddbb.mongo.repo;

import com.ddbb.mongo.MongoBaseRepository;
import com.ddbb.mongo.entity.AssistantCoach;
import org.springframework.stereotype.Repository;

/**
 db.user.createIndex({"coordinate":"2dsphere"},{"name":"idx_coordinate_2dsphere","background":true})
 db.user.createIndex({"qid":1},{"name":"uk_qid",unique: true,"background":true})
 */
@Repository
public class AssistantCoachRepo extends MongoBaseRepository<AssistantCoach> {
    @Override
    public String getCollectionName() {
        return "user";
    }
}
