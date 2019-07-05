package cn.kuwo.player.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * SharedPreferences相关工具类
 */
public class SpUtils {

    private static HashMap<String, SharedPreferences> sPreferencesMap = new HashMap<>();
    private static Context sContext;

    /**
     * 程序用全局设置
     */
    public static final String KEY_APPLICATION = "stay";
    /**
     * 账号用，登录态等，会被统一清空
     */
    public static final String KEY_ACCOUNT = "account";

    /**
     * 初始化，通常放在Application的onCreate
     *
     * @param context Application context
     */
    public static void init(Context context) {
        sContext = context;
    }


    public static void putInt(String key, int value) {
        putInt(key, value, KEY_APPLICATION);
    }

    public static void putInt(String key, int value, String preferencesKey) {
        getPreferences(preferencesKey).edit().putInt(key, value).apply();
    }

    public static void putLong(String key, long value) {
        putLong(key, value, KEY_APPLICATION);
    }

    public static void putLong(String key, long value, String preferencesKey) {
        getPreferences(preferencesKey).edit().putLong(key, value).apply();
    }

    public static void putFloat(String key, float value) {
        putFloat(key, value, KEY_APPLICATION);
    }

    public static void putFloat(String key, float value, String preferencesKey) {
        getPreferences(preferencesKey).edit().putFloat(key, value).apply();
    }

    public static void putBoolean(String key, boolean value) {
        putBoolean(key, value, KEY_APPLICATION);
    }

    public static void putBoolean(String key, boolean value, String preferencesKey) {
        getPreferences(preferencesKey).edit().putBoolean(key, value).apply();
    }

    public static void putString(String key, String value) {
        putString(key, value, KEY_APPLICATION);
    }

    public static void putString(String key, String value, String preferencesKey) {
        getPreferences(preferencesKey).edit().putString(key, value).apply();
    }


    public static String getString(String key) {
        return getString(key, "");
    }

    public static String getString(String key, String defaultValue) {
        return getString(key, defaultValue, KEY_APPLICATION);
    }

    public static String getString(String key, String defaultValue, String preferencesKey) {
        return getPreferences(preferencesKey).getString(key, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return getBoolean(key, defaultValue, KEY_APPLICATION);
    }

    public static boolean getBoolean(String key, boolean defaultValue, String preferencesKey) {
        return getPreferences(preferencesKey).getBoolean(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        return getInt(key, defaultValue, KEY_APPLICATION);
    }

    public static int getInt(String key, int defaultValue, String preferencesKey) {
        return getPreferences(preferencesKey).getInt(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue) {
        return getLong(key, defaultValue, KEY_APPLICATION);
    }

    public static long getLong(String key, long defaultValue, String preferencesKey) {
        return getPreferences(preferencesKey).getLong(key, defaultValue);
    }

    public static float getFloat(String key, float defaultValue) {
        return getFloat(key, defaultValue, KEY_APPLICATION);
    }

    public static float getFloat(String key, float defaultValue, String preferencesKey) {
        return getPreferences(preferencesKey).getFloat(key, defaultValue);
    }


    public static boolean contains(String key) {
        return contains(key, KEY_APPLICATION);
    }

    public static boolean contains(String key, String preferencesKey) {
        return getPreferences(preferencesKey).contains(key);
    }


    public static void clear(String preferencesKey) {
        getPreferences(preferencesKey).edit().clear().apply();
    }

    public static void remove(String key) {
        remove(key, KEY_APPLICATION);
    }

    public static void remove(String key, String preferencesKey) {
        getPreferences(preferencesKey).edit().remove(key).apply();
    }


    public static SharedPreferences getPreferences(String key) {
        if (sPreferencesMap.containsKey(key)) {
            return sPreferencesMap.get(key);
        } else {
            SharedPreferences preferences = sContext.getSharedPreferences(key, Context.MODE_PRIVATE);
            sPreferencesMap.put(key, preferences);
            return preferences;
        }
    }
}
