package com.ddbb.infra.data.mongo.repo;

import com.ddbb.infra.data.mongo.entity.PointRecordEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRecordRepository extends MongoRepository<PointRecordEntity, Long> {

    List<PointRecordEntity> findByUidOrderByCtsDesc(Long uid);

    // 使用 findBy + 属性名 + 条件 的格式
    List<PointRecordEntity> findByUidAndCtsBetween(Long uid, Long startTime, Long endTime);
}
