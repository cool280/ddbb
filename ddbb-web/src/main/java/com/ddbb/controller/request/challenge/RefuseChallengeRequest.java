package com.ddbb.controller.request.challenge;

import com.ddbb.controller.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RefuseChallengeRequest extends BaseRequest implements Serializable {
    @ApiModelProperty(value = "助教uid",required = true)
    private Long uid;
    @ApiModelProperty(value = "挑战书id",required = true)
    private String challengeId;
}
