package com.ddbb.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.controller.request.NearbyAssistantCoachRequest;
import com.ddbb.controller.request.NearbyAssistantCoachResponse;
import com.ddbb.controller.request.NearbyHallRequest;
import com.ddbb.controller.request.NearbyHallResponse;
import com.ddbb.service.nearby.NearbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NearbyController extends BaseController {
    @Autowired
    private NearbyService nearbyService;

    @ResponseBody
    public JSONObject getNearbyAssistantCoach(NearbyAssistantCoachRequest nearbyRequest){
        List<NearbyAssistantCoachResponse> ret = nearbyService.getNearbyAssistantCoach(nearbyRequest);
        return buildOk(ret,"");
    }

    @ResponseBody
    public JSONObject getNearbyHall(NearbyHallRequest nearbyRequest){
        List<NearbyHallResponse> ret = nearbyService.getNearbyHall(nearbyRequest);
        return buildOk(ret,"");
    }
}
