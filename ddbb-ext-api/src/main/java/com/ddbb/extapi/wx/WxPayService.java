package com.ddbb.extapi.wx;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.internal.utils.OkHttpUtil;
import com.ddbb.internal.utils.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付文档
 * https://pay.weixin.qq.com/doc/v3/partner/4012085810
 * https://pay.weixin.qq.com/doc/v3/merchant/4012791897
 */
@Service
@Slf4j
public class WxPayService {
    /**
     * 下单
     * 返回【预支付交易会话标识】
     */
    public String placeOrder() throws Exception{
        JSONObject bodyParam = new JSONObject();
        bodyParam.put("appid",WxPayConstants.SMALL_APP_ID);
        bodyParam.put("mchid","1715041370");
        bodyParam.put("description","教练费");
        bodyParam.put("out_trade_no","otn_"+ SnowflakeIdUtil.getInstance().nextId());
        bodyParam.put("notify_url","http://www.ddbb365.com/wx_notify");


        JSONObject amountJson = new JSONObject();
        amountJson.put("total",100);
        amountJson.put("currency" ,"CNY");
        bodyParam.put("amount",amountJson);

        JSONObject payerJson = new JSONObject();
        payerJson.put("openid","ovqdowRIfstpQK_kYShFS2MSS9XS");
        bodyParam.put("payer",payerJson);


        Response resp = OkHttpUtil.doPostResponseReturned(WxPayConstants.WholeUrl.PLACE_ORDER,bodyParam,getHeader(WxPayConstants.OperationUrl.PLACE_ORDER,bodyParam));

        boolean b = WxAuthorizationDown.verifyWxResponse(resp);
        if(!b){
            log.error("[placeOrder] >>> validate wx response failed");
            return "";
        }else{
            //获取Http Status Code.其中200表示成功
            if (resp.code() == 200) {
                //这里需要注意，response.body().string()是获取返回的结果，此句话只能调用一次，再次调用获得不到结果。
                //所以先将结果使用result变量接收
                String result = resp.body().string();
                log.info("[doPost] >>> result: "+result);

                JSONObject json = JSONObject.parseObject(result);
                return json.getString("prepay_id");
            }else{
                log.warn("[doPost] >>> resultCode is not 200, but: "+resp.code());
                String result = resp.body().string();
                log.warn("[doPost] >>> response.body(): "+result);
                return "";
            }
        }
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
