package com.ddbb.test;

import com.ddbb.infra.data.mongo.repo.LoginVerifyCodeRepo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ThreadLocalRandom;

public class LoginTest extends BaseTest{
    @Autowired
    private LoginVerifyCodeRepo loginVerifyCodeRepo;
    @Test
    public void testUpsertVerifyCode(){
        String phone = "13472644803";
        String verifyCode = String.valueOf(ThreadLocalRandom.current().nextInt(1000,10000));
        loginVerifyCodeRepo.upsert(phone,verifyCode);
        System.out.println(" ================= testUpsertVerifyCode done =================");
    }
}
