package com.ddbb.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.annotate.DdbbController;
import com.ddbb.controller.request.*;
import com.ddbb.service.challenge.ChallengeService;
import com.ddbb.utils.ObjectConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@DdbbController
@RequestMapping("/challenge")
@Slf4j
public class ChallengeController extends BaseController {
    @Autowired
    private ChallengeService challengeService;

    /**
     * 发起挑战
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/launchChallenge")
    public JSONObject launchChallenge(@RequestBody LaunchChallengeRequest request){
        log.info("launchChallenge start: {}", ObjectConverter.o2s(request));
        challengeService.launchChallenge(request);
        return SUCCESS;
    }


}
