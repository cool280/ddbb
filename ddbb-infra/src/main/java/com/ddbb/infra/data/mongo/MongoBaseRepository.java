package com.ddbb.infra.data.mongo;

import com.ddbb.infra.data.mongo.entity.MongoEntity;
import lombok.Data;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class MongoBaseRepository<T extends MongoEntity> {
    @Resource
    protected MongoTemplate mongoTemplate;

    private static Map<String,PageingMinAid> PAGEING_QUERY_ID_MAP = new HashMap<>();

    public abstract String getCollectionName();

    public void insert(T object){
        mongoTemplate.insert(object,getCollectionName());
    }
    public void delete(Criteria criteria){
        delete(new Query(criteria));
    }
    public void delete(Query query){
        mongoTemplate.remove(query,getCollectionName());
    }

    public T findOne(Criteria criteria){
        Query query = new Query();
        query.addCriteria(criteria);
        return findOne(query);
    }

    public T findOne(Query query){
        Class<T> clazz = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return mongoTemplate.findOne(query,clazz,getCollectionName());
    }

    public List<T> findAll(Criteria criteria){
        Query query = new Query();
        query.addCriteria(criteria);
        return findAll(query);
    }

    public List<T> findAll(Query query){
        Class<T> clazz = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return mongoTemplate.find(query,clazz,getCollectionName());
    }

    /**
     * 分页查询
     * @param queryId   每个查询的编号，每次分页查询都要携带过来同一个id。可用时间戳、uuid、雪花id
     * @param limit
     * @return
     */
    public List<T> pagingQuery(String queryId,int limit){
        Long minAid = Optional.ofNullable(PAGEING_QUERY_ID_MAP.get(queryId))
                .orElseGet(()->new PageingMinAid(-1L,System.currentTimeMillis()))
                .getMinAid();
        List<T> ret = pagingQuery(minAid,limit);

        if(CollectionUtils.isEmpty(ret)){
            PAGEING_QUERY_ID_MAP.remove(queryId);
        }else{
            minAid = ret.get(ret.size() - 1).getAid();
            PAGEING_QUERY_ID_MAP.put(queryId,new PageingMinAid(minAid,System.currentTimeMillis()));
        }

        return ret;
    }

    /**
     * 分页查询
     * @param minAid
     * @param limit
     * @return
     */
    private List<T> pagingQuery(Long minAid,int limit){
        Class<T> clazz = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Criteria criteria = Criteria.where("aid").gt(minAid);

        // 升序排序
        Query query = new Query(criteria).with(Sort.by(Sort.Direction.ASC, "aid")).limit(limit);

        return mongoTemplate.find(query,clazz,getCollectionName());
    }

    @Data
    static
    class PageingMinAid{
        private Long minAid;
        private Long createTs;

        public PageingMinAid(Long minAid,Long createTs){
            this.minAid = minAid;
            this.createTs = createTs;
        }
    }
}
