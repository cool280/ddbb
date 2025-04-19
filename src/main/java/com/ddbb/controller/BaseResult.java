package com.ddbb.controller;


import lombok.Data;

import java.io.Serializable;
@Data
public class BaseResult implements Serializable {
    private int resultCode;
    private String msg;
    private Object data;

    public BaseResult(int resultCode){
        this.resultCode = resultCode;
    }

    public BaseResult(int resultCode,String msg){
        this.resultCode = resultCode;
        this.msg = msg;
    }

    public BaseResult(int resultCode,String msg,Object data){
        this.resultCode = resultCode;
        this.msg = msg;
        this.data = data;
    }

    public static BaseResult OK(){
        return new BaseResult(0);
    }
    public static BaseResult OK(String msg) {
        return new BaseResult(0, msg);
    }

    public static BaseResult ERROR(){
        return new BaseResult(-1);
    }

    public static BaseResult ERROR(String errMsg){
        return new BaseResult(-1,errMsg);
    }
}
