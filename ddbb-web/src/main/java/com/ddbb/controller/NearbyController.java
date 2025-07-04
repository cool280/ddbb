package com.ddbb.controller;

import com.ddbb.internal.annotate.DdbbController;
import com.ddbb.controller.request.NearbyAssistantCoachRequest;
import com.ddbb.controller.response.IWithPhotoVO;
import com.ddbb.controller.response.NearbyAssistantCoachResponse;
import com.ddbb.controller.request.NearbyHallRequest;
import com.ddbb.controller.response.NearbyHallResponse;
import com.ddbb.service.nearby.NearbyService;
import com.ddbb.internal.utils.ObjectConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@DdbbController
@RequestMapping("/nearby")
@Slf4j
@Api(tags = "附近")
public class NearbyController extends BaseController {
    @Autowired
    private NearbyService nearbyService;

    @ResponseBody
    @PostMapping("/coach")
    @ApiOperation(value = "附近的助教")
    public BaseResult<List<NearbyAssistantCoachResponse>> getNearbyAssistantCoach(@RequestBody NearbyAssistantCoachRequest nearbyRequest, HttpServletRequest httpServletRequest){
        log.info("getNearbyAssistantCoach start: {}", ObjectConverter.o2s(nearbyRequest));
        List<NearbyAssistantCoachResponse> ret = nearbyService.getNearbyAssistantCoach(nearbyRequest);
        if(ret !=null){
            ret.forEach(e->{
                e.setAvatar(getImageAbsoluteUrl(httpServletRequest,e.getAvatar()));
                handlePhoto(e,httpServletRequest);
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
    @PostMapping("/starCoach")
    @ApiOperation(value = "明星助教")
    public BaseResult<List<NearbyAssistantCoachResponse>> getStarAssistantCoach(HttpServletRequest httpServletRequest){
        log.info("getStarAssistantCoach >>> start");
        List<NearbyAssistantCoachResponse> ret = nearbyService.getStarAssistantCoach();
        if(ret !=null){
            ret.forEach(e->{
                e.setAvatar(getImageAbsoluteUrl(httpServletRequest,e.getAvatar()));
                handlePhoto(e,httpServletRequest);
            });
        }

        return OK(ret,"");
    }
    private void handlePhoto(IWithPhotoVO ele, HttpServletRequest httpServletRequest){
        List<String> photo = ele.getPhoto();
        if(CollectionUtils.isEmpty(photo)){
            return;
        }

        List<String> photoUrls = new ArrayList<>();
        photo.forEach(s->{
            photoUrls.add(getImageAbsoluteUrl(httpServletRequest,s));
        });

        ele.setPhoto(photoUrls);
    }


    @ResponseBody
    @PostMapping("/hall")
    @ApiOperation(value = "附近的球房")
    public BaseResult<List<NearbyHallResponse>> getNearbyHall(@RequestBody NearbyHallRequest nearbyRequest, HttpServletRequest httpServletRequest){
        log.info("getNearbyHall start: {}", ObjectConverter.o2s(nearbyRequest));
        List<NearbyHallResponse> ret = nearbyService.getNearbyHall(nearbyRequest);

        if(ret !=null){
            ret.forEach(e->{
                e.setAvatar(getImageAbsoluteUrl(httpServletRequest,e.getAvatar()));
                handlePhoto(e,httpServletRequest);
            });
        }
        return OK(ret,"");
    }
}
