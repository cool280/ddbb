package com.ddbb.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WorkplaceRequest extends BaseRequest{
    @ApiModelProperty(value = "助教id",required = true)
    private Long uid;
    @ApiModelProperty(value = "可出台球房id",required = true)
    private Long hallId;
}
