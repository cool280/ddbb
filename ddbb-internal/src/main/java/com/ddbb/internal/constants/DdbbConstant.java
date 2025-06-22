package com.ddbb.internal.constants;

public class DdbbConstant {
    /**
     * 系统账号uid
     */
    public static Long SYS_UID = 10000L;

    /**
     * geo查询的默认值
     */
    public static class GeoQueryDefault{
        /**
         * 最多数量
         */
        public static int MAX_COUNT = 50;
        /**
         * 最小距离km
         */
        public static double MIN_DISTANCE_KM = 0;
        /**
         * 最大距离km
         */
        public static double MAX_DISTANCE_KM = 20;

        //默认经纬度：杨浦百联滨江
        public static double DEFAULT_LONGITUDE = 121.544732;
        public static double DEFAULT_LATITUDE = 31.271186;
    }
}
