package com.ddbb.infra.data.mongo;

import com.ddbb.infra.data.mongo.entity.MongoEntity;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;

import java.lang.reflect.ParameterizedType;

public abstract class MongoBaseGeoRepository<T extends MongoEntity> extends MongoBaseRepository<T> {

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
