package com.ddbb.mongo.entity;

import lombok.Data;

@Data
public class MongoEntity {
    /**
     * 自增id
     */
    protected Long aid;
    protected Long createTime;
    protected Long updateTime;
}
