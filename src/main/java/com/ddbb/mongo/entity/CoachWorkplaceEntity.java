package com.ddbb.mongo.entity;

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
    private Long qid;
    /**
     * 可出台球房id
     */
    private Long hallId;
    /**
     * 球房id的坐标
     */
    private Double[] coordinate;
    /**
     * 是否工作在这个球房
     */
    private Integer workHere;
}
