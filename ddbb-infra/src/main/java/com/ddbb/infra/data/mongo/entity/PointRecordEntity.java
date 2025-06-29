package com.ddbb.infra.data.mongo.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "point_record")
@Data
public class PointRecordEntity extends MongoEntity {

    @Indexed
    private Long uid; //uid

    private Integer actionType; //操作类型

    private Integer points; //积分

    @Indexed
    private Long cts = System.currentTimeMillis();//创建时间
}
