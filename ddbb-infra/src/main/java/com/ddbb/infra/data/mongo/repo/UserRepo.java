package com.ddbb.infra.data.mongo.repo;

import com.ddbb.internal.enums.UserType;
import com.ddbb.infra.data.mongo.MongoBaseRepository;
import com.ddbb.infra.data.mongo.entity.UserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 db.user.createIndex({"uid":1},{"name":"uk_uid",unique: true,"background":true})
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
     * 根据uid查人
     * @param uid
     * @return
     */
    public UserEntity findByUid(Long uid){
        Criteria criteria = Criteria.where("uid").is(uid);
        return findOne(criteria);
    }

    public Map<Long, UserEntity> findByUids(Set<Long> uids) {
        Criteria criteria = Criteria.where("uid").in(uids);
        List<UserEntity> list = findAll(criteria);
        return Optional.of(list).orElse(new ArrayList<>()).stream().collect(Collectors.toMap(UserEntity::getUid, Function.identity()));
    }

    /**
     * 获取用户类型
     * @param uid
     * @return
     */
    public UserType getUserType(Long uid){
        UserEntity entity = findByUid(uid);
        if(entity == null){
            return null;
        }
        return UserType.of(entity.getUserType());
    }
    /**
     * 根据手机号查人
     * @param phone
     * @return
     */
    public UserEntity findByPhone(String phone){
        Criteria criteria = Criteria.where("phone").is(phone);
        return findOne(criteria);
    }

    /**
     * 明星助教
     * @return
     */
    public List<UserEntity> getStarCoach(){
        Criteria criteria = Criteria.where("userType").is(UserType.ASSISTANT_COACH.getCode()).and("starLevel").gt(0);
        return findAll(criteria);
    }

    /**
     * 根据uid更新，为null的值不更新
     * @param entity
     */
    public void updateByUidWithoutNull(UserEntity entity){
        updateByUid(entity,false);
    }
    /**
     * 根据uid更新，为null的值也更新
     * @param entity
     */
    public void updateByUidWithNull(UserEntity entity){
        updateByUid(entity,true);
    }

    /**
     * 根据uid更新
     * @param entity
     * @param withNull    为null的值是否更新
     */
    private void updateByUid(UserEntity entity, boolean withNull){
        Criteria criteria = Criteria.where("uid").is(entity.getUid());
        Query q = new Query(criteria);

        Update u = new Update().set("uid",entity.getUid());
        if(withNull || StringUtils.isNotBlank(entity.getPhone())){
            u.set("phone",entity.getPhone());
        }
        if(withNull || StringUtils.isNotBlank(entity.getNickname())){
            u.set("nickname",entity.getNickname());
        }
        if(withNull || entity.getAge()!=null){
            u.set("age",entity.getAge());
        }
        if(withNull || entity.getGender()!=null){
            u.set("gender",entity.getGender());
        }
        if(withNull || StringUtils.isNotBlank(entity.getAvatar())){
            u.set("avatar",entity.getAvatar());
        }
        if(withNull || !CollectionUtils.isEmpty(entity.getPhoto())){
            u.set("photo",entity.getPhoto());
        }
        if(withNull || StringUtils.isNotBlank(entity.getCityCode())){
            u.set("cityCode",entity.getCityCode());
        }
        if(withNull || StringUtils.isNotBlank(entity.getCityName())){
            u.set("cityName",entity.getCityName());
        }
        if(withNull || StringUtils.isNotBlank(entity.getHometown())){
            u.set("hometown",entity.getHometown());
        }
        if(withNull || StringUtils.isNotBlank(entity.getHobby())){
            u.set("hobby",entity.getHobby());
        }
        if(withNull || StringUtils.isNotBlank(entity.getIntro())){
            u.set("intro",entity.getIntro());
        }
        if(withNull || (null != entity.getCoordinate() && entity.getCoordinate().length == 2)){
            u.set("coordinate",entity.getCoordinate());
        }
        if(withNull || entity.getDan()!=null){
            u.set("dan",entity.getDan());
        }
        if(withNull || entity.getLevel()!=null){
            u.set("level",entity.getLevel());
        }
        if(withNull || StringUtils.isNotBlank(entity.getLevelDesc())){
            u.set("levelDesc",entity.getLevelDesc());
        }
        if(withNull || entity.getScore()!=null){
            u.set("score",entity.getScore());
        }
        if(withNull || entity.getPrice()!=null){
            u.set("price",entity.getPrice());
        }
        if(withNull || StringUtils.isNotBlank(entity.getWorkWeekDay())){
            u.set("workWeekDay",entity.getWorkWeekDay());
        }
        if(withNull || entity.getWorkTimeStart()!=null){
            u.set("workTimeStart",entity.getWorkTimeStart());
        }
        if(withNull || entity.getWorkTimeEnd()!=null){
            u.set("workTimeEnd",entity.getWorkTimeEnd());
        }
        if(withNull || entity.getUserType()!=null){
            u.set("userType",entity.getUserType());
        }
        if(withNull || entity.getJobType()!=null){
            u.set("jobType",entity.getJobType());
        }
        if(withNull || entity.getWorkHallId()!=null){
            u.set("workHallId",entity.getWorkHallId());
        }

        mongoTemplate.updateMulti(q,u,getCollectionName());
    }
}
