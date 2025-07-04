package com.ddbb.extapi.wx;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.internal.domain.PlaceOrderResponse;
import com.ddbb.internal.utils.OkHttpUtil;
import com.ddbb.internal.utils.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付文档
 * 小程序支付模式介绍            https://pay.weixin.qq.com/doc/v3/partner/4012085810
 *
 */
@Service
@Slf4j
public class WxPayService {
    @Autowired
    private WxAuthorizationDown wxAuthorizationDown;
    /**
     * 第一步：预下单
     * 返回【预支付交易会话标识】
     * 接口文档JSAPI/小程序下单      https://pay.weixin.qq.com/doc/v3/merchant/4012791897
     *
     * 2.1 微信支付应答商户的请求时，商户需要验签。
     * 2.2 接收微信支付的回调时，商户需要验签
     * https://pay.weixin.qq.com/doc/v3/merchant/4012365342#%E8%AF%B7%E6%B1%82%E6%8E%A5%E5%8F%A3%E5%90%8E%E7%AB%AF%E7%AD%BE%E5%90%8D
     */
    public PlaceOrderResponse placeOrder() throws Exception{
        JSONObject bodyParam = new JSONObject();
        bodyParam.put("appid",WxPayConstants.SMALL_APP_ID);
        bodyParam.put("mchid",WxPayConstants.MCH_ID);
        bodyParam.put("description","教练费");
        bodyParam.put("out_trade_no","otn_"+ SnowflakeIdUtil.getInstance().nextId());
        bodyParam.put("notify_url","https://www.ddbb365.com:8080/wxcb/pay");


        JSONObject amountJson = new JSONObject();
        amountJson.put("total",1);//以分为单位
        amountJson.put("currency" ,"CNY");
        bodyParam.put("amount",amountJson);

        JSONObject payerJson = new JSONObject();
        payerJson.put("openid",WxPayConstants.TEST_OPEN_ID);
        bodyParam.put("payer",payerJson);

        //{"prepay_id":"wx04225310113623d5e91aa074f64fbe0000"}
        String result = OkHttpUtil.doPost(WxPayConstants.WholeUrl.PLACE_ORDER,bodyParam,
                getHeader(WxPayConstants.OperationUrl.PLACE_ORDER,bodyParam),
                wxAuthorizationDown);
        return buildH5Resp(result);
    }

    /**
     * 构建h5返回值
     * https://pay.weixin.qq.com/doc/v3/merchant/4012365341
     * @param placeOrderResult
     * @return
     */
    private PlaceOrderResponse buildH5Resp(String placeOrderResult){
        JSONObject json = JSONObject.parseObject(placeOrderResult);
        String prepayId = json.getString("prepay_id");

        long ts = System.currentTimeMillis() / 1000;
        String nonceStr = String.valueOf(SnowflakeIdUtil.getInstance().nextId());

        PlaceOrderResponse ret = new PlaceOrderResponse();
        ret.setSignType("RSA");
        ret.setPackage1("prepay_id="+prepayId);
        ret.setTimeStamp(String.valueOf(ts));
        ret.setNonceStr(nonceStr);
        ret.setPaySign(WxAuthorizationUp.getSmallAppPaySign(ts,nonceStr,prepayId));

        return ret;
    }
    private Map<String,String> getHeader(String url, JSONObject bodyParam){
        Map<String,String> map = new HashMap<>();
        map.put("Accept","application/json");
        map.put("Content-Type","application/json");
        map.put("Authorization", WxAuthorizationUp.getAuthorization(url,bodyParam));
        return map;
    }

    /**
     * 获取用户的openid
     * https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/code2Session.html
     * @param jsCode
     * @return
     */
    public Jscode2sessionResp jscode2session(String jsCode){
        String url = WxPayConstants.WholeUrl.GET_OPENID+"?grant_type=authorization_code&appid="+WxPayConstants.SMALL_APP_ID
                +"&secret="+WxPayConstants.SMALL_APP_SECRET+"&js_code=" + jsCode;
        String s = OkHttpUtil.doGet(url);
        JSONObject json = JSONObject.parseObject(s);
        Integer errCode = json.getInteger("errcode");
        if(errCode == null || errCode != 0){
            log.error("getOpenId errCode != 0, but: "+errCode+", message: "+json.getString("errmsg"));
            return null;
        }

        return Jscode2sessionResp.builder().openId(json.getString("openid"))
                .sessionKey(json.getString("session_key"))
                .unionId(json.getString("unionid")).build();
    }
}
