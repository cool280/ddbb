package com.ddbb.enums;

public enum ChallengeStatus {
    INIT(1,"发起方发起"),
    TO_REFUSED(2,"接受方拒绝"),
    TO_ACCEPTED(3,"接受方接受"),
    FROM_CANCELED(4,"发起方取消"),
    FROM_SIGN_IN(5,"发起方签到"),
    TO_SIGN_IN(6,"接受方签到"),
    ;

    private int code;
    private String desc;

    private ChallengeStatus(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
