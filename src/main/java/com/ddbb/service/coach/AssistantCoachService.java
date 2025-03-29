package com.ddbb.service.coach;

import com.ddbb.controller.request.NearbyAssistantCoachRequest;
import com.ddbb.controller.request.NearbyAssistantCoachResponse;
import com.ddbb.mongo.AssistantCoachRepo;
import com.ddbb.mongo.GeoQueryContext;
import com.ddbb.mongo.entity.AssistantCoach;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssistantCoachService {
    //杨浦百联滨江
    private static double DEFAULT_LONGITUDE = 121.544732;
    private static double DEFAULT_LATITUDE = 31.271186;

    @Autowired
    private AssistantCoachRepo repo;

    public List<NearbyAssistantCoachResponse> getNearbyAssistantCoach(NearbyAssistantCoachRequest nearbyRequest) {
        if (nearbyRequest.getLongitude() == null) {
            nearbyRequest.setLongitude(DEFAULT_LONGITUDE);
        }
        if (nearbyRequest.getLatitude() == null) {
            nearbyRequest.setLatitude(DEFAULT_LATITUDE);
        }
        GeoResults<AssistantCoach> geoResults = repo.geoQuery(GeoQueryContext.builder().longitude(nearbyRequest.getLongitude()).latitude(nearbyRequest.getLatitude())
                .maxDistanceKm(150d).minDistanceKm(0d).maxNum(100).build());

        List<NearbyAssistantCoachResponse> ret = new ArrayList<>();
        if (geoResults != null) {
            List<GeoResult<AssistantCoach>> content = geoResults.getContent();
            content.forEach(e -> {
                AssistantCoach coach = e.getContent();
                double distance = e.getDistance().getValue();

                NearbyAssistantCoachResponse r = new NearbyAssistantCoachResponse();
                BeanUtils.copyProperties(coach, r);
                r.setDistanceKm(distance);
                ret.add(r);
            });
        }
        return ret;
    }
}
