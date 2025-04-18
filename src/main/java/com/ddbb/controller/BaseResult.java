package com.ddbb.controller;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseResult implements Serializable {
    private int resultCode;
    private String msg;
    private Object data;

    public BaseResult(int resultCode,String msg){
        this.resultCode = resultCode;
        this.msg = msg;
    }
}
