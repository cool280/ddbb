package com.ddbb.controller.request.challenge;

import com.ddbb.controller.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CancelChallengeRequest extends BaseRequest implements Serializable {
    @ApiModelProperty(value = "谁取消的",required = true)
    private Long uid;
    @ApiModelProperty(value = "挑战书id",required = true)
    private String challengeId;
}
