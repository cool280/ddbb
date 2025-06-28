package com.ddbb.infra.data.mongo.repo;

import com.ddbb.infra.data.mongo.entity.PointRecordEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRecordRepository extends MongoRepository<PointRecordEntity, Long> {

}
