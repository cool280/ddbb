package com.ddbb.controller;


import com.alibaba.fastjson.JSONObject;

public class BaseController {
    public static JSONObject SUCCESS = new JSONObject();
    static {
        SUCCESS.put("resultCode",0);
        SUCCESS.put("msg","ok");
    }
}
