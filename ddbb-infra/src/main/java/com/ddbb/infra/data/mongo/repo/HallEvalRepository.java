package com.ddbb.infra.data.mongo.repo;

import com.ddbb.infra.data.mongo.entity.HallEvalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallEvalRepository extends MongoRepository<HallEvalEntity, Long> {

    Page<HallEvalEntity> findByHallId(Long hallId, Pageable pageable);
}

