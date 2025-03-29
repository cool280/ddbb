package com.ddbb.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.controller.request.NearbyAssistantCoachRequest;
import com.ddbb.controller.request.NearbyAssistantCoachResponse;
import com.ddbb.mongo.GeoQueryContext;
import com.ddbb.mongo.entity.AssistantCoach;
import com.ddbb.service.coach.AssistantCoachService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class NearbyController extends BaseController {
    @Autowired
    private AssistantCoachService assistantCoachService;

    @ResponseBody
    public JSONObject getNearbyAssistantCoach(NearbyAssistantCoachRequest nearbyRequest){
        List<NearbyAssistantCoachResponse> ret = assistantCoachService.getNearbyAssistantCoach(nearbyRequest);
        return buildOk(ret,"");
    }
}
