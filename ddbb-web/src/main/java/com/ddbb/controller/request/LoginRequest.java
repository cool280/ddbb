package com.ddbb.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
public class LoginRequest extends BaseRequest implements Serializable {
    @ApiModelProperty(value = "手机号",required = true)
    private String phone;
    @ApiModelProperty(value = "验证码（发送验证码接口非必须）",required = true)
    private String verifyCode;
    @ApiModelProperty(value = "微信小程序登录的js_code",required = true)
    private String wxJsCode;
}
