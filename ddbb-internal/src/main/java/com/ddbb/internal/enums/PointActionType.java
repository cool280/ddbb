package com.ddbb.internal.enums;

import java.util.Arrays;
import java.util.Optional;

public enum PointActionType {

    SIGN_IN(1, 5, "签到"),

    KEEP_SIGN_IN(2, 100, "签到"),

    COACH_COMMENT(3, 20, "评论"),

    HALL_COMMENT(4, 20, "评论"),

    ;

    PointActionType(int type, int points, String desc) {
        this.type = type;
        this.points = points;
        this.desc = desc;
    }

    public int type;

    public int points;

    public String desc;


    public static PointActionType of(int type) {
        Optional<PointActionType> optionalPointType = Arrays.stream(PointActionType.values()).filter(point -> point.type == type).findFirst();
        return optionalPointType.isPresent() ? optionalPointType.get() : null;
    }

    public static boolean isSignIn(int type) {
        return PointActionType.SIGN_IN.type == type || PointActionType.KEEP_SIGN_IN.type == type;
    }
}
