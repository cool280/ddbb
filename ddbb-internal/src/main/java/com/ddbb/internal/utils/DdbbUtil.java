package com.ddbb.internal.utils;

public class DdbbUtil {
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

    public static void main(String[] args) {
        int[] interval1 = {1, 3};
        int[] interval2 = {3, 7};
        // 调用 isOverlap 方法判断两个区间是否重叠
        boolean result = isOverlap(interval1, interval2,true);
        System.out.println("两个区间是否重叠: " + result);
        result = isOverlap(interval1, interval2,false);
        System.out.println("两个区间是否重叠: " + result);
    }
}
