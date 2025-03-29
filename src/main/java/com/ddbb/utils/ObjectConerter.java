package com.ddbb.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class ObjectConerter {
    private static Gson GSON = null;
    static {
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        GSON = builder.create();
    }

    public static <T> T s2o(String json,Class<T> target){
        return GSON.fromJson(json,target);
    }

    public static String o2s(Object o){return GSON.toJson(o);}

    public static <T> T m2o(Map<String,String> map, Class<T> target){
        if(map == null || map.size() == 0){
            return null;
        }

        String json = GSON.toJson(map);
        return s2o(json,target);
    }

}
