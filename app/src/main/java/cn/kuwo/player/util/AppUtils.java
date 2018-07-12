package cn.kuwo.player.util;

import android.content.Context;

public final class AppUtils {
    private AppUtils() {
    }
    private static long mLastClickTime;// 用户判断多次点击的时间
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (Math.abs(time - mLastClickTime) < 1000) {
            return true;
        }
        mLastClickTime = time;
        return false;
    }
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }
}