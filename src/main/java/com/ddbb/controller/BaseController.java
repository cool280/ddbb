package com.ddbb.controller;


import com.alibaba.fastjson.JSONObject;

public class BaseController {
    public static JSONObject SUCCESS = new JSONObject();
    static {
        SUCCESS.put("resultCode",0);
        SUCCESS.put("msg","ok");
    }

    public static JSONObject buildOk(Object data,String msg){
        JSONObject json = new JSONObject();
        json.put("resultCode",0);
        json.put("msg",msg);
        json.put("data",data);
        return json;
    }
}
