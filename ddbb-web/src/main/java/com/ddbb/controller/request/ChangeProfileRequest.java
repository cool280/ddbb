package com.ddbb.controller.request;

import com.ddbb.infra.data.mongo.entity.MongoEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
public class ChangeProfileRequest implements Serializable {
    @ApiModelProperty(value = "qid",required = true)
    private Long qid;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "昵称")
    private String nickname;
    @ApiModelProperty(value = "年龄")
    private Integer age;
    @ApiModelProperty(value = "性别")
    private Integer gender;
    @ApiModelProperty(value = "所在城市code")
    private String cityCode;
    @ApiModelProperty(value = "所在城市name")
    private String cityName;
    @ApiModelProperty(value = "家乡")
    private String hometown;
    @ApiModelProperty(value = "爱好")
    private String hobby;
    @ApiModelProperty(value = "个人简介")
    private String intro;
    @ApiModelProperty(value = "台球挡位")
    private Integer dan;
    @ApiModelProperty(value = "级别：1-5分别对应：实习、初级、中级、高级、星级")
    private Integer level;
    @ApiModelProperty(value = "级别描述：1-5分别对应：实习、初级、中级、高级、星级")
    private String levelDesc;
    @ApiModelProperty(value = "助教评分")
    private Double score;
    @ApiModelProperty(value = "助教价格  元/时")
    private Double price;
    @ApiModelProperty(value = "周几可约，1 - 7，分别表示周1 - 周日，用逗号分隔")
    private String workWeekDay;
    @ApiModelProperty(value = "每天可约开始时间",example = "14")
    private Integer workTimeStart;
    @ApiModelProperty(value = "每天可约结束时间",example = "23")
    private Integer workTimeEnd;
    @ApiModelProperty(value = "用户类型：1 - 助教，2 - 普通用户")
    private Integer userType;

}
