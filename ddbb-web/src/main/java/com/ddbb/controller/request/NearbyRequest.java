package com.ddbb.controller.request;

import com.ddbb.internal.constants.DdbbConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class NearbyRequest extends BaseRequest implements Serializable {
    @ApiModelProperty(value = "请求者id",required = true)
    private Long qid;
    @ApiModelProperty(value = "经度",required = true)
    private Double longitude;
    @ApiModelProperty(value = "纬度",required = true)
    private Double latitude;
    @ApiModelProperty(value = "最小距离，默认50km",required = false)
    private Double minDistanceKm;
    @ApiModelProperty(value = "最大距离，默认20km",required = false)
    private Double maxDistanceKm;
    @ApiModelProperty(value = "查询最大数量，默认50",required = false)
    private Integer maxCount;
}
