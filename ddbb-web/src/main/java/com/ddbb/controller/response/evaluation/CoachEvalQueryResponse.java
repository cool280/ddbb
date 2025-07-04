package com.ddbb.controller.response.evaluation;

import lombok.Data;

import java.io.Serializable;

@Data
public class CoachEvalQueryResponse implements Serializable {

    private String nickname;

    private Integer satisfaction = 0;    // 满意度

    private Integer appearance = 0;      // 颜值(0-10分)

    private Integer skill = 0;           // 球技(0-10分)

    private Integer attitude = 0;        // 态度(0-10分)

    private String comment; //内容

    private Long createTime;
}
