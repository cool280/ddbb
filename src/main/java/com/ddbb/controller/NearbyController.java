package com.ddbb.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.annotate.DdbbController;
import com.ddbb.controller.request.NearbyAssistantCoachRequest;
import com.ddbb.controller.request.NearbyAssistantCoachResponse;
import com.ddbb.controller.request.NearbyHallRequest;
import com.ddbb.controller.request.NearbyHallResponse;
import com.ddbb.service.nearby.NearbyService;
import com.ddbb.utils.ObjectConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@DdbbController
@RequestMapping("/nearby")
@Slf4j
public class NearbyController extends BaseController {
    @Autowired
    private NearbyService nearbyService;

    @ResponseBody
    @PostMapping("/coach")
    public JSONObject getNearbyAssistantCoach(@RequestBody NearbyAssistantCoachRequest nearbyRequest){
        log.info("getNearbyAssistantCoach start: {}", ObjectConverter.o2s(nearbyRequest));
        List<NearbyAssistantCoachResponse> ret = nearbyService.getNearbyAssistantCoach(nearbyRequest);
        return OK(ret,"");
    }

    @ResponseBody
    @PostMapping("/hall")
    public JSONObject getNearbyHall(@RequestBody NearbyHallRequest nearbyRequest){
        log.info("getNearbyHall start: {}", ObjectConverter.o2s(nearbyRequest));
        List<NearbyHallResponse> ret = nearbyService.getNearbyHall(nearbyRequest);
        return OK(ret,"");
    }
}
