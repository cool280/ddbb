package com.ddbb.controller.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NearbyAssistantCoachResponse extends BaseResponse implements Serializable, IWithPhotoVO {
    @ApiModelProperty(value = "助教id",required = true)
    private Long qid;
    @ApiModelProperty(value = "昵称",required = true)
    private String nickname;
    @ApiModelProperty(value = "年龄",required = true)
    private Integer age;
    @ApiModelProperty(value = "头像",required = true)
    private String avatar;
    @ApiModelProperty(value = "生活照",required = true)
    private List<String> photo;
    @ApiModelProperty(value = "所在城市code",required = false)
    private String cityCode;
    @ApiModelProperty(value = "所在城市name",required = false)
    private String cityName;
    @ApiModelProperty(value = "家乡",required = false)
    private String hometown;
    @ApiModelProperty(value = "爱好",required = false)
    private String hobby;
    @ApiModelProperty(value = "自我介绍",required = false)
    private String intro;
    @ApiModelProperty(value = "台球挡位",required = false,example = "3")
    private Integer dan;
    @ApiModelProperty(value = "级别：1-5分别对应：实习、初级、中级、高级、星级",required = false,example = "4")
    private Integer level;
    @ApiModelProperty(value = "级别描述",required = false,example = "星级")
    private String levelDesc;
    @ApiModelProperty(value = "助教评分",required = false,example = "")
    private Double score;
    @ApiModelProperty(value = "助教价格  元/时",required = true,example = "128")
    private Double price;
    @ApiModelProperty(value = "距离km",required = false,example = "")
    private Double distanceKm;
}
