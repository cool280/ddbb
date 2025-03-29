package com.ddbb.controller.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class NearbyAssistantCoachRequest implements Serializable {
    private Long qid;
    private Double longitude;
    private Double latitude;
}
