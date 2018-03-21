package com.jiebao.baqiang.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.global.NetworkConstant;

public class SharedUtil {

    /**
     * 默认 SharePreferences文件名.
     */
    public static String SHARED_PATH = "app_share";

    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PATH, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, 0);
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, null);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * 从SharedPreferences中获取到存储的IP地址和端口号
     *
     * @param appContext
     * @param servlet
     * @return
     */
    public static String getServletAddresFromSP(Context appContext, String servlet) {
        String dataServerAddress = getString(appContext, Constant
                .PREFERENCE_KEY_DATA_SERVER_ADDRESS);
        String dataServerPort = getString(appContext, Constant.PREFERENCE_KEY_DATA_SERVER_PORT);

        if (TextUtils.isEmpty(dataServerAddress) || TextUtils.isEmpty(dataServerPort)) {
            /*Toast.makeText(appContext, "数据服务器地址或端口出错", Toast.LENGTH_SHORT)
                    .show();*/
        } else {
            return NetworkConstant.HTTP_DOMAIN + dataServerAddress + ":" + dataServerPort + servlet;
        }

        return null;
    }

    /**
     * 从SharedPreferences中获取到存储的IP地址和端口号
     *
     * @param appContext
     * @param servlet
     * @return
     */
    public static String getJiebaoServletAddresFromSP(Context appContext, String servlet) {
        String dataServerAddress = getString(appContext, Constant.PREFERENCE_KEY_JB_SERVER);
        String dataServerPort = getString(appContext, Constant.PREFERENCE_KEY_JB_SERVER_PORT);

        if (TextUtils.isEmpty(dataServerAddress) || TextUtils.isEmpty(dataServerPort)) {
            /*Toast.makeText(appContext, "数据服务器地址或端口出错", Toast.LENGTH_SHORT)
                    .show();*/
        } else {
            return NetworkConstant.HTTP_DOMAIN + dataServerAddress + ":" + dataServerPort + servlet;
        }

        return null;
    }

    public static int getSPUnloadZCFJRecords(Context appContext, String servlet) {
        return getInt(appContext, Constant.PREFERENCE_NAME_ZCFJ);
    }
}
