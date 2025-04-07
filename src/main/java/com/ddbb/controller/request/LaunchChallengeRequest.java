package com.ddbb.controller.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LaunchChallengeRequest implements Serializable {
    private Long from;//挑战发起人 - 球友id
    private Long to;//挑战接收
    // 人 - 助教id
    private Long hallId;//对战球房id
    private String date;//yyyy-MM-dd
    // 预约开始时间：预约起止时间必须连续且不能跨天
    private Integer startTime;
    // 预约终止时间：预约起止时间必须连续且不能跨天
    private Integer endTime;
}
