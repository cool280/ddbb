package com.ddbb.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.annotate.DdbbController;
import com.ddbb.controller.request.*;
import com.ddbb.service.challenge.ChallengeService;
import com.ddbb.utils.ObjectConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/launchChallenge")
    public JSONObject launchChallenge(@RequestBody LaunchChallengeRequest request){
        log.info("[launchChallenge] >>> start: {}", ObjectConverter.o2s(request));
        try{
            if(request.getFrom() == null || request.getTo() == null || request.getHallId()==null||
                    request.getStartTime()==null||request.getEndTime()==null||
                    StringUtils.isBlank(request.getChallengeDateStr())){
                return buildResult(-1,"param error");
            }
            int startTime = request.getStartTime();
            int endTime = request.getEndTime();
            if(!(startTime >=0 && startTime<=23)){
                return buildResult(-1,"startTime must be in [0,23]");
            }
            if(!(endTime >=1 && startTime<=24)){
                return buildResult(-1,"endTime must be in [1,24]");
            }
            if(endTime <= startTime){
                return buildResult(-1,"endTime must > startTime");
            }

            String regex = "\\d{4}-\\d{2}-\\d{2}";
            boolean isValidDate1 = request.getChallengeDateStr().matches(regex); // true 表示格式正确
            if(!isValidDate1){
                return buildResult(-1,"challenge date format error, must be yyyy-MM-dd");
            }

            challengeService.launchChallenge(request);
            return SUCCESS;
        }catch (Exception e){
            log.error("[launchChallenge] with error:{}",e.getMessage(),e);
            return buildError(e);
        }

    }


}
