package com.ddbb.test;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpURLConnectionUtil {
    private static String myLocal = "https://localhost:8080";
    private static String ali = "https://www.ddbb365.com:8080";


    /**
     * http get请求
     * @param httpUrl 链接
     * @return 响应数据
     */
    public static String doGet(String httpUrl){
        //链接
        HttpURLConnection connection=null;
        InputStream is=null;
        BufferedReader br = null;
        StringBuffer result=new StringBuffer();
        try {
            //创建连接
            URL url=new URL(httpUrl);
            connection= (HttpURLConnection) url.openConnection();
            //设置请求方式
            connection.setRequestMethod("GET");
            //设置连接超时时间
            connection.setConnectTimeout(15000);
            //设置读取超时时间
            connection.setReadTimeout(15000);
            //开始连接
            connection.connect();
            //获取响应数据
            if(connection.getResponseCode()==200){
                //获取返回的数据
                is=connection.getInputStream();
                if(is!=null){
                    br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    String temp = null;
                    while ((temp=br.readLine())!=null){
                        result.append(temp);
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            connection.disconnect();// 关闭远程连接
        }
        return result.toString();
    }

    /**
     * post请求
     * @param httpUrl 链接
     * @param param 参数
     * @return
     */
    public static String doPost(String httpUrl, String param) {
        StringBuffer result=new StringBuffer();
        //连接
        HttpURLConnection connection=null;
        OutputStream os=null;
        InputStream is=null;
        BufferedReader br=null;
        try {
            //创建连接对象
            URL url=new URL(httpUrl);
            //创建连接
            connection= (HttpURLConnection) url.openConnection();
            //设置请求方法
            connection.setRequestMethod("POST");
            //设置连接超时时间
            connection.setConnectTimeout(15000);
            //设置读取超时时间
            connection.setReadTimeout(15000);
            //设置是否可读取
            connection.setDoOutput(true);
            //设置响应是否可读取
            connection.setDoInput(true);
            //设置参数类型
            connection.setRequestProperty("Content-Type", "application/json");
            //拼装参数
            if(param!=null&&!param.equals("")){
                //设置参数
                os=connection.getOutputStream();
                //拼装参数
                os.write(param.getBytes("UTF-8"));
            }
            //设置权限
            //设置请求头等
            //开启连接
            //connection.connect();
            //读取响应
            if(connection.getResponseCode()==200){
                is=connection.getInputStream();
                if(is!=null){
                    br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    String temp=null;
                    if((temp=br.readLine())!=null){
                        result.append(temp);
                    }
                }
            }else {
                return "httpCode is not 200, but: "+connection.getResponseCode();
            }
            //关闭连接
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //关闭连接
            connection.disconnect();
        }
        return result.toString();
    }
    public static void main(String[] args) {
        String uri = "/nearby/hall";

        JSONObject param = new JSONObject();
        param.put("uid",123);
        param.put("longitude",121.544732);
        param.put("latitude",31.271186);
        param.put("minDistanceKm",0);
        param.put("maxDistanceKm",123);
        param.put("maxCount",30);

        String ret = doPost(myLocal+uri,param.toJSONString());
        System.out.println("=================== localhost api returns: ===================");
        System.out.println(""+ret);

        ret = doPost(ali+uri,param.toJSONString());
        System.out.println("=================== www.ddbb365.com api returns: ===================");
        System.out.println(""+ret);
    }
}
