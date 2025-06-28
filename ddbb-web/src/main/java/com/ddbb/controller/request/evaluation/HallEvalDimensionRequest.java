package com.ddbb.controller.request.evaluation;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
public class HallEvalDimensionRequest implements Serializable {

    private Long hallId;

    private Long evaluatorId;

    private Integer satisfaction;    // 满意度

    private Integer environment;     // 环境

    private Integer facilities;      // 设施

    private Integer service;         // 服务

    private Integer assistantScore;  // 助教整体评分

    private Integer costPerformance; // 性价比

    private String comment;

    private String challengeId;

    public boolean validate() {
        if (hallId == null || evaluatorId == null) {
            return false;
        }
        if (hallId <= 0L || evaluatorId <= 0L) {
            return false;
        }
        if (StringUtils.isEmpty(challengeId)) {
            return false;
        }
        return true;
    }
}
