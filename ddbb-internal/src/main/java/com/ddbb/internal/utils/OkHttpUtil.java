package com.ddbb.internal.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttpUtil {
    private static final OkHttpClient okHttpClient = new OkHttpClient();
    private static final OkHttpClient okHttpClientUnsafeHttps = getUnsafeOkHttpClient();


    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // 创建一个信任所有证书的TrustManager
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            // 创建一个不验证证书的 SSLContext，并使用上面的TrustManager初始化
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // 使用上面创建的SSLContext创建一个SSLSocketFactory
            javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            builder.readTimeout(1, TimeUnit.MINUTES);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws Exception {

        // 发送请求
        Request request = new Request.Builder()
                .url("https://example.com")
                .build();

        Response response = getUnsafeOkHttpClient().newCall(request).execute();
        System.out.println(response.body().string());
    }

    /**
     * get请求。参数请拼在url后面
     * @param url
     * @return
     */
    public static String doGet(String url){
        Request request = new Request
                .Builder() //利用建造者模式创建Request对象
                .url(url) //设置请求的URL
                .build(); //生成Request对象

        Response response = null;
        try {
            //将请求添加到请求队列等待执行，并返回执行后的Response对象
            response = okHttpClient.newCall(request).execute();
            //获取Http Status Code.其中200表示成功
            if (response.code() == 200) {
                //这里需要注意，response.body().string()是获取返回的结果，此句话只能调用一次，再次调用获得不到结果。
                //所以先将结果使用result变量接收
                String result = response.body().string();
                log.info("[doGet] >>> result: "+result);
                return result;
            }else{
                log.warn("[doGet] >>> resultCode is not 200, but: "+response.code());
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[doGet] >>> with error: "+e.getMessage());
            return "";
        } finally {
            if (response != null) {
                response.body().close();
            }
        }
    }

    public static String doPost(String url, JSONObject jsonObject){
        return doPost(url,jsonObject,null);
    }

    public static String doPostWithHeader(String url, JSONObject jsonObject,Map<String,String> headersMap){
        return doPost(url,jsonObject,headersMap);
    }


    public static String doPost(String url, JSONObject jsonObject,Map<String,String> headersMap){
        try{
            Response response1 = doPostResponseReturned(url, jsonObject, headersMap);
            //获取Http Status Code.其中200表示成功
            if (response1.code() == 200) {
                //这里需要注意，response.body().string()是获取返回的结果，此句话只能调用一次，再次调用获得不到结果。
                //所以先将结果使用result变量接收
                String result = response1.body().string();
                log.info("[doPost] >>> result: "+result);
                return result;
            }else{
                log.warn("[doPost] >>> resultCode is not 200, but: "+response1.code());
                String result = response1.body().string();
                log.warn("[doPost] >>> response.body(): "+result);
                return "";
            }
        }catch (Exception e) {
            e.printStackTrace();
            log.error("[doPost] >>> with error: "+e.getMessage());
            return "";
        }



    }
    public static Response doPostResponseReturned(String url, JSONObject jsonObject,Map<String,String> headersMap){
        String json = jsonObject.toJSONString();
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .headers(buildHeader(headersMap))
                .build();
        Response response = null;
        try{
            Call call;
            if(url.indexOf("https")>=0){
                call = okHttpClientUnsafeHttps.newCall(request);
            }else{
                call = okHttpClient.newCall(request);
            }
            response = call.execute();
            return response;
            //获取Http Status Code.其中200表示成功
            /*
            if (response.code() == 200) {
                //这里需要注意，response.body().string()是获取返回的结果，此句话只能调用一次，再次调用获得不到结果。
                //所以先将结果使用result变量接收
                String result = response.body().string();
                log.info("[doPost] >>> result: "+result);
                return response;
            }else{
                log.warn("[doPost] >>> resultCode is not 200, but: "+response.code());
                String result = response.body().string();
                log.warn("[doPost] >>> response.body(): "+result);
                return null;
            }
             */
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[doPost] >>> with error: "+e.getMessage());
            return null;
        } finally {
            if (response != null) {
                response.body().close();
            }
        }

    }

    private static Headers buildHeader(Map<String,String> map){
        Headers headers =null;
        Headers.Builder headersBuilder = new Headers.Builder();
        if (map != null && map.size()>0) {
            map.forEach((k,v)->{
                headersBuilder.add(k, v);
            });
        }
        headers = headersBuilder.build();
        return headers;
    }
}
