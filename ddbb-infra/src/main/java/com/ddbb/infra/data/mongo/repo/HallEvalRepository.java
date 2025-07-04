package com.ddbb.infra.data.mongo.repo;

import com.ddbb.infra.data.mongo.entity.HallAggregationResult;
import com.ddbb.infra.data.mongo.entity.HallEvalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallEvalRepository extends MongoRepository<HallEvalEntity, Long> {

    Page<HallEvalEntity> findByHallId(Long hallId, Pageable pageable);

    @Aggregation(pipeline = {
            "{$match: {hallId: ?0}}",
            "{$group: {_id: 'hallId', satisfaction: {$sum: '$satisfaction'}, environment: {$sum: '$environment'}, facilities: {$sum: '$facilities'} , service:{$sum: '$service'},assistantScore:{$sum: '$assistantScore'},costPerformance:{$sum:'$costPerformance'},count:{$sum:1}}}"
    })
    HallAggregationResult sumAndCountByHallId(Long hallId);
}

