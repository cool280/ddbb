package com.ddbb.controller.request.challenge;

import com.ddbb.controller.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SaveScoreRequest extends BaseRequest implements Serializable {
    @ApiModelProperty(value = "球友或助教uid",required = true)
    private Long uid;
    @ApiModelProperty(value = "挑战书id",required = true)
    private String challengeId;
    @ApiModelProperty(value = "发起方（球友）胜几局",required = true)
    private Integer fromWinRound;
    @ApiModelProperty(value = "接受方（助教）胜几局",required = true)
    private Integer toWinRound;
}
