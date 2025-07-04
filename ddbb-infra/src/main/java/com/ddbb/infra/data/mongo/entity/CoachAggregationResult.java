package com.ddbb.infra.data.mongo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CoachAggregationResult implements Serializable {

    private Integer satisfaction;

    private Integer appearance;

    private Integer skill;

    private Integer attitude;

    private Integer count;
}
