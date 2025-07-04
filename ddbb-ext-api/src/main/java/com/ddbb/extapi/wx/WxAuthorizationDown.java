package com.ddbb.extapi.wx;

import com.ddbb.internal.utils.IVerifyResponseService;
import com.ddbb.internal.utils.ObjectConverter;
import com.ddbb.internal.utils.SnowflakeIdUtil;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Optional;


/**
 * 微信接口响应或回调验签
 * 如何使用平台证书验签名
 * https://pay.weixin.qq.com/doc/v3/merchant/4013053420
 *
 * https://blog.csdn.net/qq_43377237/article/details/103896951
 */
@Slf4j
@Service
public class WxAuthorizationDown implements IVerifyResponseService, InitializingBean {
    private static final String ENCODING = "UTF-8";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    @Autowired
    private WxConfig wxConfig;

    private static PublicKey PUBLIC_KEY = null;
    /**
     * BEGIN CERTIFICATE格式解析密钥
     * @Return: java.security.PublicKey

    private static String getCerToPublicKey1() throws FileNotFoundException, CertificateException {
        FileInputStream file = new FileInputStream("D://publicKey.cer");

        CertificateFactory ft = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) ft.generateCertificate(file);
        PublicKey publicKey = certificate.getPublicKey();

        String strKey = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getEncoder().encodeToString(publicKey.getEncoded())
                + "\n-----END PUBLIC KEY-----";
        System.out.println(strKey);
        return strKey;
    }*/

    /**
     * 从微信商户平台上，下载的平台证书（api安全）
     * @return
     * @throws Exception
     */
    private PublicKey getCerToPublicKey() throws Exception {
        //File file = ResourceUtils.getFile("classpath:wechatpay_4E221BD0951FCC4D6F495E7037165D5A4D7F3381.pem");
        File file = new File(wxConfig.getPemFolder()+"\\wechatpay_4E221BD0951FCC4D6F495E7037165D5A4D7F3381.pem");
        FileInputStream inputStream = new FileInputStream(file);

        CertificateFactory ft = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) ft.generateCertificate(inputStream);
        PublicKey publicKey = certificate.getPublicKey();

