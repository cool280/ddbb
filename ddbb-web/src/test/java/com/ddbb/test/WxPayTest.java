package com.ddbb.test;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.extapi.wx.WxAuthorizationUp;
import com.ddbb.extapi.wx.WxPayConstants;
import com.ddbb.extapi.wx.WxPayService;
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

    @Test
    public void testPlaceOrder() throws Exception {
        wxPayService.placeOrder();
        System.out.println(" ==================================");
    }
}
