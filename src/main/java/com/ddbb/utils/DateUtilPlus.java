package com.ddbb.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtilPlus {

    public static final String DATE_TIME_FORMATTER_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMATTER_PATTERN = "yyyy-MM-dd";
    public static final String TIME_FORMATTER_PATTERN = "HH:mm:ss";
    public static final SimpleDateFormat S_DATE_TIME_FORMATTER = new SimpleDateFormat(DATE_TIME_FORMATTER_PATTERN);
    public static final SimpleDateFormat S_DATE_FORMATTER = new SimpleDateFormat(DATE_FORMATTER_PATTERN);
    public static final SimpleDateFormat S_TIME_FORMATTER = new SimpleDateFormat(TIME_FORMATTER_PATTERN);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_PATTERN);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMATTER_PATTERN);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMATTER_PATTERN);

    /**
     * 返回当前日期: 2025-04-12
     * @return
     */
    public static String getCurrentDateTimeString(){
        return LocalDateTime.now().format(DATE_FORMATTER);
    }
    /**
     * 返回当前时间: 11:29:13
     */
    public static String getCurrentTimeString(){
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

    /**
     * 返回当前日期 + 时间: 2025-04-12 11:29:13
     * @return
     */
    public static String getCurrentDateString(){
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    /**
     * 字符串转java.util.Date
     * @param dateStr 必须为 yyyy-MM-dd
     * @return
     */
    public static Date stringYmd2UtilDate(String dateStr){
        try{
            return DateUtils.parseDate(dateStr,DATE_FORMATTER_PATTERN);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转java.util.Date
     * @param dateTimeStr 必须为 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date stringYmdhms2UtilDate(String dateTimeStr){
        try{
            return DateUtils.parseDate(dateTimeStr,DATE_TIME_FORMATTER_PATTERN);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转LocalDate
     * @param dateStr 格式必须为：2025-01-01
     * @return
     */
    public static LocalDate string2LocalDate(String dateStr){
        LocalDate ld = LocalDate.parse(dateStr,DATE_FORMATTER);
        return ld;
    }
    /**
     * 字符串转LocalDateTime
     * @param dateTimeStr 格式必须为：2025-01-01 12:03:43
     * @return
     */
    public static LocalDateTime string2LocalDateTime(String dateTimeStr){
        LocalDateTime ldt = LocalDateTime.parse(dateTimeStr,DATE_TIME_FORMATTER);
        return ldt;
    }

    /**
     * 获取当前年、月、日的int
     * @return year = 2025, month = 4(从1开始的), day = 12, hour = 18, minute = 20, second = 2
     */
    public static int[] getIntYearMonthDayHourMinuteSecond(){
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();

        System.out.printf("year = %d, month = %d, day = %d, hour = %d, minute = %d, second = %d", year, month, day,hour,minute,second);
        System.out.println();
        int[] ret = {year,month,day,hour,minute,second};
        return ret;
    }

    /**
     * java.util.date 转 LocalDateTime
     * @param date
     * @return
     */
    public static LocalDateTime utilDate2LocalDateTime(java.util.Date date){
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime;
    }

    /**
     * java.util.date 转 string
     * @param date
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String utilDate2StringYmdHms(java.util.Date date){
        LocalDateTime ldt = utilDate2LocalDateTime(date);
        return localDateTime2StringYmdHms(ldt);
    }
    /**
     * java.util.date 转 string
     * @param date
     * @return yyyy-MM-dd
     */
    public static String utilDate2StringYmd(java.util.Date date){
        LocalDateTime ldt = utilDate2LocalDateTime(date);
        return localDateTime2StringYmd(ldt);
    }

    /**
     * LocalDate 转 java.util.Date
     * @param localDate
     * @return
     */
    public static Date localDate2UtilDate(LocalDate localDate){
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        java.util.Date date = Date.from(instant);
        return date;
    }

    /**
     * LocalDate 转string
     * @param localDate
     * @return yyyy-MM-dd
     */
    public static String localDate2String(LocalDate localDate){
        String time1= localDate.format(DATE_FORMATTER);
        return time1;
    }

    /**
     * LocalDateTime 转 java.util.Date
     * @param localDateTime
     * @return
     */
    public static Date localDateTime2UtilDate(LocalDateTime localDateTime){
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        java.util.Date date = Date.from(instant);
        return date;
    }

    /**
     * LocalDateTime 转 LocalDate
     * @param now
     * @return
     */
    public static LocalDate localDateTime2LocalDate(LocalDateTime now){
        LocalDate localDate = now.toLocalDate();
        return localDate;
    }

    /**
     * LocalDateTime转string
     * @param ldt
     * @return 2015-03-01 23:09:21
     */
    public static String localDateTime2StringYmdHms(LocalDateTime ldt){
        String time1= ldt.format(DATE_TIME_FORMATTER);
        return time1;
    }

    /**
     * LocalDateTime转string
     * @param ldt
     * @return 2015-03-01
     */
    public static String localDateTime2StringYmd(LocalDateTime ldt){
        String time1= ldt.format(DATE_FORMATTER);
        return time1;
    }

    /**
     * 获取今天开始时刻
     * @return 2022-12-30 00:00:00
     */
    public static LocalDateTime getBeginOfToday(){
        //获取当前时间
        LocalDateTime nowTime = LocalDateTime.now();
        //获取当前日期
        LocalDate nowDate = LocalDate.now();
        //设置零点
        LocalDateTime beginTime = LocalDateTime.of(nowDate, LocalTime.MIN);
        return beginTime;
    }
    /**
     * 获取今天开始时刻
     * @return 2022-12-30 00:00:00
     */
    public static String getBeginOfTodayString(){
        //获取当前时间
        LocalDateTime ldt = getBeginOfToday();
        //将时间进行格式化
        return localDateTime2StringYmdHms(ldt);
    }

    /**
     * 获取今天最后一刻
     * @return 2022-12-30 23:59:59
     */
    public static LocalDateTime getEndOfToday(){
        //获取当前时间
        LocalDateTime nowTime = LocalDateTime.now();
        //获取当前日期
        LocalDate nowDate = LocalDate.now();
        //设置零点
        LocalDateTime beginTime = LocalDateTime.of(nowDate, LocalTime.MAX);
        return beginTime;
    }
    /**
     * 获取今天最后一刻
     * @return 2022-12-30 23:59:59
     */
    public static String getEndOfTodayString(){
        //获取当前时间
        LocalDateTime ldt = getEndOfToday();
        //将时间进行格式化
        return localDateTime2StringYmdHms(ldt);
    }

    public static int getNextHour(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime plus = now.plusHours(1);
        return plus.getHour();
    }


    public static void main(String[] args) {
        System.out.println(getCurrentTimeString());
        System.out.println(getCurrentDateString());
        System.out.println(getCurrentDateTimeString());

        int[] ymd = getIntYearMonthDayHourMinuteSecond();
        System.out.println("year: "+ymd[0]+", month: "+ymd[1]+", day: "+ymd[2]+", hour: "+ymd[3]+", minute: "+ymd[4]+", second: "+ymd[5]);

        System.out.println(stringYmd2UtilDate("2025-04-15"));
        System.out.println(stringYmdhms2UtilDate("2025-04-12 15:00:01"));

        Date d1 = stringYmdhms2UtilDate("2025-04-12 15:00:01");
        Date d2 = stringYmdhms2UtilDate("2025-04-12 15:00:00");
        System.out.println("d1: "+d1+" is after d1: "+d2+" ? "+d1.after(d2));

        System.out.println(string2LocalDate("2025-03-30"));
        System.out.println(string2LocalDateTime("2025-03-30 12:44:21"));

        System.out.println(getBeginOfTodayString());
        System.out.println(getEndOfTodayString());


        String aDate = "2013-09-09";
        LocalDate ld = string2LocalDate(aDate);
        System.out.println(localDate2String(ld));

        System.out.println(getNextHour());

        LocalDateTime ldt = LocalDateTime.now().with(LocalTime.of(23,9));
        System.out.println(ldt.plusHours(1).getHour());
    }
}
