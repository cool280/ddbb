package com.ddbb.service.login;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.controller.response.LoginResponse;
import com.ddbb.extapi.wx.Jscode2sessionResp;
import com.ddbb.extapi.wx.WxPayService;
import com.ddbb.internal.enums.UserType;
import com.ddbb.infra.data.mongo.entity.LoginVerifyCodeEntity;
import com.ddbb.infra.data.mongo.entity.UserEntity;
import com.ddbb.infra.data.mongo.repo.LoginVerifyCodeRepo;
import com.ddbb.infra.data.mongo.repo.UserRepo;
import com.ddbb.extapi.sms.Sioo;
import com.ddbb.internal.utils.DateUtilPlus;
import com.ddbb.internal.utils.ProfileGenerator;
import com.ddbb.internal.utils.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import com.ddbb.controller.BaseResult;

@Service
@Slf4j
public class LoginService {
    @Autowired
    private Sioo sioo;
    @Autowired
    private LoginVerifyCodeRepo loginVerifyCodeRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private WxPayService wxPayService;

    /**
     * 发验证码
     * @param phone
     * @return
     * @throws Exception
     */
    public BaseResult sendSmsCode(String phone) throws Exception {
        LoginVerifyCodeEntity entity = loginVerifyCodeRepo.findByPhone(phone);
        if(entity!=null){
            LocalDateTime last = DateUtilPlus.utilDate2LocalDateTime(entity.getCreatTime());
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(last,now);
            if(Math.abs(duration.getSeconds()) <= 120){
                return BaseResult.ERROR("您发送验证码太频繁");
            }
        }

        int i = ThreadLocalRandom.current().nextInt(1000,10000);
        String verifyCode = String.valueOf(i);
        loginVerifyCodeRepo.upsert(phone,verifyCode);
        JSONObject jsonObject = sioo.sendMsg("您的验证码为：" + verifyCode + "，5分钟内有效", phone);
        log.info("send result: "+jsonObject.toJSONString());
        return BaseResult.OK("验证码已发送到您的手机！");
    }

    /**
     * 登录
     * @param phone
     * @param verifyCode
     * @return
     * @throws Exception
     */
    public LoginResponse doLogin(String phone,String verifyCode,String jsCode) throws Exception {
        LoginVerifyCodeEntity entity = loginVerifyCodeRepo.findByPhone(phone);

        if(entity==null || StringUtils.isBlank(entity.getVerifyCode())) {
            return LoginResponse.builder().resultCode(-1).msg("验证码过期，请重新发送验证码").build();
        }
        String correct = entity.getVerifyCode();
        if(!correct.equals(verifyCode)){
            return LoginResponse.builder().resultCode(-1).msg("验证码错误").build();
        }
        UserEntity user = userRepo.findByPhone(phone);
        boolean isNewUser = false;
        if(user == null){
            isNewUser = true;
            user = createCommonUser(phone);
        }
        final UserEntity user4Update = user;

        //获取wx的openid
        if(StringUtils.isBlank(user.getWxOpenId())){
            new Thread(
                    ()->{
                        Jscode2sessionResp resp = wxPayService.jscode2session(jsCode);
                        if(resp!=null){
                            user4Update.setWxOpenId(resp.getOpenId());
                            user4Update.setWxUnionId(resp.getUnionId());
                            userRepo.updateByUidWithoutNull(user4Update);
                        }
                    }
            ).start();
        }

        return LoginResponse.builder().resultCode(0).msg("ok").newUser(isNewUser).uid(user.getUid())
                .userType(user.getUserType())
                .build();
    }

    /**
     * 创建普通用户
     * @param phone
     * @return
     */
    private UserEntity createCommonUser(String phone){
        UserEntity user = new UserEntity();
        Long uid = SnowflakeIdUtil.getInstance().nextId();
        user.setUid(uid);
        user.setAid(uid);
        user.setPhone(phone);
        user.setNickname(ProfileGenerator.getNickname());
        user.setAvatar(ProfileGenerator.getAvatar());
        user.setUserType(UserType.COMMON_USER.getCode());
        user.setGender(0);

        userRepo.insert(user);
        return user;
    }
}
