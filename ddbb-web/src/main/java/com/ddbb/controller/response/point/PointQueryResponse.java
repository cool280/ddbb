package com.ddbb.controller.response.point;

import com.ddbb.controller.response.BaseResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class PointQueryResponse extends BaseResponse implements Serializable {

    @ApiModelProperty(value = "可用积分")
    private Integer availablePoints = 0;

    @ApiModelProperty(value = "积分收入")
    private Integer totalPointsEarned = 0;

    @ApiModelProperty(value = "积分消耗")
    private Integer totalPointsUsed = 0;

    private List<PointDetailResponse> details = new ArrayList<>();

}
