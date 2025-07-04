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
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;


/**
 * 微信接口响应或回调验签
 * 如何使用平台证书验签名
 * https://pay.weixin.qq.com/doc/v3/merchant/4013053420
 *
 * https://blog.csdn.net/qq_43377237/article/details/103896951
 */
@Slf4j
@Service
public class WxAuthorizationDown implements IVerifyResponseService {
    private static final String ENCODING = "UTF-8";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    private static PublicKey PUBLIC_KEY = null;
    static {
        try {
            PUBLIC_KEY = getCerToPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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
    private static PublicKey getCerToPublicKey() throws Exception {
        File file = ResourceUtils.getFile("classpath:wechatpay_4E221BD0951FCC4D6F495E7037165D5A4D7F3381.pem");
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

            String body = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());

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
                log.info("[doVerify] >>> traceId:{}, success",traceId);
                return Pair.of(true,body);
            }
            log.error("[doVerify] >>> traceId:{}, not pass",traceId);

        }catch (Exception e){
            log.error("doVerify error: "+e.getMessage(),e);
        }

        return NOT_PASS;
    }


    /**
     * 微信验签对象
      */
    @Data
    @Builder
    class WxVerifyObject{
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
