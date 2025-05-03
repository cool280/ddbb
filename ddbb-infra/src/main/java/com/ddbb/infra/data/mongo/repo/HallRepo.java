package com.ddbb.infra.data.mongo.repo;

import com.ddbb.infra.data.mongo.MongoBaseRepository;
import com.ddbb.infra.data.mongo.entity.HallEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
/**
 db.hall.createIndex({"coordinate":"2dsphere"},{"name":"idx_coordinate_2dsphere","background":true})
 db.hall.createIndex({"hallId":1},{"name":"uk_hallId",unique: true,"background":true})
 */
@Repository
public class HallRepo extends MongoBaseRepository<HallEntity> {
    @Override
    public String getCollectionName() {
        return "hall";
    }
    /**
     * 根据hallId查球房
     * @param hallId
     * @return
     */
    public HallEntity findByHallId(Long hallId){
        Criteria criteria = Criteria.where("hallId").is(hallId);
        return findOne(criteria);
    }
}