        return publicKey;
    }

    /**
     * 验证签名
     * @param verifyData 验签字符串
     * @param sign      微信返回的签名，base64解密后的内容
     * @param publicKey 微信商户平台下载的平台证书公钥
     * @return
     */
    private static boolean verify256(String verifyData, byte[] sign, PublicKey publicKey){
        if(verifyData == null || sign == null || publicKey == null){
            return false;
        }

        try {
            Signature signetcheck = Signature.getInstance(SIGNATURE_ALGORITHM);
            signetcheck.initVerify(publicKey);
            signetcheck.update(verifyData.getBytes("UTF-8"));
            return signetcheck.verify(sign);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验签http的返回
     *
     * @param response
     * @return
     */
    @Override
    public Pair<Boolean, String> verifyResponse(Response response){
        try{
            //1、您应先检查 HTTP 头 Wechatpay-Serial 的内容是否跟商户当前所持有的微信支付平台证书的序列号一致
            String weChatpaySerial=response.header("Wechatpay-Serial");

            //2、防【重放攻击】：在验证签名之前，商户系统应检查时间戳是否已过期。我们建议商户系统允许最多5分钟的时间偏差
            //HTTP 头 Wechatpay-Timestamp 中的应答时间戳
            String wxTs = response.header("Wechatpay-Timestamp");

            //HTTP 头 Wechatpay-Nonce 中的应答随机串
            String wxNonce = response.header("Wechatpay-Nonce");

            String body = response.body().string();

            //微信支付的应答签名通过 HTTP 头 Wechatpay-Signature 传递
            String wxSignature = response.header("Wechatpay-Signature");

            return doVerify(WxVerifyObject.builder().weChatpaySerial(weChatpaySerial)
                    .wxTs(wxTs).wxNonce(wxNonce).body(body).wxSignature(wxSignature)
                    .type(2).traceId(SnowflakeIdUtil.getInstance().nextId()).build());
        }catch (Exception e){
            log.error("verifyWxResponse error: "+e.getMessage(),e);
        }

        return NOT_PASS;
    }
    /*public Pair<Boolean, String> verifyResponse(Response response){
        try{
            //1、您应先检查 HTTP 头 Wechatpay-Serial 的内容是否跟商户当前所持有的微信支付平台证书的序列号一致
            String weChatpaySerial=response.header("Wechatpay-Serial");
            if(!WxPayConstants.WX_PLATFORM_SERIAL_NO.equals(weChatpaySerial)){
                return NOT_PASS;
            }
            //2、防【重放攻击】：在验证签名之前，商户系统应检查时间戳是否已过期。我们建议商户系统允许最多5分钟的时间偏差
            //HTTP 头 Wechatpay-Timestamp 中的应答时间戳
            String wxTs = response.header("Wechatpay-Timestamp");
            if(StringUtils.isBlank(wxTs)){
                return NOT_PASS;
            }
            long longWxTs = Long.parseLong(wxTs);
            long now = System.currentTimeMillis() / 1000;

            if(now < longWxTs || now - longWxTs > 300){
                return NOT_PASS;
            }
            //HTTP 头 Wechatpay-Nonce 中的应答随机串
            String wxNonce = response.header("Wechatpay-Nonce");

            String body = response.body().string();
            StringBuffer verifyData = new StringBuffer();
            verifyData.append(wxTs).append("\n")
                    .append(wxNonce).append("\n")
                    .append(body).append("\n");

            //微信支付的应答签名通过 HTTP 头 Wechatpay-Signature 传递
            String wxSignature = response.header("Wechatpay-Signature");
            //使用 base64 解码 Wechatpay-Signature 字段值，得到应答签名。
            byte[] wxSignatureDecodeByte = Base64.getDecoder().decode(wxSignature);

            boolean b = verify256(verifyData.toString(),wxSignatureDecodeByte,PUBLIC_KEY);
            if(b){
                return Pair.of(true,body);
            }
        }catch (Exception e){
            log.error("verifyWxResponse error: "+e.getMessage(),e);
        }

        return NOT_PASS;
    }*/

    /**
     * 验签微信主动回调
     * 注意：使用了 @RequestBody 注解后，无法再通过IOUtils.toString()获取body，和response.body().string()一样
     * 因此，无论是否使用了@RequestBody 注解，都要求调用方把body放在request.setAttribute("requestBodyXxx","...")中
     * @param request
     * @return
     */
    @Override
    public Pair<Boolean, String> verifyRequest(HttpServletRequest request) {
        try{
            //1、您应先检查 HTTP 头 Wechatpay-Serial 的内容是否跟商户当前所持有的微信支付平台证书的序列号一致
            String weChatpaySerial=request.getHeader("Wechatpay-Serial");

            //2、防【重放攻击】：在验证签名之前，商户系统应检查时间戳是否已过期。我们建议商户系统允许最多5分钟的时间偏差
            //HTTP 头 Wechatpay-Timestamp 中的应答时间戳
            String wxTs = request.getHeader("Wechatpay-Timestamp");

            //HTTP 头 Wechatpay-Nonce 中的应答随机串
            String wxNonce = request.getHeader("Wechatpay-Nonce");
            //{"summary":"支付成功","event_type":"TRANSACTION.SUCCESS","create_time":"2025-07-05T03:47:11+08:00","resource":{"original_type":"transaction","algorithm":"AEAD_AES_256_GCM","ciphertext":"70DJY8uDO9MXDJBk3CjWt7lDbu83v+bpx7lG5VWYCwDTVw7NmGMCKTPoX4U+OhfiPyMLMx1IOvnjzHzeRklJ3DYCJrnfMo+VoaDJBkn5586Zefno/0CFhHJOC0lpPo88Iwm4z+896ITrxGTqiTfsfLxq/vfCYxlHB5QZ5a7L7aFm6E48hEsi6JeuVcU9/sWT8EtGCd7413gMKnYW+rGWzwmX2PJYMe3SAXNQF8IKYlhtxFKLIim0b1Zz63Ni9N1rHPwB5R9T6zJ5HmCEQgScl/zajth2aQ8Bg+adIRT5HO99EI3xbBd+MTAsk7gb66YLy0MXzWCb/YH/8n8V8QbwioAh9WXWkFjaK3OK4kxbVJmhc8PBrW6bWNN3eh4fUC3fSuQTPgwBm/Tdcta+m/pRPRs4KqpUOn6PJweGbvyk+pWhhMfXO3aNtGdwmaHi9CU438Y7EpjDdDNVHUXc3XfJMi5zZBtQYw872sd8VshOBCTK2joN0MIJxkYk7B7acZH2zePwWsWS27M7LVMuuA7bvidjTzwHl5ao60YexxL9h2B7UpBjj4i5EdMV8SgY042XCNRZqA==","associated_data":"transaction","nonce":"k2X2ESSWMuSk"},"resource_type":"encrypt-resource","id":"49bf0c81-5ca5-52f2-b16f-b7d50247c8dc"}
            //String body = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            String body = Optional.ofNullable(request.getAttribute("requestBodyXxx")).map(Object::toString).orElse("");
            if(StringUtils.isBlank(body)){
                throw new Exception("you must invoke request.setAttribute(\"requestBodyXxx\",\"...\")");
            }
            //微信支付的应答签名通过 HTTP 头 Wechatpay-Signature 传递
            String wxSignature = request.getHeader("Wechatpay-Signature");

            return doVerify(WxVerifyObject.builder().weChatpaySerial(weChatpaySerial)
                    .wxTs(wxTs).wxNonce(wxNonce).body(body).wxSignature(wxSignature)
                    .type(1).traceId(SnowflakeIdUtil.getInstance().nextId()).build());
        }catch (Exception e){
            log.error("verifyWxRequest error: "+e.getMessage(),e);
        }

        return NOT_PASS;
    }

    private Pair<Boolean, String> doVerify(WxVerifyObject obj){
        long traceId = obj.getTraceId();
        log.info("[doVerify] >>> traceId:{}, start: {}", traceId,ObjectConverter.o2s(obj));
        try{
            //1、您应先检查 HTTP 头 Wechatpay-Serial 的内容是否跟商户当前所持有的微信支付平台证书的序列号一致
            String weChatpaySerial = obj.getWeChatpaySerial();
            if(!WxPayConstants.WX_PLATFORM_SERIAL_NO.equals(weChatpaySerial)){
                log.error("[doVerify] >>> traceId:{}, error: weChatpaySerial wrong",traceId);
                return NOT_PASS;
            }
            //2、防【重放攻击】：在验证签名之前，商户系统应检查时间戳是否已过期。我们建议商户系统允许最多5分钟的时间偏差
            //HTTP 头 Wechatpay-Timestamp 中的应答时间戳
            String wxTs = obj.getWxTs();
            if(StringUtils.isBlank(wxTs)){
                log.error("[doVerify] >>> traceId:{}, error: wxTs is blank",traceId);
                return NOT_PASS;
            }
            long longWxTs = Long.parseLong(wxTs);
            long now = System.currentTimeMillis() / 1000;

            if(now < longWxTs || now - longWxTs > 300){
                log.error("[doVerify] >>> traceId:{}, error: wxTs wrong",traceId);
                return NOT_PASS;
            }
            //HTTP 头 Wechatpay-Nonce 中的应答随机串
            String wxNonce = obj.getWxNonce();

            String body = obj.getBody();
            StringBuffer verifyData = new StringBuffer();
            verifyData.append(wxTs).append("\n")
                    .append(wxNonce).append("\n")
                    .append(body).append("\n");

            //微信支付的应答签名通过 HTTP 头 Wechatpay-Signature 传递
            String wxSignature = obj.getWxSignature();
            //使用 base64 解码 Wechatpay-Signature 字段值，得到应答签名。
            byte[] wxSignatureDecodeByte = Base64.getDecoder().decode(wxSignature);

            boolean b = verify256(verifyData.toString(),wxSignatureDecodeByte,PUBLIC_KEY);
            if(b){
                log.info("[doVerify] >>> traceId:{}, verify256 success",traceId);
                return Pair.of(true,body);
            }
            log.error("[doVerify] >>> traceId:{}, verify256 failed - not pass",traceId);

        }catch (Exception e){
            log.error("doVerify error: "+e.getMessage(),e);
        }

        return NOT_PASS;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try{
            PUBLIC_KEY = getCerToPublicKey();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 微信验签对象
      */
    @Data
    @Builder
    private static class WxVerifyObject{
        /**
         * 平台证书的序列号
         */
        private String weChatpaySerial;
        /**
         * 时间戳
         */
        private String wxTs;
        /**
         * 随机串
         */
        private String wxNonce;
        /**
         * wx的签名
         */
        private String wxSignature;
        /**
         * request或response中的body
         */
        private String body;
        private Integer type;
        private Long traceId;
    }
}
