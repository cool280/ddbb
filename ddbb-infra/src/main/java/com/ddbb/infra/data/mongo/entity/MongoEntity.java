package com.ddbb.infra.data.mongo.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
public class MongoEntity {
    /**
     * 自增id
     */
    @Indexed
    protected Long aid;
}
