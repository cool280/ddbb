package com.ddbb.infra.data.mongo.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 助教评论
 */
@Document(collection = "coach_eval")
@Data
public class CoachEvalEntity extends MongoEntity {

    @Indexed
    private Long coachUid; //助教UID

    @Indexed
    private Long evalUid;//评论者的UID

    private Integer satisfaction = 0;    // 满意度

    private Integer appearance = 0;      // 颜值(0-10分)

    private Integer skill = 0;           // 球技(0-10分)

    private Integer attitude = 0;        // 态度(0-10分)

    private String comment; //内容

    @Indexed
    private String challengeId; // 订单ID

    private long cts = System.currentTimeMillis();
}
