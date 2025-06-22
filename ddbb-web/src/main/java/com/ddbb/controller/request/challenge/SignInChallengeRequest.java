package com.ddbb.controller.request.challenge;

import com.ddbb.controller.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SignInChallengeRequest extends BaseRequest implements Serializable {
    @ApiModelProperty(value = "签到的uid",required = true)
    private Long uid;
    @ApiModelProperty(value = "挑战书id",required = true)
    private String challengeId;
    @ApiModelProperty(value = "经度",required = true)
    private String longitude;
    @ApiModelProperty(value = "纬度",required = true)
    private String latitude;
}
