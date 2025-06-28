package com.ddbb.controller.response.point;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PointDetailResponse implements Serializable {

    @ApiModelProperty(value = "积分类型")
    private int pointActionType;

    @ApiModelProperty(value = "积分名称")
    private String pointActionTypeName;

    @ApiModelProperty(value = "积分获取值")
    private int point;

    @ApiModelProperty(value = "创建时间")
    private Long cts;
}
