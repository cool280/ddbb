package com.ddbb.infra.data.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoQueryContext {
    private Double longitude;
    private Double latitude;
    private Double minDistanceKm;
    private Double maxDistanceKm;
    private int maxNum;
    private Criteria criteria;
}
