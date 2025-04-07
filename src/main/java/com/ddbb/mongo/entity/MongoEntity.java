package com.ddbb.mongo.entity;

import lombok.Data;

@Data
public class MongoEntity {
    protected Long id;
    protected Long createTime;
    protected Long updateTime;
}
