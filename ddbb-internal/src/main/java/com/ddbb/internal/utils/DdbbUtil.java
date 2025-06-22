package com.ddbb.internal.utils;

public class DdbbUtil {
    private static final int EARTH_RADIUS = 6371; // 地球半径，单位为千米

    /**
     * 判断两个数字区间是否有重叠
     * @param interval1 区间1数组，0 - 区间小值，1 - 区间大值
     * @param interval2 区间2数组，0 - 区间小值，1 - 区间大值
     * @param equalAsOverlap 区间极值相等是否算重叠，如[1,3] [3,9]，true算重叠，false不算
     *
     * @return
     */
    public static boolean isOverlap(int[] interval1, int[] interval2,boolean equalAsOverlap) {
        // 检查区间是否有重叠
        if(equalAsOverlap){
            return Math.max(interval1[0], interval2[0]) <= Math.min(interval1[1], interval2[1]);
        }
        return Math.max(interval1[0], interval2[0]) < Math.min(interval1[1], interval2[1]);
    }

    /**
     * 计算两个经纬度之间的距离（单位：km）
     * @param lon1
     * @param lat1
     * @param lon2
     * @param lat2
     * @return （单位：km）
     */
    public static double calculateDistanceKm(double lon1, double lat1, double lon2, double lat2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceKm = EARTH_RADIUS * c;

        return distanceKm;
    }

    public static void main(String[] args) {
        int[] interval1 = {1, 3};
        int[] interval2 = {3, 7};
        // 调用 isOverlap 方法判断两个区间是否重叠
        boolean result = isOverlap(interval1, interval2,true);
        System.out.println("两个区间是否重叠: " + result);
        result = isOverlap(interval1, interval2,false);
        System.out.println("两个区间是否重叠: " + result);


        System.out.println(calculateDistanceKm(116.36115,39.951008,116.382997,39.873084));
    }
}
