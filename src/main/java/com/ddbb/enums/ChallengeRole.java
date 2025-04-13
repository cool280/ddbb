package com.ddbb.enums;

public enum ChallengeRole {
    LAUNCH(1,"发起方"),
    RECEIVE(2,"接受方"),
    ;

    private int code;
    private String desc;

    private ChallengeRole(int code, String desc){
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
