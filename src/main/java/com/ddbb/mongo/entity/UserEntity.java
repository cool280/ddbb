package com.ddbb.mongo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserEntity extends MongoEntity implements Serializable {
    private Long qid;
    private String phone;
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
    /**
     * 周几可约，1 - 7，分别表示周1 - 周日，用逗号分隔
     */
    private String workWeekDay;
    /**
     * 每天可约开始时间
     */
    private Integer workTimeStart;
    /**
     * 每天可约结束时间
     */
    private Integer workTimeEnd;
    /**
     * 用户类型：1 - 助教，2 - 普通用户
     */
    private Integer userType;
    /**
     * 工作类型：1 - 球房驻场，2 - 自由职业
     */
    private Integer jobType;
    /**
     * 如果是驻场助教，工作的球房id
     */
    private Long workHallId;
}
