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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
     * {"summary":"支付成功","event_type":"TRANSACTION.SUCCESS","create_time":"2025-07-05T03:47:11+08:00",
     *  "resource":{
     *      "original_type":"transaction","algorithm":"AEAD_AES_256_GCM",
     *      "ciphertext":"70DJY8uDO9MXDJBk3CjWt7lDbu83v+bpx7lG5VWYCwDTVw7NmGMCKTPoX4U+OhfiPyMLMx1IOvnjzHzeRklJ3DYCJrnfMo+VoaDJBkn5586Zefno/0CFhHJOC0lpPo88Iwm4z+896ITrxGTqiTfsfLxq/vfCYxlHB5QZ5a7L7aFm6E48hEsi6JeuVcU9/sWT8EtGCd7413gMKnYW+rGWzwmX2PJYMe3SAXNQF8IKYlhtxFKLIim0b1Zz63Ni9N1rHPwB5R9T6zJ5HmCEQgScl/zajth2aQ8Bg+adIRT5HO99EI3xbBd+MTAsk7gb66YLy0MXzWCb/YH/8n8V8QbwioAh9WXWkFjaK3OK4kxbVJmhc8PBrW6bWNN3eh4fUC3fSuQTPgwBm/Tdcta+m/pRPRs4KqpUOn6PJweGbvyk+pWhhMfXO3aNtGdwmaHi9CU438Y7EpjDdDNVHUXc3XfJMi5zZBtQYw872sd8VshOBCTK2joN0MIJxkYk7B7acZH2zePwWsWS27M7LVMuuA7bvidjTzwHl5ao60YexxL9h2B7UpBjj4i5EdMV8SgY042XCNRZqA==",
     *      "associated_data":"transaction","nonce":"k2X2ESSWMuSk"
     *   },
     *   "resource_type":"encrypt-resource","id":"49bf0c81-5ca5-52f2-b16f-b7d50247c8dc"}
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    @ResponseBody
    @PostMapping("/pay")
    public JSONObject payCallback(@RequestBody String json , HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        log.info("payCallback start: {}", json);

        httpServletRequest.setAttribute("requestBodyXxx",json);

        Pair<Boolean, String> p = wxAuthorizationDown.verifyRequest(httpServletRequest);
        if(p.getLeft() || true){
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            return null;
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        JSONObject ret = new JSONObject();
        ret.put("code","FAIL");
        ret.put("message","失败");
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
