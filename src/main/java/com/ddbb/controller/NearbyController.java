package com.ddbb.controller;

import com.ddbb.annotate.DdbbController;
import com.ddbb.controller.request.NearbyAssistantCoachRequest;
import com.ddbb.controller.response.NearbyAssistantCoachResponse;
import com.ddbb.controller.request.NearbyHallRequest;
import com.ddbb.controller.response.NearbyHallResponse;
import com.ddbb.service.nearby.NearbyService;
import com.ddbb.utils.ObjectConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
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
    public BaseResult getNearbyAssistantCoach(@RequestBody NearbyAssistantCoachRequest nearbyRequest, HttpServletRequest httpServletRequest){
        log.info("getNearbyAssistantCoach start: {}", ObjectConverter.o2s(nearbyRequest));
        List<NearbyAssistantCoachResponse> ret = nearbyService.getNearbyAssistantCoach(nearbyRequest);
        if(ret !=null){
            ret.forEach(e->{
                e.setAvatar(getImageAbsoluteUrl(httpServletRequest,e.getAvatar()));
            });
        }
        /*
        System.out.println("getRequestURI ============== "+httpServletRequest.getRequestURI());
        System.out.println("getRequestURL ============== "+httpServletRequest.getRequestURL());
        System.out.println("getServletPath ============== "+httpServletRequest.getServletPath());
        System.out.println("getContextPath ============== "+httpServletRequest.getContextPath());
        System.out.println("getPathInfo ============== "+httpServletRequest.getPathInfo());
        System.out.println("getLocalAddr ============== "+httpServletRequest.getLocalAddr());
        System.out.println("getLocalPort ============== "+httpServletRequest.getLocalPort());
        System.out.println("getServletPath ============== "+httpServletRequest.getServletPath());
        System.out.println("getRemoteAddr ============== "+httpServletRequest.getRemoteAddr());
        System.out.println("getRemoteHost ============== "+httpServletRequest.getRemoteHost());
        System.out.println("getRemotePort ============== "+httpServletRequest.getRemotePort());
        */

        return OK(ret,"");
    }

    @ResponseBody
    @PostMapping("/hall")
    public BaseResult getNearbyHall(@RequestBody NearbyHallRequest nearbyRequest){
        log.info("getNearbyHall start: {}", ObjectConverter.o2s(nearbyRequest));
        List<NearbyHallResponse> ret = nearbyService.getNearbyHall(nearbyRequest);
        return OK(ret,"");
    }
}
