package cn.kuwo.player.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

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


    public static Date getCurrentDate() {
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
    public static int  getWeekNumber() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return 7;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
            default:
                return -1;
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