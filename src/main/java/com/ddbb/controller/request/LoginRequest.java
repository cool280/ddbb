package com.ddbb.controller.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class LoginRequest extends BaseRequest implements Serializable {
    private String phone;
    private String verifyCode;
}
