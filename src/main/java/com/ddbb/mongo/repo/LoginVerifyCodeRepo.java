package com.ddbb.mongo.repo;

import com.ddbb.mongo.MongoBaseRepository;
import com.ddbb.mongo.entity.HallEntity;
import com.ddbb.mongo.entity.LoginVerifyCodeEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 db.login_verify_code.createIndex({ "createTime": 1 }, { "name":'idx_ex_createTime',expireAfterSeconds: 300 ,background:true});
 db.login_verify_code.createIndex({"phone":1},{"name":'uk_phone',unique: true, background:true})
 // 对多个字段创建唯一索引（关系数据库中的联合主键）
 */
@Repository
public class LoginVerifyCodeRepo extends MongoBaseRepository<LoginVerifyCodeEntity> {
    @Override
    public String getCollectionName() {
        return "login_verify_code";
    }
    /**
     * @return
     */
    public LoginVerifyCodeEntity findByPhone(String phone){
        Criteria criteria = Criteria.where("phone").is(phone);
        return findOne(criteria);
    }

    /**
     * upsert验证码
     * @param phone
     * @param verifyCode
     */
    public void upsert(String phone,String verifyCode){
        Criteria criteria = Criteria.where("phone").is(phone);
        Query query = new Query(criteria);

        Update update = new Update();
        update.set("phone",phone).set("verifyCode",verifyCode).set("createTime",new Date());
        mongoTemplate.upsert(query,update,getCollectionName());
    }
}
