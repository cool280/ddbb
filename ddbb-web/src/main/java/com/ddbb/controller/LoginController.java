package com.ddbb.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.internal.annotate.DdbbController;
import com.ddbb.controller.request.CoachRegisterRequest;
import com.ddbb.controller.request.LoginRequest;
import com.ddbb.controller.response.LoginResponse;
import com.ddbb.service.login.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@DdbbController
@RequestMapping("/login")
@Api(tags = "登录、注册")
public class LoginController extends BaseController{

    private static final Pattern pattern = Pattern.compile("^1[3-9]\\d{9}$");
    @Autowired
    private LoginService loginService;


    @PostMapping("/sendSmsCode")
    @ResponseBody
    @ApiOperation(value = "发送验证码")
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
    @ApiOperation(value = "登录")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "ok", response = LoginResponse.class)})
    public BaseResult<LoginResponse> login(@RequestBody LoginRequest request){
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


            return OK(resp);
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR("登录失败，请稍后重试!");
        }
    }

    @PostMapping("/coachRegister")
    @ResponseBody
    @ApiOperation(value = "助教注册")
    public BaseResult coachRegister(@RequestBody CoachRegisterRequest request){
        return null;
    }

}
