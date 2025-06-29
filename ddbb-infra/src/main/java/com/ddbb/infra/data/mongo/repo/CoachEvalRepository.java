package com.ddbb.infra.data.mongo.repo;

import com.ddbb.infra.data.mongo.entity.CoachEvalEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachEvalRepository extends MongoRepository<CoachEvalEntity, Long> {

    CoachEvalEntity findByOrderId(Long orderId);

}
