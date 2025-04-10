package com.ddbb.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
public class OkHttpUtil {
    private static final OkHttpClient okHttpClient = new OkHttpClient();

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
        String json = jsonObject.toJSONString();
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try{
            Call call = okHttpClient.newCall(request);
            response = call.execute();

            //获取Http Status Code.其中200表示成功
            if (response.code() == 200) {
                //这里需要注意，response.body().string()是获取返回的结果，此句话只能调用一次，再次调用获得不到结果。
                //所以先将结果使用result变量接收
                String result = response.body().string();
                log.info("[doPost] >>> result: "+result);
                return result;
            }else{
                log.warn("[doPost] >>> resultCode is not 200, but: "+response.code());
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[doPost] >>> with error: "+e.getMessage());
            return "";
        } finally {
            if (response != null) {
                response.body().close();
            }
        }

    }
}
