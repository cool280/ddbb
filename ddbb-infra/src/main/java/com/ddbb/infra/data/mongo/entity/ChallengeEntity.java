package com.ddbb.infra.data.mongo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChallengeEntity extends MongoEntity implements Serializable {
    /**
     * 一张挑战书生成两条记录，owner分别 = from和to，其他字段都一样
     */
    private Long owner;
    /**
     * 挑战书id
     */
    private String challengeId;
    private Long from;
    private Long to;
    /**
     * 球房id
     */
    private Long hallId;
    private String challengeDateStr;
    private Integer challengeDateInt;
    private Integer startTime;
    private Integer endTime;
    private Integer status;
    /**
     * 动作：1 - 发起，2 - 接受
     */
    private Integer challengeRole;
    /**
     * 挑战书是否存活。定时任务，超过24h自动设置为false
     */
    private Boolean alive;
    /**
     * 挑战方是否签到
     */
    private Boolean fromSignIn;
    /**
     * 接受方是否签到
     */
    private Boolean toSignIn;
    /**
     * 比分是否登记
     */
    private Boolean scoreSaved;
    /**
     * 比分，字符串，格式：from胜几局_to胜几局 或 from分数_to分数
     */
    private String scoreSnapshot;
    /**
     * 助教是否已评价
     */
    private Boolean coachCommented;
    /**
     * 球房是否已评价
     */
    private Boolean hallCommented;
    private Long createTimeTs;
    private Long updateTimeTs;
}
