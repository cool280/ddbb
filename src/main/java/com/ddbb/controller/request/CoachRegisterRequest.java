package com.ddbb.controller.request;

import com.ddbb.utils.MD5Util;
import lombok.Data;

import java.io.Serializable;

@Data
public class CoachRegisterRequest extends BaseRequest implements Serializable {
    private String phone;
    private String verifyCode;


    public static void main(String[] args) {
        Long qid = 65143423516446720L;
        for(int i=0;i<10;i++){
            System.out.println(qid+" ====> "+MD5Util.encode(qid.toString()));
            qid = qid.longValue()+1;
        }

        System.out.println("==========================================================");
        qid = 0L;
        for(int i=0;i<10;i++){
            System.out.println(qid+" ====> "+MD5Util.encode(qid.toString()));
            qid = qid.longValue()+1;
        }
    }
}
