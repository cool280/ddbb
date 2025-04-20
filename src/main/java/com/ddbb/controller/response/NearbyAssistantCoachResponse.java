package com.ddbb.controller.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NearbyAssistantCoachResponse extends BaseResponse implements Serializable, IWithPhotoVO {
    private Long qid;
    private String nickname;
    private Integer age;
    private String avatar;
    private List<String> photo;
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

    private Double distanceKm;
}
