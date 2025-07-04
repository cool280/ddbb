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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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
     *      "original_type":"transaction", // 加密前的对象类型
     *      "algorithm":"AEAD_AES_256_GCM", // 加密算法
     *      // Base64编码后的密文
     *      "ciphertext":"70DJY8uDO9MXDJBk3CjWt7lDbu83v+bpx7lG5VWYCwDTVw7NmGMCKTPoX4U+OhfiPyMLMx1IOvnjzHzeRklJ3DYCJrnfMo+VoaDJBkn5586Zefno/0CFhHJOC0lpPo88Iwm4z+896ITrxGTqiTfsfLxq/vfCYxlHB5QZ5a7L7aFm6E48hEsi6JeuVcU9/sWT8EtGCd7413gMKnYW+rGWzwmX2PJYMe3SAXNQF8IKYlhtxFKLIim0b1Zz63Ni9N1rHPwB5R9T6zJ5HmCEQgScl/zajth2aQ8Bg+adIRT5HO99EI3xbBd+MTAsk7gb66YLy0MXzWCb/YH/8n8V8QbwioAh9WXWkFjaK3OK4kxbVJmhc8PBrW6bWNN3eh4fUC3fSuQTPgwBm/Tdcta+m/pRPRs4KqpUOn6PJweGbvyk+pWhhMfXO3aNtGdwmaHi9CU438Y7EpjDdDNVHUXc3XfJMi5zZBtQYw872sd8VshOBCTK2joN0MIJxkYk7B7acZH2zePwWsWS27M7LVMuuA7bvidjTzwHl5ao60YexxL9h2B7UpBjj4i5EdMV8SgY042XCNRZqA==",
     *      "associated_data":"transaction", // 附加数据包（可能为空）
     *      "nonce":"k2X2ESSWMuSk"  // 加密使用的随机串初始化向量
     *   },
     *   "resource_type":"encrypt-resource",
     *   "id":"49bf0c81-5ca5-52f2-b16f-b7d50247c8dc"}
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    @ResponseBody
    @PostMapping("/pay")
    //注意：@RequestBody 后面一定要用String，这样保证接收最原始的请求体
    //注意：@RequestBody 后面一定要用String，这样保证接收最原始的请求体
    //注意：@RequestBody 后面一定要用String，这样保证接收最原始的请求体
    public JSONObject payCallback(@RequestBody String json , HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        log.info("payCallback start: {}", json);

        httpServletRequest.setAttribute("requestBodyXxx",json);

        Pair<Boolean, String> p = wxAuthorizationDown.verifyRequest(httpServletRequest);
        if(p.getLeft()){
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            new Thread(()->{
                decryptPayCallbackToString(json);
            }).start();
            return null;
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        JSONObject ret = new JSONObject();
        ret.put("code","FAIL");
        ret.put("message","失败");
        return ret;
    }
    static final int KEY_LENGTH_BYTE = 32;
    static final int TAG_LENGTH_BIT = 128;

    public static String decryptPayCallbackToString(String jsonStr){
        JSONObject json = JSONObject.parseObject(jsonStr);
        JSONObject resource = json.getJSONObject("resource");

        String a = StringUtils.isBlank(resource.getString("associated_data"))?"":resource.getString("associated_data");
        byte[] associatedData = a.getBytes(StandardCharsets.UTF_8);
        byte[] nonce = resource.getString("nonce").getBytes(StandardCharsets.UTF_8);
        try {
            String ret = decryptPayCallbackToString(associatedData,nonce, resource.getString("ciphertext"));
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 解密支付的回调
     * @param associatedData
     * @param nonce
     * @param ciphertext
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static String decryptPayCallbackToString(byte[] associatedData, byte[] nonce, String ciphertext)
            throws GeneralSecurityException, IOException {
        byte[] aesKey = "ddbbc9d2EfG4h5Ijxl98Mn0OpQ3rSt5U".getBytes(StandardCharsets.UTF_8);
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, nonce);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            cipher.updateAAD(associatedData);
            return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), "utf-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException(e);
        }
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


    public static void main(String[] args) throws NoSuchAlgorithmException {
        String jsonStr = "{\"id\":\"5934a34c-d94e-51ad-bf5d-3949cc79672c\",\"create_time\":\"2025-07-05T05:06:22+08:00\",\"resource_type\":\"encrypt-resource\",\"event_type\":\"TRANSACTION.SUCCESS\",\"summary\":\"支付成功\",\"resource\":{\"original_type\":\"transaction\",\"algorithm\":\"AEAD_AES_256_GCM\",\"ciphertext\":\"7+xct2MUYJBp7SJEOMkCY+tVt33liVrBYUr7+NEgJHZ5vw5x1jq1juyA/Trv8IerrJZaiybM96/D9IE3GPH0D2wPWjGwwr70WpMOq8Yoh6DahbTHxrNc3lmr8ofCWKhQGgXg63DucOfbUOqP50IywKRSp3UFtYaL7cCXaUeNZn7+m3ClqwSUWkt6NTWLNkSaddmMRIgKT8KI5av3rSBoBkclqN1/BOKxXMHojlmYfldeA/7IcpHtNTd+Ym6lJR9rLDIj3N3ZmTWN5xrNGT1XgspijxoSfiWCckrJ/KDGQNL8FMY4aOtY4EME9TJpbY8b3mQ7cNa7qzCSKOV38zaT/7TNwMXDLsEdk/nBMoxsD9JB/AHQujnhU2ve4qfahKLuRU/zmbu6usvTa4Wvnk37EGbBhv3uJTZ8vjChSot+sYrvlu3FKcdtqmWDyT/md9q6lWXodVGyaRWf6byaY0uPc9l/wFqCkAey20y0vmU0pTbpOKjP0luv2vAqkG8wchD9n5GciXYRVsXWeUta3vgty0njLVIeqdheoDNiHjekC8oYpZs8W9p7tuYgT8mgqqSQ3UGGbw==\",\"associated_data\":\"transaction\",\"nonce\":\"EeTkoX9UWzHL\"}}";
        String ret = decryptPayCallbackToString(jsonStr);
        System.out.println(ret);


        int maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
        System.out.println("Max key length: " + maxKeyLen);  // 应该输出2147483647
    }
}
