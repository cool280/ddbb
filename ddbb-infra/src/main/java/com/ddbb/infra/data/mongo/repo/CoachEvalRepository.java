package com.ddbb.infra.data.mongo.repo;

import com.ddbb.infra.data.mongo.entity.CoachEvalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachEvalRepository extends MongoRepository<CoachEvalEntity, Long> {

    Page<CoachEvalEntity> findAllByCoachUid(Long coachUid, Pageable pageable);
}
