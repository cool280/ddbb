package com.ddbb.controller.request.evaluation;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class CoachEvalDimensionRequest {

    private Long coachUid;

    private Long evaluatorId;

    private Integer satisfaction = 0;    // 满意度

    private Integer appearance = 0;      // 颜值(0-10分)

    private Integer skill = 0;           // 球技(0-10分)

    private Integer attitude = 0;        // 态度(0-10分)

    private String comment;

    private String challengeId;

    public boolean validate() {
        if (coachUid == null || evaluatorId == null) {
            return false;
        }
        if (coachUid <= 0L || evaluatorId <= 0L) {
            return false;
        }
        if (StringUtils.isEmpty(challengeId)) {
            return false;
        }
        return true;
    }
}
