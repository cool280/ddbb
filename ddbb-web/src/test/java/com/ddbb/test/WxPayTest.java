package com.ddbb.test;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.controller.wx.WxCallbackController;
import com.ddbb.extapi.wx.Jscode2sessionResp;
import com.ddbb.extapi.wx.WxAuthorizationUp;
import com.ddbb.extapi.wx.WxPayConstants;
import com.ddbb.extapi.wx.WxPayService;
import com.ddbb.internal.domain.PlaceOrderResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class WxPayTest extends BaseTest{
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private WxCallbackController wxCallbackController;
    @Test
    public void testWxAuthorization(){
        JSONObject json = new JSONObject();
        json.put("name","jack");
        json.put("amount",20);


        String authorization = WxAuthorizationUp.getAuthorization(WxPayConstants.OperationUrl.PLACE_ORDER, json);
        System.out.println(" ==================================");
        System.out.println(authorization);
    }

    /**
     * {"prepay_id":"wx04225310113623d5e91aa074f64fbe0000"}
     * @throws Exception
     */
    @Test
    public void testPlaceOrder() throws Exception {
        PlaceOrderResponse s = wxPayService.placeOrder();
        //{"prepay_id":"wx04225310113623d5e91aa074f64fbe0000"}
        System.out.println(" testPlaceOrder ============== " + JSONObject.toJSONString(s));
    }

    /**
     * {"session_key":"UtbZ8qw1TbxCgMamr+Fl/Q==","openid":"oC7Ri7B9YhFthihK4m3ytqt8LJAM"}
     */
    @Test
    public void testJscode2session(){
        String jsCode = "0e3uVT000s7OwU1WRZ2000ZpZf0uVT0l";
        Jscode2sessionResp jscode2sessionResp = wxPayService.jscode2session(jsCode);
        System.out.println(JSONObject.toJSONString(jscode2sessionResp));
    }

    @Test
    public void testDecryptPayCallbackToString(){
        String jsonStr = "{\"summary\":\"支付成功\",\"event_type\":\"TRANSACTION.SUCCESS\",\"create_time\":\"2025-07-05T03:47:11+08:00\",\"resource\":{\"original_type\":\"transaction\",\"algorithm\":\"AEAD_AES_256_GCM\",\"ciphertext\":\"70DJY8uDO9MXDJBk3CjWt7lDbu83v+bpx7lG5VWYCwDTVw7NmGMCKTPoX4U+OhfiPyMLMx1IOvnjzHzeRklJ3DYCJrnfMo+VoaDJBkn5586Zefno/0CFhHJOC0lpPo88Iwm4z+896ITrxGTqiTfsfLxq/vfCYxlHB5QZ5a7L7aFm6E48hEsi6JeuVcU9/sWT8EtGCd7413gMKnYW+rGWzwmX2PJYMe3SAXNQF8IKYlhtxFKLIim0b1Zz63Ni9N1rHPwB5R9T6zJ5HmCEQgScl/zajth2aQ8Bg+adIRT5HO99EI3xbBd+MTAsk7gb66YLy0MXzWCb/YH/8n8V8QbwioAh9WXWkFjaK3OK4kxbVJmhc8PBrW6bWNN3eh4fUC3fSuQTPgwBm/Tdcta+m/pRPRs4KqpUOn6PJweGbvyk+pWhhMfXO3aNtGdwmaHi9CU438Y7EpjDdDNVHUXc3XfJMi5zZBtQYw872sd8VshOBCTK2joN0MIJxkYk7B7acZH2zePwWsWS27M7LVMuuA7bvidjTzwHl5ao60YexxL9h2B7UpBjj4i5EdMV8SgY042XCNRZqA==\",\"associated_data\":\"transaction\",\"nonce\":\"k2X2ESSWMuSk\"},\"resource_type\":\"encrypt-resource\",\"id\":\"49bf0c81-5ca5-52f2-b16f-b7d50247c8dc\"}";
        String ret = wxCallbackController.decryptPayCallbackToString(jsonStr);
        System.out.println(ret);
    }
}
