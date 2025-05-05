package com.ddbb.controller.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
@Data
@Builder
public class LoginResponse extends BaseResponse implements Serializable {

    @ApiModelProperty(value = "是否为新注册用户")
    private boolean newUser;
    @ApiModelProperty(value = "qid")
    private Long qid;
    @ApiModelProperty(hidden = true)
    private int resultCode;
    @ApiModelProperty(hidden = true)
    private String msg;
    @ApiModelProperty(value = "用户类型，球友还是助教")
    private int userType;
}
