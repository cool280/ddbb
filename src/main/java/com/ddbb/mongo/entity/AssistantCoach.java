package com.ddbb.mongo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AssistantCoach extends MongoEntity implements Serializable {
    private Long qid;
    private String nickname;
    private Integer age;
    private String avatar;
    private String cityCode;
    private String cityName;
    private String hometown;
    private String hobby;
    private String intro;
    private Double[] coordinate;
    /**
     * 台球挡位
     */
    private Integer dan;
    /**
     * 级别：1-5分别对应：实习、初级、中级、高级、星级
     */
    private Integer level;
    private String levelDesc;
    /**
     * 助教评分
     */
    private Double score;
    /**
     * 助教价格  元/时
     */
    private Double price;
}
