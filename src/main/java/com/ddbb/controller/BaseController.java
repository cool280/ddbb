package com.ddbb.controller;


import com.alibaba.fastjson.JSONObject;

public class BaseController {
    public static JSONObject OK = OK();
    public static JSONObject ERROR = ERROR();


    public static JSONObject OK(){
        return OK(null);
    }
    public static JSONObject OK(Object data){
        return OK(data,"ok");
    }
    public static JSONObject OK(Object data, String msg){
        JSONObject json = new JSONObject();
        json.put("resultCode",0);
        json.put("msg",msg);
        json.put("data",data);
        return json;
    }

    public static JSONObject RESULT(int code){
        return RESULT(code,"");
    }
    public static JSONObject RESULT(int code, String msg){
        return RESULT(code,null,msg);
    }
    public static JSONObject RESULT(int code, Object data, String msg){
        JSONObject json = new JSONObject();
        json.put("resultCode",code);
        json.put("msg",msg);
        json.put("data",data);
        return json;
    }
    public static JSONObject ERROR(){
        return ERROR("");
    }
    public static JSONObject ERROR(Throwable e){
        return ERROR(e.getMessage());
    }
    public static JSONObject ERROR(String err){
        JSONObject json = new JSONObject();
        json.put("resultCode",-1);
        json.put("msg",err);
        return json;
    }
}
