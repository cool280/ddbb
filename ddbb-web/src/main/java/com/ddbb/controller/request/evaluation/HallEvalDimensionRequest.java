package com.ddbb.controller.request.evaluation;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
public class HallEvalDimensionRequest implements Serializable {

    private Long hallId; //球房ID

    private Long evalUid; //评论者UID

    private Integer satisfaction;    // 满意度

    private Integer environment;     // 环境

    private Integer facilities;      // 设施

    private Integer service;         // 服务

    private Integer assistantScore;  // 助教整体评分

    private Integer costPerformance; // 性价比

    private String comment; //评价内容

    private String challengeId; //订单ID

    public boolean validate() {
        if (hallId == null || evalUid == null) {
            return false;
        }
        if (hallId <= 0L || evalUid <= 0L) {
            return false;
        }
        if (StringUtils.isEmpty(challengeId)) {
            return false;
        }
        return true;
    }

    public boolean validateDimension() {
        if (satisfaction <= 0 || environment <= 0 || facilities <= 0 || service <= 0 || assistantScore <= 0 || costPerformance <= 0) {
            return false;
        }
        if (StringUtils.isEmpty(comment)) {
            return false;
        }
        return true;
    }
}
