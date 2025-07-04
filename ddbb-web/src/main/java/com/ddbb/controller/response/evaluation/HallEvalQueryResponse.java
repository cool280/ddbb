package com.ddbb.controller.response.evaluation;

import lombok.Data;

import java.io.Serializable;

@Data
public class HallEvalQueryResponse implements Serializable {

    private String nickname;

    private Integer satisfaction = 0;    // 满意度

    private Integer environment = 0;     // 环境

    private Integer facilities = 0;      // 设施

    private Integer service = 0;         // 服务

    private Integer assistantScore = 0;  // 助教整体评分

    private Integer costPerformance = 0; // 性价比

    private String comment; //评价内容

    private Long createTime;

}
