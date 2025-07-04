package com.ddbb.controller.wx;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.controller.BaseController;
import com.ddbb.controller.BaseResult;
import com.ddbb.controller.request.NearbyAssistantCoachRequest;
import com.ddbb.controller.response.NearbyAssistantCoachResponse;
import com.ddbb.extapi.wx.WxAuthorizationDown;
import com.ddbb.internal.annotate.DdbbController;
import com.ddbb.internal.utils.ObjectConverter;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 微信回调controller
 */
@RestController
@DdbbController
@Slf4j
@RequestMapping("/wxcb")
public class WxCallbackController extends BaseController {
    @Autowired
    private WxAuthorizationDown wxAuthorizationDown;
    /**
     * 支付成功回调
     * https://pay.weixin.qq.com/doc/v3/merchant/4012791902
     * @param json
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    @ResponseBody
    @PostMapping("/pay")
    public JSONObject payCallback(@RequestBody JSONObject json, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        log.info("payCallback start: {}", json.toJSONString());
        Pair<Boolean, String> p = wxAuthorizationDown.verifyRequest(httpServletRequest);
        if(p.getLeft()){
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            return null;
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        JSONObject ret = new JSONObject();
        json.put("code","FAIL");
        json.put("message","失败");
        return ret;
    }


    /**
     * 消费者投诉2.0 https://pay.weixin.qq.com/doc/v3/merchant/4012068466
     * 投诉通知回调  https://pay.weixin.qq.com/doc/v3/merchant/4012289719
     * @param nearbyRequest
     * @param httpServletRequest
     * @return
     */
    @ResponseBody
    @PostMapping("/complain")
    public void complainCallback(@RequestBody NearbyAssistantCoachRequest nearbyRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
//        log.info("complainCallback start: {}", ObjectConverter.o2s(nearbyRequest));
//        List<NearbyAssistantCoachResponse> ret = nearbyService.getNearbyAssistantCoach(nearbyRequest);
//        if(ret !=null){
//            ret.forEach(e->{
//                e.setAvatar(getImageAbsoluteUrl(httpServletRequest,e.getAvatar()));
//                handlePhoto(e,httpServletRequest);
//            });
//        }
//
//        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
    }
}
