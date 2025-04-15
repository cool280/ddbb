package com.ddbb.service.challenge;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
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

}
