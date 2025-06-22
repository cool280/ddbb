package com.ddbb.service.challenge;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "ddbb.challenge")
@Data
public class ChallengeConfig {
    public static final Map<Integer,String> CHALLENGE_MESSAGE_MAP = new LinkedHashMap<Integer,String>(){{
        put(1,"我一定准时到");
        put(2,"不见不散");
        put(3,"我要报仇，不要跑");
    }};
    /**
     * 最多可预约到几天后对战
     */
    private Integer afterDays;
    /**
     * 挑战结束多少秒后，关闭挑战
     */
    private Integer closeWhenSecondsAfterEndTime;
    /**
     * 默认星期几上班: 1,2,3,4,5,6,7
     */
    private String defaultWorkDay;
    /**
     * 助教是否可以发起挑战
     */
    private Boolean assistantCoachAllowLaunch;

    /**
     * 助教默认上班时间
     */
    private Integer defaultWorkTimeStart;
    /**
     * 助教默认下班时间
     */
    private Integer defaultWorkTimeEnd;
    /**
     * 比赛开始前几小时内可签到
     */
    private Integer signInAheadHours;
    /**
     * 距离球房多少米内可签到
     */
    private Integer signInDistanceMi;
}
