package com.ddbb.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.annotate.DdbbController;
import com.ddbb.controller.request.NearbyAssistantCoachRequest;
import com.ddbb.controller.request.NearbyAssistantCoachResponse;
import com.ddbb.controller.request.NearbyHallRequest;
import com.ddbb.controller.request.NearbyHallResponse;
import com.ddbb.service.nearby.NearbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@DdbbController
@RequestMapping("/nearby")
public class NearbyController extends BaseController {
    @Autowired
    private NearbyService nearbyService;

    @ResponseBody
    @PostMapping("/coach")
    public JSONObject getNearbyAssistantCoach(@RequestBody NearbyAssistantCoachRequest nearbyRequest){
        List<NearbyAssistantCoachResponse> ret = nearbyService.getNearbyAssistantCoach(nearbyRequest);
        return buildOk(ret,"");
    }

    @ResponseBody
    @PostMapping("/hall")
    public JSONObject getNearbyHall(@RequestBody NearbyHallRequest nearbyRequest){
        List<NearbyHallResponse> ret = nearbyService.getNearbyHall(nearbyRequest);
        return buildOk(ret,"");
    }
}
