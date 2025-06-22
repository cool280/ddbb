package com.ddbb.infra.data.mongo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CoachWorkplaceEntity extends MongoEntity{
    /**
     * 助教id
     */
    private Long uid;
    /**
     * 可出台球房id
     */
    private Long hallId;
    /**
     * 球房id的坐标
     */
    private Double[] coordinate;
}
