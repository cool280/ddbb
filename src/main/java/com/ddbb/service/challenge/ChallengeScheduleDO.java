package com.ddbb.service.challenge;

import lombok.Data;

import java.util.Map;

@Data
public class ChallengeScheduleDO {
    //yyyy-MM-dd
    private String dateStr;
    //今天、明天、后天
    private String dateDesc;
    //周几
    private Integer week;
    private Map<Integer,Integer> hourMap;
}
