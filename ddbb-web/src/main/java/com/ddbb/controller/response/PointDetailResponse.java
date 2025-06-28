package com.ddbb.controller.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PointDetailResponse implements Serializable {

    @ApiModelProperty(value = "积分类型")
    private int pointType;

    @ApiModelProperty(value = "积分名称")
    private String pointName;

    @ApiModelProperty(value = "积分获取值")
    private int point;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
