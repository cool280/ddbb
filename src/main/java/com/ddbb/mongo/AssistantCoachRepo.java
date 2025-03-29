package com.ddbb.mongo;

import com.ddbb.mongo.entity.AssistantCoach;
import org.springframework.stereotype.Repository;

@Repository
public class AssistantCoachRepo extends MongoBaseRepository<AssistantCoach>{
    @Override
    public String getCollectionName() {
        return "user";
    }
}
