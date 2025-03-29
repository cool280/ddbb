package com.ddbb.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class MongoBaseRepository<T> {
    @Autowired
    protected MongoTemplate mongoTemplate;

    public abstract String getCollectionName();

    public void insert(T object){
        mongoTemplate.insert(object,getCollectionName());
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
     * db.user.createIndex({"coordinate":"2dsphere"},{"name":"idx_coordinate_2dsphere","background":true})
     * @param context
     * @return
     */
    public GeoResults<T> geoQuery(GeoQueryContext context){
        NearQuery near = NearQuery.near(context.getLongitude(),context.getLatitude())
                .spherical(true)
                .minDistance(context.getMinDistanceKm(), Metrics.KILOMETERS)
                .maxDistance(context.getMaxDistanceKm(), Metrics.KILOMETERS)
                .limit(context.getMaxNum());
        if(context.getCriteria()!=null){
            Query query = new Query(context.getCriteria());
            //set query, not execute
            near.query(query);
        }

        Class<T> clazz = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return mongoTemplate.geoNear(near,clazz,getCollectionName());
    }
}
