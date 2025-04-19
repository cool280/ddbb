package com.ddbb.mongo.repo;

import com.ddbb.mongo.MongoBaseRepository;
import com.ddbb.mongo.entity.UserEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

/**
 db.user.createIndex({"coordinate":"2dsphere"},{"name":"idx_coordinate_2dsphere","background":true})
 db.user.createIndex({"qid":1},{"name":"uk_qid",unique: true,"background":true})
 db.user.createIndex({"aid":1},{"name":"uk_aid",unique: true,"background":true})
 db.user.createIndex({"phone":1},{"name":"uk_phone",unique: true,"background":true})
 */
@Repository
public class UserRepo extends MongoBaseRepository<UserEntity> {
    @Override
    public String getCollectionName() {
        return "user";
    }

    /**
     * 根据qid查人
     * @param qid
     * @return
     */
    public UserEntity findByQid(Long qid){
        Criteria criteria = Criteria.where("qid").is(qid);
        return findOne(criteria);
    }
    /**
     * 根据qid查人
     * @param phone
     * @return
     */
    public UserEntity findByPhone(String phone){
        Criteria criteria = Criteria.where("phone").is(phone);
        return findOne(criteria);
    }
}
