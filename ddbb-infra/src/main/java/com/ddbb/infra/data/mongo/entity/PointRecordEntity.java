package com.ddbb.infra.data.mongo.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "point_record")
@Data
public class PointRecordEntity extends MongoEntity {

    @Indexed
    private Long uid; //uid

    private Integer pointType; //积分类型

    private Integer point; //积分

    private Date createTime;//创建时间
}
