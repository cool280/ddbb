package com.ddbb.extapi.wx;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.internal.utils.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;


/**
 * 微信接口响应或回调验签
 * 如何使用平台证书验签名
 * https://pay.weixin.qq.com/doc/v3/merchant/4013053420
 *
 * https://blog.csdn.net/qq_43377237/article/details/103896951
 */
@Slf4j
public class WxAuthorizationDown {
    private static final String ENCODING = "UTF-8";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";


    /**
     * BEGIN CERTIFICATE格式解析密钥
     * @Return: java.security.PublicKey
     */
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
    }

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
     * 验证微信接口响应
     * @param response
     * @return
     */
    public static boolean verifyWxResponse(Response response){
        try{
            //1、您应先检查 HTTP 头 Wechatpay-Serial 的内容是否跟商户当前所持有的微信支付平台证书的序列号一致
            String weChatpaySerial=response.header("Wechatpay-Serial");
            if(!WxPayConstants.SERIAL_NO.equals(weChatpaySerial)){
                return false;
            }
            //2、在验证签名之前，商户系统应检查时间戳是否已过期。我们建议商户系统允许最多5分钟的时间偏差
            //HTTP 头 Wechatpay-Timestamp 中的应答时间戳
            String wxTs = response.header("Wechatpay-Timestamp");
            if(StringUtils.isBlank(wxTs)){
                return false;
            }
            long longWxTs = Long.parseLong(wxTs);
            long now = System.currentTimeMillis() / 1000;

            if(now < longWxTs || now - longWxTs > 300){
                return false;
            }
            //HTTP 头 Wechatpay-Nonce 中的应答随机串
            String wxNonce = response.header("Wechatpay-Nonce");

            StringBuffer verifyData = new StringBuffer();
            verifyData.append(wxTs).append("\n")
                    .append(wxNonce).append("\n")
                    .append(response.body().string()).append("\n");

            //微信支付的应答签名通过 HTTP 头 Wechatpay-Signature 传递
            String wxSignature = response.header("Wechatpay-Signature");
            //使用 base64 解码 Wechatpay-Signature 字段值，得到应答签名。
            byte[] wxSignatureDecodeByte = Base64.getDecoder().decode(wxSignature);

            return verify256(verifyData.toString(),wxSignatureDecodeByte,getCerToPublicKey());
        }catch (Exception e){
            log.error("verifyWxResponse error: "+e.getMessage(),e);
            return false;
        }

    }
    /**
     * 二进制数据编码为BASE64字符串
     * @param data
     * @return
     */
//    public static String encodeBase64(byte[] bytes){
//        return new String(Base64.encodeBase64(bytes));
//    }

    /**
     * BASE64解码
     * @param bytes
     * @return
     */
//    public static byte[] decodeBase64(byte[] bytes) {
//        byte[] result = null;
//        try {
//            result = Base64.decodeBase64(bytes);
//        } catch (Exception e) {
//            return null;
//        }
//        return result;
//    }

}
