package com.ddbb.controller.request;

import com.ddbb.internal.utils.MD5Util;
import lombok.Data;

import java.io.Serializable;

@Data
public class CoachRegisterRequest extends BaseRequest implements Serializable {
    private String phone;
    private String verifyCode;


    public static void main(String[] args) {
        Long uid = 65143423516446720L;
        for(int i=0;i<10;i++){
            System.out.println(uid+" ====> "+MD5Util.encode(uid.toString()));
            uid = uid.longValue()+1;
        }

        System.out.println("==========================================================");
        uid = 0L;
        for(int i=0;i<10;i++){
            System.out.println(uid+" ====> "+MD5Util.encode(uid.toString()));
            uid = uid.longValue()+1;
        }
    }
}
