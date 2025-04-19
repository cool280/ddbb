package com.ddbb.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.annotate.DdbbController;
import com.ddbb.controller.request.ChallengeRequest;
import com.ddbb.controller.request.LoginRequest;
import com.ddbb.controller.response.LoginResponse;
import com.ddbb.mongo.entity.LoginVerifyCodeEntity;
import com.ddbb.mongo.repo.LoginVerifyCodeRepo;
import com.ddbb.service.login.LoginService;
import com.ddbb.sms.Sioo;
import com.ddbb.utils.DateUtilPlus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@DdbbController
@RequestMapping("/login")
public class LoginController extends BaseController{

    private static final Pattern pattern = Pattern.compile("^1[3-9]\\d{9}$");
    @Autowired
    private LoginService loginService;


    @PostMapping("/sendSmsCode")
    @ResponseBody
    public BaseResult sendSmsCode(@RequestBody LoginRequest request){
        String phone = request.getPhone();
        if(StringUtils.isBlank(phone)){
            return ERROR("phone is null");
        }
        Matcher matcher = pattern.matcher(phone);

        if (!matcher.matches()) {
            return ERROR("请输入正确格式的手机号");
        }

        try {
            return loginService.sendSmsCode(phone);
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR("验证码发送失败，请稍后重试!");
        }
    }
    @PostMapping("/doLogin")
    @ResponseBody
    public BaseResult login(@RequestBody LoginRequest request){
        String phone = request.getPhone();
        if(StringUtils.isBlank(phone)){
            return ERROR("phone is null");
        }
        Matcher matcher = pattern.matcher(phone);

        if (!matcher.matches()) {
            return ERROR("请输入正确格式的手机号");
        }

        String verifyCode = request.getVerifyCode();
        if(StringUtils.isBlank(verifyCode)){
            return ERROR("请输入验证码");
        }
        try {
            LoginResponse resp = loginService.doLogin(phone,verifyCode);
            if(resp.getResultCode()!=0){
                return ERROR(resp.getMsg());
            }

            JSONObject json = new JSONObject();
            json.put("isNewUser",resp.isNewUser());
            json.put("qid",resp.getQid());

            return OK(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR("登录失败，请稍后重试!");
        }
    }

}
