package com.ddbb.extapi.wx;

public class WxPayConstants {
    /**
     * 主域名
     */
    private static String MAIN_DOMAIN = "https://api.mch.weixin.qq.com";
    /**
     * 备用域名
     */
    private static String STANDBY_DOMAIN = "https://api.mch.weixin.qq.com";

    /**
     * 小程序appId
     */
    public static String SMALL_APP_ID = "wx76acad0bedba74f7";
    /**
     * 小程序的secret
     */
    public static String SMALL_APP_SECRET = "ad1e9515677612c577e953a738c60a63";
    /**
     * 商户API证书序列号serial_no
     */
    public static String SERIAL_NO = "50F80CC236A91E19E1A35203BF0E61005CB42648";
    /**
     * 商户号
     */
    public static String MCH_ID = "1715041370";


    public static class WholeUrl{
        /**
         * 下单的完整url
         */
        public static String PLACE_ORDER = MAIN_DOMAIN+OperationUrl.PLACE_ORDER;
        /**
         * 获取openid的完成url
         */
        public static String GET_OPENID = "https://api.weixin.qq.com/sns/jscode2session";
    }
    /**
     * 操作的uri
     */
    public static class OperationUrl{
        /**
         * 下单
         */
        public static String PLACE_ORDER = "/v3/pay/transactions/jsapi";
    }
}
