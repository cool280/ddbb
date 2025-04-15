package com.ddbb.enums;

public enum JobType {
    FIX_HALL(1,"驻场助教"),
    FREE_PERSON(2,"自由职业"),
    ;

    private int code;
    private String desc;

    private JobType(int code, String desc){
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
