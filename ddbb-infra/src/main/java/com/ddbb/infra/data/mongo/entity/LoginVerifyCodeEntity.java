package com.ddbb.infra.data.mongo.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Data
public class LoginVerifyCodeEntity extends MongoEntity implements Serializable {
    private String phone;
    private String verifyCode;
    @Field("createTime")
    private Date creatTime;

}
