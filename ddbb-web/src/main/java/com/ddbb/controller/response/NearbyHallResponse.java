package com.ddbb.controller.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NearbyHallResponse extends BaseResponse implements Serializable, IWithPhotoVO {
    @ApiModelProperty(value = "球房id",required = true)
    private Long hallId;
    @ApiModelProperty(value = "球房名称",required = true)
    private String nickname;
    @ApiModelProperty(value = "教练数量",required = true)
    private Integer coachCount;
    @ApiModelProperty(value = "球房头像",required = true)
    private String avatar;
    @ApiModelProperty(value = "球房照片",required = false,example = "https://www.ddbb365.com/ddbb_u_images/1.jpg,https://www.ddbb365.com/ddbb_u_images/2.jpg")
    private List<String> photo;
    @ApiModelProperty(value = "所在城市code",required = false)
    private String cityCode;
    @ApiModelProperty(value = "所在城市name",required = true)
    private String cityName;
    @ApiModelProperty(value = "球房评分",required = true)
    private Double score;
    @ApiModelProperty(value = "中八价格  元/时")
    private Double tablePrice;
    @ApiModelProperty(value = "斯诺克价格   元/时")
    private Double snookerPrice;
    @ApiModelProperty(value = "包厢价格   元/时")
    private Double vipRoomPrice;
    @ApiModelProperty(value = "地址",required = true)
    private String address;
    @ApiModelProperty(value = "电话")
    private String tel;
    @ApiModelProperty(value = "网站")
    private String website;
    @ApiModelProperty(value = "面积")
    private Integer area;
    @ApiModelProperty(value = "中八台子数量")
    private Integer tableCount;
    @ApiModelProperty(value = "斯诺克台子数量")
    private Integer snookerCount;
    @ApiModelProperty(value = "vip包房数量")
    private Integer vipRoomCount;
    @ApiModelProperty(value = "营业时间",example = "10:00 - 2:00")
    private String workTime;
    @ApiModelProperty(value = "乘车路线",example = "地铁12号线宁国路站")
    private String wayTo;
    @ApiModelProperty(value = "自驾停车优惠",example = "免费停车4小时")
    private String parkingDiscount;
    @ApiModelProperty(value = "距离km")
    private Double distanceKm;

}
