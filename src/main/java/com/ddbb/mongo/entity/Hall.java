package com.ddbb.mongo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Hall extends MongoEntity implements Serializable {
    /**
     * 球房id
     */
    private Long hallId;
    /**
     * 球房名称
     */
    private String nickname;
    /**
     * 教练数量
     */
    private Integer coachCount;
    /**
     * 球房头像
     */
    private String avatar;
    private String cityCode;
    private String cityName;
    /**
     * 球房评分
     */
    private Double score;
    /**
     * 中八价格  元/时
     */
    private Double tablePrice;
    /**
     * 斯诺克价格   元/时
     */
    private Double snookerPrice;
    /**
     * 包厢价格   元/时
     */
    private Double vipRoomPrice;

    private String address;
    private String tel;
    private String website;
    private Double[] coordinate;
    /**
     * 面积
     */
    private Integer area;

    /**
     * 中八台子数量
     */
    private Integer tableCount;
    /**
     * 斯诺克台子数量
     */
    private Integer snookerCount;
    /**
     * vip包房数量
     */
    private Integer vipRoomCount;
    /**
     * 营业时间10:00 - 2:00
     */
    private String workTime;
    /**
     * 乘车路线
     */
    private String wayTo;
    /**
     * 自驾停车优惠
     */
    private String parkingDiscount;

}
