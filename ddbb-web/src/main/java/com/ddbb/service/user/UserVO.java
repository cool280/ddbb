package com.ddbb.service.user;

import lombok.Data;

/**
 * 放一些能够给前端返回的用户信息
 */
@Data
public class UserVO {
    private Long uid;
    private String nickname;
    private Integer age;
    private String avatar;
    private String cityCode;
    private String cityName;
    private String hometown;
    private String hobby;
    private String intro;
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
}
