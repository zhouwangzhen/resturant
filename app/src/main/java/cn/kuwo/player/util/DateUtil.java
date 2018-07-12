package cn.kuwo.player.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    /*
  获取时间间隔（单位分钟）
 */
    public static String TimeInterval(Date startDate) {
        long interval = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        interval = (currentDate.getTime() - startDate.getTime()) / 1000 / 60;
        return interval + "分钟";
    }

    public static Date getTodayZero() {
        Date date = new Date();
        long l = 24 * 60 * 60 * 1000; //每天的毫秒数
        //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
        //减8个小时的毫秒值是为了解决时区的问题。
        return new Date(date.getTime() - (date.getTime() % l) - 8 * 60 * 60 * 1000);
    }

    public static Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String formatDate(Date date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            return format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String formatLongDate(Date date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static Date getZero(Date date) {
        long l = 24 * 60 * 60 * 1000; //每天的毫秒数
        //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
        //减8个小时的毫秒值是为了解决时区的问题。
        return new Date(date.getTime() - (date.getTime() % l) - 8 * 60 * 60 * 1000);
    }

    public static Date getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date(System.currentTimeMillis());
        return currentDate;
    }

    public static long getZeroTimeStamp(Date date) {
        long current = date.getTime();
        long zero =  current/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset();
        return zero;
    }

    public static long getLasterTimeStamp(Date date) {
        if (isNow(date)) {
            return new Date().getTime();
        } else {
            long current = date.getTime();
            long zero =  current/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset();
            long twelve=zero+24*60*60*1000-1;
            return twelve;
        }

    }
    public static long getZeroTimeStampBySecond(Date date) {
        long current = date.getTime();
        long zero =  current/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset();
        return zero/1000;
    }

    public static long getLasterTimeStampBySecond(Date date) {
        if (isNow(date)) {
            return new Date().getTime()/1000;
        } else {
            long current = date.getTime();
            long zero =  current/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset();
            long twelve=zero+24*60*60*1000-1;
            return twelve/1000;
        }

    }

    public static String getStringDateShort(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String getWeek() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }

    public static Boolean isBlackFive() {
        if (getWeek().equals("星期五")) {
            return true;
        }
        return false;
    }

    private static boolean isNow(Date date) {
        Date now = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String nowDay = sf.format(now);
        String day = sf.format(date);

        return day.equals(nowDay);


    }
}