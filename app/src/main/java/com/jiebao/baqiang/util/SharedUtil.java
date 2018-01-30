package com.jiebao.baqiang.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.jiebao.baqiang.global.NetworkConstant;

public class SharedUtil {

    /** 默认 SharePreferences文件名. */
    public static String SHARED_PATH = "app_share";

    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        if(sharedPreferences==null){
            sharedPreferences = context.getSharedPreferences(SHARED_PATH, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key,null);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key,false);
    }
    /**
     * 从SharedPreferences中获取到存储的IP地址和端口号
     *
     * @param appContext
     * @param servlet
     * @return
     */
    public static String getServletAddresFromSP(Context appContext,
                                                String servlet) {
        SharedPreferences sp = appContext
                .getSharedPreferences
                        ("ServerInfo", Context.MODE_PRIVATE);
        if (sp != null) {
            String ip = sp.getString("Ip", "");
            String port = sp.getString("Port", "");
            return NetworkConstant.HTTP_DOMAIN + ip + ":" + port +
                    servlet;
        }

        return null;
    }
}
