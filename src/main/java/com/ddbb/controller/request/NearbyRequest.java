package com.ddbb.controller.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class NearbyRequest implements Serializable {
    private Long qid;
    private Double longitude;
    private Double latitude;
    private Double minDistanceKm;
    private Double maxDistanceKm;
    private Integer maxCount;
}
