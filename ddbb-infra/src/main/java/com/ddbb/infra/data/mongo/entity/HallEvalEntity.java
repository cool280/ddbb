package com.ddbb.infra.data.mongo.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 大厅评论
 */
@Document(collection = "hall_eval")
@Data
public class HallEvalEntity extends MongoEntity {

    private Long hallId;

    private Long evaluatorId;

    private Integer satisfaction=0;    // 满意度

    private Integer environment=0;     // 环境

    private Integer facilities=0;      // 设施

    private Integer service=0;         // 服务

    private Integer assistantScore=0;  // 助教整体评分

    private Integer costPerformance=0; // 性价比

    private String comment;

    private String challengeId;

    private long cts = System.currentTimeMillis();
}
