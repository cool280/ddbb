package com.ddbb.controller.request.challenge;

import com.ddbb.controller.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PrepareChallengeRequest extends BaseRequest implements Serializable {
    @ApiModelProperty(value = "挑战发起人 - 球友uid")
    private Long fromUid;
    @ApiModelProperty(value = "挑战挑战接收人（助教uid）")
    private Long toUid;//挑战接收
}
