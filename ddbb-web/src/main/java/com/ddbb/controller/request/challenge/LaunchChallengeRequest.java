package com.ddbb.controller.request.challenge;

import com.ddbb.controller.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class LaunchChallengeRequest extends BaseRequest implements Serializable {
    @ApiModelProperty(value = "挑战发起人 - 球友uid",required = true)
    private Long from;
    @ApiModelProperty(value = "挑战接收人（助教uid）",required = true)
    private Long to;
    @ApiModelProperty(value = "球房id",required = true)
    private Long hallId;
    @ApiModelProperty(value = "挑战日期yyyy-MM-dd",required = true)
    private String challengeDateStr;
    @ApiModelProperty(value = "预约开始时间：预约起止时间必须连续且不能跨天",required = true,example = "17")
    private Integer startTime;
    @ApiModelProperty(value = "预约终止时间：预约起止时间必须连续且不能跨天",required = true,example = "19")
    private Integer endTime;
}
