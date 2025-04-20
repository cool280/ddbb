package com.ddbb.controller.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
@Data
@Builder
public class LoginResponse extends BaseResponse implements Serializable {
    private boolean newUser;
    private Long qid;
    private int resultCode;
    private String msg;
    private int userType;
}
