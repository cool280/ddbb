package com.ddbb.controller;


import com.alibaba.fastjson.JSONObject;

public class BaseController {
    public static JSONObject SUCCESS = new JSONObject();
    public static JSONObject FAIL = new JSONObject();
    static {
        SUCCESS.put("resultCode",0);
        SUCCESS.put("msg","ok");

        FAIL.put("resultCode",-1);
        FAIL.put("msg","system error");
    }

    public static JSONObject buildOk(){
        return buildOk(null);
    }
    public static JSONObject buildOk(Object data){
        return buildOk(data,"ok");
    }

    public static JSONObject buildOk(Object data,String msg){
        JSONObject json = new JSONObject();
        json.put("resultCode",0);
        json.put("msg",msg);
        json.put("data",data);
        return json;
    }
    public static JSONObject buildResult(int code){
        return buildResult(code,"");
    }
    public static JSONObject buildResult(int code,String msg){
        return buildResult(code,null,msg);
    }
    public static JSONObject buildResult(int code,Object data,String msg){
        JSONObject json = new JSONObject();
        json.put("resultCode",code);
        json.put("msg",msg);
        json.put("data",data);
        return json;
    }

    public static JSONObject buildError(Throwable e){
        JSONObject json = new JSONObject();
        json.put("resultCode",-1);
        json.put("msg",e.getMessage());
        return json;
    }
}
