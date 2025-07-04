package com.ddbb.controller.request.evaluation;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class CoachEvalDimensionRequest {

    private Long coachUid; //助教UID

    private Long evalUid; //评论者UID

    private Integer satisfaction = 0;    // 满意度

    private Integer appearance = 0;      // 颜值(0-10分)

    private Integer skill = 0;           // 球技(0-10分)

    private Integer attitude = 0;        // 态度(0-10分)

    private String comment; //内容

    private String challengeId; //订单ID

    public boolean validate() {
        if (coachUid == null || evalUid == null) {
            return false;
        }
        if (coachUid <= 0L || evalUid <= 0L) {
            return false;
        }
        if (StringUtils.isEmpty(challengeId)) {
            return false;
        }
        return true;
    }

    public boolean validateDimension() {
        if (satisfaction <= 0 || appearance <= 0 || skill <= 0 || attitude <= 0) {
            return false;
        }
        if (StringUtils.isEmpty(comment)) {
            return false;
        }
        return true;
    }
}
