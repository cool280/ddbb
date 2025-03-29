package com.ddbb.mongo;

import com.ddbb.mongo.entity.AssistantCoach;
import com.ddbb.mongo.entity.Hall;
import org.springframework.stereotype.Repository;

@Repository
public class HallRepo extends MongoBaseRepository<Hall>{
    @Override
    public String getCollectionName() {
        return "hall";
    }
}
