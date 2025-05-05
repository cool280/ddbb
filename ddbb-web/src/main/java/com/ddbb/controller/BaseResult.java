package com.ddbb.controller;


import lombok.Data;

import java.io.Serializable;
@Data
public class BaseResult<T> implements Serializable {
    private int resultCode;
    private String msg;
    private T data;

    public BaseResult(int resultCode){
        this.resultCode = resultCode;
    }

    public BaseResult(int resultCode,String msg){
        this.resultCode = resultCode;
        this.msg = msg;
    }

    public BaseResult(int resultCode,String msg,T data){
        this.resultCode = resultCode;
        this.msg = msg;
        this.data = data;
    }

    private static BaseResult SIMPLE_OK = new BaseResult(0,"ok");
    private static BaseResult SIMPLE_ERROR = new BaseResult(-1,"error");

    public static BaseResult OK(){
        return SIMPLE_OK;
    }
    public static BaseResult OK(String msg) {
        return new BaseResult(0, msg);
    }

    public static BaseResult ERROR(){
        return SIMPLE_ERROR;
    }

    public static BaseResult ERROR(String errMsg){
        return new BaseResult(-1,errMsg);
    }
    public static BaseResult ERROR(Throwable e){
        return ERROR(e.getMessage());
    }
}
