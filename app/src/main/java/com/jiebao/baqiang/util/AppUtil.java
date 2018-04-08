package com.jiebao.baqiang.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.util.DisplayMetrics;

import com.jiebao.baqiang.application.BaqiangApplication;

import java.lang.reflect.Method;
import java.util.UUID;

public class AppUtil {
    /**
     * 获取包信息.
     *
     * @param context the context
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            String packageName = context.getPackageName();
            info = context.getPackageManager().getPackageInfo(packageName, PackageManager
                    .GET_ACTIVITIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 获取屏幕尺寸与密度.
     *
     * @param context the context
     * @return mDisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources mResources;
        if (context == null) {
            mResources = Resources.getSystem();

        } else {
            mResources = context.getResources();
        }
        return mResources.getDisplayMetrics();
    }

    public static int getWindowWidthSize() {
        return BaqiangApplication.getContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getWindowHeightSize() {
        return BaqiangApplication.getContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static PowerManager.WakeLock mWakeLock;


    public static void setScreenBright(boolean isBright) {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) BaqiangApplication.getContext().getSystemService
                    (Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager
                    .ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "cinema");
        }
        if (isBright) {
            if (mWakeLock != null && mWakeLock.isHeld()) return;
            mWakeLock.acquire(); //设置保持唤醒
        } else {
            if (mWakeLock != null && mWakeLock.isHeld()) mWakeLock.release();
            mWakeLock = null;
        }
    }

    public static boolean IsNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) BaqiangApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {// 当前网络不可用
            return false;
        } else {
            return true;
        }
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0";
        }
    }

    //重新启动app
    public static void restartApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClassName("com.jiebao.stockbao", "com.jiebao.stockbao.activity.SplashActivity");
        BaqiangApplication.getContext().startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static int dip2px(float dpValue) {
        final float scale = BaqiangApplication.getContext().getResources().getDisplayMetrics()
                .density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String getSystemProperty(Context context, String key) throws
            IllegalArgumentException {
        String ret = "";
        try {
            ClassLoader cl = context.getClassLoader();
            Class SystemProperties = cl.loadClass("android.os" + "" + "" + ".SystemProperties");
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;
            Method get = SystemProperties.getMethod("get", paramTypes);
            Object[] params = new Object[1];
            params[0] = new String(key);
            ret = (String) get.invoke(SystemProperties, params);
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = "";
            //TODO } return ret;
        }

        return ret;
    }
}
