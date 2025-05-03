package com.ddbb.service.challenge;

import lombok.Data;

import java.util.List;

@Data
public class ChallengeScheduleDO {
    //yyyy-MM-dd
    private String dateStr;
    //今天、明天、后天
    private String dateDesc;
    //周几
    private Integer week;
    /**
     * 是否上班
     */
    private Boolean atWork;
    private List<HourDO> hourList;
}
