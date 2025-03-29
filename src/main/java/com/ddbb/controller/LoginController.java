package com.ddbb.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.annotate.DdbbController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@DdbbController
@RequestMapping("/login")
public class LoginController extends BaseController{
    @PostMapping("/sendSmsCode")
    @ResponseBody
    public JSONObject sendSmsCode(String mobile){
        return SUCCESS;
    }
    @PostMapping("/doLogin")
    @ResponseBody
    public JSONObject login(String mobile,String vCode){
        return SUCCESS;
    }

}
