package com.ddbb.mongo.repo;

import com.ddbb.mongo.MongoBaseRepository;
import com.ddbb.mongo.entity.AssistantCoach;
import com.ddbb.mongo.entity.Hall;
import org.springframework.stereotype.Repository;
/**
 db.hall.createIndex({"coordinate":"2dsphere"},{"name":"idx_coordinate_2dsphere","background":true})
 db.hall.createIndex({"hallId":1},{"name":"uk_hallId",unique: true,"background":true})
 */
@Repository
public class HallRepo extends MongoBaseRepository<Hall> {
    @Override
    public String getCollectionName() {
        return "hall";
    }
}
