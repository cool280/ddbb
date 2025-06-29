package com.ddbb.extapi.wx;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.internal.utils.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;


/**
 * 请求微信接口时的签名
 * 请求参数里带Body参数(包体参数），如何计算签名
 * https://pay.weixin.qq.com/doc/v3/merchant/4012365336
 */
@Slf4j
public class WxAuthorizationUp {
    private static final String ENCODING = "UTF-8";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public static String getAuthorization(String url, JSONObject bodyParam){
        Long ts = System.currentTimeMillis() / 1000;
        String nonceStr = String.valueOf(SnowflakeIdUtil.getInstance().nextId());

        StringBuffer sb = new StringBuffer();
        sb.append("POST\n")
                .append(url).append("\n")
                .append(ts).append("\n")
                .append(nonceStr).append("\n")
                .append(bodyParam.toJSONString()).append("\n");

        byte[] b = new byte[0];
        try {
            b = sign256(sb.toString(),generatePrivateKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String signStr = Base64.getEncoder().encodeToString(b);

        StringBuffer ret = new StringBuffer();
        ret.append("WECHATPAY2-SHA256-RSA2048 mchid=\"").append(WxPayConstants.MCH_ID).append("\",nonce_str=\"").append(nonceStr)
                .append("\",signature=\"").append(signStr).append("\",timestamp=\"").append(ts)
                .append("\",serial_no=\"").append(WxPayConstants.SERIAL_NO).append("\"");

        return ret.toString();
    }

    private static PrivateKey generatePrivateKey() throws Exception {
        // // Step 1: Read the content of the PEM file
        InputStream inputStream = WxAuthorizationUp.class.getClassLoader().getResourceAsStream("wx_apiclient_key.pem");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String pemContent = sb.toString();

        // Step 2: Remove PEM header and footer and decode Base64
        String privateKeyPem = pemContent.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", ""); // Remove any whitespace characters
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPem);
        // Step 3: Convert the byte array to a PrivateKey object
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" depending on the key type
        return kf.generatePrivate(spec);
    }
    /**
     * SHA256WithRSA签名
     * @param data
     * @param privateKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     */
    public static byte[] sign256(String data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
            SignatureException, UnsupportedEncodingException {

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

        signature.initSign(privateKey);

        signature.update(data.getBytes(ENCODING));

        return signature.sign();
    }

    public static boolean verify256(String data, byte[] sign, PublicKey publicKey){
        if(data == null || sign == null || publicKey == null){
            return false;
        }

        try {
            Signature signetcheck = Signature.getInstance(SIGNATURE_ALGORITHM);
            signetcheck.initVerify(publicKey);
            signetcheck.update(data.getBytes("UTF-8"));
            return signetcheck.verify(sign);
        } catch (Exception e) {
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
