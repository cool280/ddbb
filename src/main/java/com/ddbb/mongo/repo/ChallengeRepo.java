package com.ddbb.mongo.repo;

import com.ddbb.mongo.MongoBaseRepository;
import com.ddbb.mongo.entity.Challenge;
import com.ddbb.mongo.entity.Hall;
import org.springframework.stereotype.Repository;

/**
 */
@Repository
public class ChallengeRepo extends MongoBaseRepository<Challenge> {
    @Override
    public String getCollectionName() {
        return "challenge";
    }
}
