package com.ddbb.test;

import com.alibaba.fastjson.JSONObject;
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
}
