package com.ddbb.mongo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
public class Challenge extends MongoEntity implements Serializable {
    /**
     * 一张挑战书生成两条记录，owner分别 = from和to，其他字段都一样
     */
    private Long owner;
    /**
     * 挑战书id
     */
    private String challengeId;
    private Long from;
    private Long to;
    /**
     * 球房id
     */
    private Long hallId;
    private String challengeDateStr;
    private Integer challengeDateInt;
    private Integer startTime;
    private Integer endTime;
    private Integer status;
    /**
     * 动作：1 - 发起，2 - 接受
     */
    private Integer challengeRole;

}
