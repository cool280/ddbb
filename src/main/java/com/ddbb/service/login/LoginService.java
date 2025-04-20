package com.ddbb.service.login;

import com.ddbb.controller.response.LoginResponse;
import com.ddbb.enums.UserType;
import com.ddbb.mongo.entity.LoginVerifyCodeEntity;
import com.ddbb.mongo.entity.UserEntity;
import com.ddbb.mongo.repo.LoginVerifyCodeRepo;
import com.ddbb.mongo.repo.UserRepo;
import com.ddbb.sms.Sioo;
import com.ddbb.utils.DateUtilPlus;
import com.ddbb.utils.ProfileGenerator;
import com.ddbb.utils.SnowflakeIdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import com.ddbb.controller.BaseResult;

@Service
public class LoginService {
    @Autowired
    private Sioo sioo;
    @Autowired
    private LoginVerifyCodeRepo loginVerifyCodeRepo;
    @Autowired
    private UserRepo userRepo;

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
        sioo.sendMsg("您的验证码为："+verifyCode+"，5分钟内有效",phone);
        return BaseResult.OK("验证码已发送到您的手机！");
    }

    /**
     * 登录
     * @param phone
     * @param verifyCode
     * @return
     * @throws Exception
     */
    public LoginResponse doLogin(String phone,String verifyCode) throws Exception {
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

        return LoginResponse.builder().resultCode(0).msg("ok").newUser(isNewUser).qid(user.getQid())
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
        Long qid = SnowflakeIdUtil.getInstance().nextId();
        user.setQid(qid);
        user.setAid(qid);
        user.setPhone(phone);
        user.setNickname(ProfileGenerator.getNickname());
        user.setAvatar(ProfileGenerator.getAvatar());
        user.setUserType(UserType.COMMON_USER.getCode());
        user.setGender(0);

        userRepo.insert(user);
        return user;
    }
}
