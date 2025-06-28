package com.ddbb.internal.enums;

import java.util.Arrays;
import java.util.Optional;

public enum PointType {

    SIGN(1, 5, "签到"),

    ;

    PointType(int type, int point, String desc) {
        this.type = type;
        this.point = point;
        this.desc = desc;
    }

    public int type;

    public int point;

    public String desc;


    public static PointType of(int type) {
        Optional<PointType> optionalPointType = Arrays.stream(PointType.values()).filter(point -> point.type == type).findFirst();
        return optionalPointType.isPresent() ? optionalPointType.get() : null;
    }
}
