package com.ddbb.service.nearby;

import com.ddbb.controller.request.*;
import com.ddbb.mongo.AssistantCoachRepo;
import com.ddbb.mongo.GeoQueryContext;
import com.ddbb.mongo.HallRepo;
import com.ddbb.mongo.entity.AssistantCoach;
import com.ddbb.mongo.entity.Hall;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NearbyService {
    //杨浦百联滨江
    private static double DEFAULT_LONGITUDE = 121.544732;
    private static double DEFAULT_LATITUDE = 31.271186;

    @Autowired
    private AssistantCoachRepo repo;
    @Autowired
    private HallRepo hallRepo;

    /**
     * 获取附近的助教
     * @param nearbyRequest
     * @return
     */
    public List<NearbyAssistantCoachResponse> getNearbyAssistantCoach(NearbyAssistantCoachRequest nearbyRequest) {
        GeoResults<AssistantCoach> geoResults = repo.geoQuery(checkParam(nearbyRequest));

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
    /**
     * 获取附近的球房
     * @param nearbyRequest
     * @return
     */
    public List<NearbyHallResponse> getNearbyHall(NearbyHallRequest nearbyRequest) {
        GeoResults<Hall> geoResults = hallRepo.geoQuery(checkParam(nearbyRequest));

        List<NearbyHallResponse> ret = new ArrayList<>();
        if (geoResults != null) {
            List<GeoResult<Hall>> content = geoResults.getContent();
            content.forEach(e -> {
                Hall hall = e.getContent();
                double distance = e.getDistance().getValue();

                NearbyHallResponse r = new NearbyHallResponse();
                BeanUtils.copyProperties(hall, r);
                r.setDistanceKm(distance);
                ret.add(r);
            });
        }
        return ret;
    }

    private GeoQueryContext checkParam(NearbyRequest nearbyRequest){
        if (nearbyRequest.getLongitude() == null) {
            nearbyRequest.setLongitude(DEFAULT_LONGITUDE);
        }
        if (nearbyRequest.getLatitude() == null) {
            nearbyRequest.setLatitude(DEFAULT_LATITUDE);
        }
        if(nearbyRequest.getMaxCount() == null){
            nearbyRequest.setMaxCount(10);
        }
        if(nearbyRequest.getMinDistanceKm() == null){
            nearbyRequest.setMinDistanceKm(0d);
        }
        if(nearbyRequest.getMaxDistanceKm() == null){
            nearbyRequest.setMaxDistanceKm(5d);
        }


        return GeoQueryContext.builder().longitude(nearbyRequest.getLongitude()).latitude(nearbyRequest.getLatitude())
                .maxDistanceKm(nearbyRequest.getMaxDistanceKm()).minDistanceKm(nearbyRequest.getMinDistanceKm())
                .maxNum(nearbyRequest.getMaxCount()).build();
    }
}
