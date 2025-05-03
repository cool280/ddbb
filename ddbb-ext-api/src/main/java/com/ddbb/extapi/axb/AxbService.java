package com.ddbb.extapi.axb;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.internal.utils.MD5Util;
import com.ddbb.internal.utils.OkHttpUtil;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AxbService {
    /**
     * https://101.37.133.245:11008/platform/version/functionCode/appId/sig
     * platform 应用平台，语音为 voice
     * version 应用平台版本号，语音版本号为 1.0.0
     * functionCode 功能代码，例：AXB 模式绑定：middleNumberAXB
     * appId 用户账号，请在控制台-应用管理页面获取
     * token 用户账号授权令牌，请在控制台-应用管理页面获取
     * sig 验证信息，sig= md5(appId+token+时间戳) ，不含+号
     * Authorization 包头验证信息，Authorization= Base64(appId:时间戳)，英文冒号
     *
     *
     * 示例（AXB 模式绑定）
     * POST /voice/1.0.0/middleNumberAXB/388292/65434c1dc82393e654d02025d1965540 HTTP/1.1
     * Host: 101.37.133.245:11008
     * Content-Type: application/json
     * Authorization: OTM2ZmRmOWY1NTcyNDU2ZmE0YjdhNzk1Y2IwMzc4Nzk6MTUyNDQ4Mjk0MDkyOQ==
     * Cache-Control: no-cache
     * {
     * "middleNumber":"17664870250",
     * "bindNumberA":"17556428812",
     * "bindNumberB":"17591925866",
     * "maxBindingTime":"60"
     * }
     */
    private static String appId = "942669";
    private static String token = "c744c923487e498b86ce584ebc8b19ad";
    private static String axbUrl = "https://101.37.133.245:11008/voice/1.0.0/middleNumberAXB/"+appId+"/";
    private static String mid = "02160556163";

    //sig= md5(appId+token+时间戳)
    private static String getSig(long ts){
        String str = appId + token + ts;
        return MD5Util.encode(str);
    }
    //Authorization 包头验证信息，Authorization= Base64(appId:时间戳)，英文冒号
    public static String bind(String phoneA,String phoneB){
        long ts = System.currentTimeMillis();
        String url = axbUrl + getSig(ts);
        String authorization = appId+":"+ts;
        String auth = Base64Util.encode(authorization);


        JSONObject param = new JSONObject();
        param.put("middleNumber",mid);
        param.put("bindNumberA",phoneA);
        param.put("bindNumberB",phoneB);
        param.put("maxBindingTime",60);

        Map<String,String> headersMap = new HashMap<>();
        headersMap.put("Authorization", auth);

        String s = OkHttpUtil.doPost(url,param,headersMap);
        return s;
    }

    public static void main(String[] args) {
        String s = bind("13472644829","18336092956");
        System.out.println(s);
    }
}
