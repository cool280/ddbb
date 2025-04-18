package com.ddbb.controller;


import com.alibaba.fastjson.JSONObject;

public class BaseController {
    public final static BaseResult OK = OK();
    public final static BaseResult ERROR = ERROR();


    public final static BaseResult OK(){
        return OK(null);
    }
    public final static BaseResult OK(Object data){
        return OK(data,"ok");
    }
    public final static BaseResult OK(Object data, String msg){
        return new BaseResult(0,msg,data);
    }

    public final static BaseResult RESULT(int code){
        return RESULT(code,"");
    }
    public final static BaseResult RESULT(int code, String msg){
        return RESULT(code,null,msg);
    }
    public final static BaseResult RESULT(int code, Object data, String msg){
        return new BaseResult(code,msg,data);
    }
    public final static BaseResult ERROR(){
        return ERROR("");
    }
    public final static BaseResult ERROR(Throwable e){
        return ERROR(e.getMessage());
    }
    public final static BaseResult ERROR(String err){
        return new BaseResult(-1,err);
    }
}
