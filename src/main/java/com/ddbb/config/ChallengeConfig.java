package com.ddbb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ddbb.challenge")
@Data
public class ChallengeConfig {
    /**
     * 最多可预约到几天后对战
     */
    private Integer afterDays;
    /**
     * 助教是否可以发起挑战
     */
    private Boolean assistantCoachAllowLaunch;
}
