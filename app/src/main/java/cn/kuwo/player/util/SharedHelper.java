package cn.kuwo.player.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedHelper {

    private static Context mContext;

    public SharedHelper() {
    }

    public SharedHelper(Context mContext) {
        this.mContext = mContext;
    }


    //定义一个保存数据的方法
    public static void save(String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    //定义一个保存数据的方法
    public static void saveBoolean(String key, boolean value) {
        SharedPreferences sp = mContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    //定义一个读取SP文件的方法
    public static String read(String key) {
        SharedPreferences sp = mContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        String data = sp.getString(key, "");
        return data;
    }
    //定义一个读取SP文件的方法
    public static Boolean readBoolean(String key) {
        SharedPreferences sp = mContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        boolean data = sp.getBoolean(key, false);
        return data;
    }
    //定义一个保存数据的方法
    public static void saveInt(String key, int value) {
        SharedPreferences sp = mContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    //定义一个读取SP文件的方法
    public static int readInt(String key) {
        SharedPreferences sp = mContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        int data = sp.getInt(key, 0);
        return data;
    }
}
