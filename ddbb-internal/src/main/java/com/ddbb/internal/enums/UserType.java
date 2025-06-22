package com.ddbb.internal.enums;

public enum UserType{
    ASSISTANT_COACH(1,"助教"),
    COMMON_USER(2,"普通用户"),
    ;

    private int code;
    private String desc;

    private UserType(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UserType of(Integer code){
        if(code == null){
            return null;
        }
        UserType[] arr = UserType.values();
        for(UserType t: arr){
            if(t.getCode() == code){
                return t;
            }
        }
        return null;
    }
}
