package com.ddbb.enums;

/**
 * 挑战书状态，注意，状态为负数表示该单已经结束，不能再修改状态
 */
public enum ChallengeStatus {
    INIT(1,"发起方发起"),
    TO_REFUSED(2,"接受方拒绝"),
    TO_ACCEPTED(3,"接受方接受"),
    FROM_CANCELED(4,"发起方取消"),
    TO_CANCELED(5,"接受方取消"),
    SINGLE_SIGN_IN(6,"单方签到"),
    ALL_SIGN_IN(7,"双方签到"),




//    FROM_SIGN_IN(6,"发起方签到"),
//    TO_SIGN_IN(7,"接受方签到"),
    SCORE_SAVED(8,"比分已登记"),
    COACH_COMMENTED(9,"助教已评价"),
    HALL_COMMENTED(10,"球房已评价"),
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

    public static ChallengeStatus of(Integer code){
        if(code == null){
            return null;
        }
        ChallengeStatus[] arr = ChallengeStatus.values();
        for(ChallengeStatus status: arr){
            if(status.getCode() == code ){
                return status;
            }
        }
        return null;
    }
}
