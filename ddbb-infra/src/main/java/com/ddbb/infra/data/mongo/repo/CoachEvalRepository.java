package com.ddbb.infra.data.mongo.repo;

import com.ddbb.infra.data.mongo.entity.CoachAggregationResult;
import com.ddbb.infra.data.mongo.entity.CoachEvalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface CoachEvalRepository extends MongoRepository<CoachEvalEntity, Long> {

    Page<CoachEvalEntity> findAllByCoachUid(Long coachUid, Pageable pageable);


    @Aggregation(pipeline = {
            "{$match: {coachUid: ?0}}",
            "{$group: {_id: '$coachUid', satisfaction: {$sum: '$satisfaction'}, appearance: {$sum: '$appearance'}, skill: {$sum: '$skill'} , attitude:{$sum: '$attitude'},count:{$sum:1}}}"
    })
    CoachAggregationResult sumAndCountByCoachUid(Long coachUid);
}
