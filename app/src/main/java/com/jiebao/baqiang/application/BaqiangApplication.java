package com.jiebao.baqiang.application;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.listener.UIHandler;
import com.jiebao.baqiang.util.AppUtil;
import com.jiebao.baqiang.util.CrashHandler;
import com.jiebao.baqiang.util.FileUtil;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class BaqiangApplication extends Application {
    private static final String TAG = BaqiangApplication.class.getSimpleName();

    public static Context mContext;
    public static Properties prop;

    private static String baseRootDir = null;
    //默认数据库文件的目录.
    private static String dbDir = null;
    //导入文件目录
    private static String importFileDir = null;
    private static String exportFileDir = null;
    private static String downloadFileDir = null;

    private static String adminDir = null;
    public static String versionChars;
    public static Activity mTopActivity;
    public static boolean isSoftDecodeScan = false;

    private static Handler mMainThreadHandler;
    public static UIHandler handler = new UIHandler(Looper.getMainLooper());

    /**
     * 维护Activity 的list
     */
    private static List<Activity> mActivitys = Collections.synchronizedList(new
            LinkedList<Activity>());


    public static Handler getmMainThreadHandler() {
        return mMainThreadHandler;
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.trace("");

        initData();
    }

    public void initData() {
        mContext = getApplicationContext();

        initPath();
        loadProperty(mContext);

        mMainThreadHandler = new Handler(getMainLooper());

        isSoftDecodeScan = FileUtil.exist("/dev/moto_sdl");
        //LogcatHelper.getInstance(this).start();
        versionChars = AppUtil.getAppVersionName(mContext);

        x.Ext.init(this);
        x.Ext.setDebug(false);

        initAutoUploadRecords();
        registerActivityListener();

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    private void initAutoUploadRecords() {
        int timeInterval = SharedUtil.getInt(this, Constant.PREFERENCE_NAME_AUTO_UPLOAD_TIME);
        if (timeInterval == 0) {
            timeInterval = 10;
            SharedUtil.putInt(this, Constant.PREFERENCE_NAME_AUTO_UPLOAD_TIME, timeInterval);
        }

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);

        int tenMinutes = timeInterval * 60 * 1000;
        long triggerAtMillis = System.currentTimeMillis() + tenMinutes;
        long intervalMillis = timeInterval * 60 * 1000;
        int requestCode = 0;

        Intent intent = new Intent();
        intent.setAction(Constant.AUTO_ACTION_UPLOAD_RECORDS);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        /**
         * int type：闹钟类型 AlarmManager.RTC_WAKEUP 使用绝对时间
         * long triggerAtMillis：闹钟首次执行时间，如果设置为（绝对时间）System.currentTimeMillis()会默认在5秒后执行首次
         *                          如果设置成System.currentTimeMillis() + 20 * 1000，首次执行在20秒后
         * long intervalMillis：两次执行时间间隔
         * PendingIntent operation：闹钟响应动作
         */
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis,
                mPendingIntent);
    }

    public static void initPath() {
        String baseRootPath = File.separator + Constant.APP_NAME + File.separator;
        String dbPath = baseRootPath + Constant.DB_DIR;

        String importDir = baseRootPath + Constant.IMPORT_DIR;
        String exportDir = baseRootPath + Constant.EXPORT_DIR;
        String downloadPath = baseRootPath + Constant.DOWNLOAD_DIR;
        String adminPath = baseRootPath + Constant.ADMIN;

        try {
            if (!FileUtil.isCanUseSD()) {
                return;
            } else {
                File root = FileUtil.getRootDirectory();
                File baseDir = new File(root.getAbsolutePath() + baseRootPath);
                if (!baseDir.exists()) {
                    baseDir.mkdirs();
                }
                baseRootDir = baseDir.getPath();
                File dbDirFile = new File(root.getAbsolutePath() + dbPath);
                if (!dbDirFile.exists()) {
                    dbDirFile.mkdirs();
                }
                dbDir = dbDirFile.getPath() + File.separator;

                File dir = new File(root.getAbsolutePath() + importDir);

                if (!dir.exists()) {
                    dir.mkdirs();
                }
                importFileDir = dir.getPath() + File.separator;

                File exDir = new File(root.getAbsolutePath() + exportDir);

                if (!exDir.exists()) {
                    exDir.mkdirs();
                }
                exportFileDir = exDir.getPath() + File.separator;

                File downloadDir = new File(root.getAbsolutePath() + downloadPath);
                if (!dbDirFile.exists()) {
                    dbDirFile.mkdirs();
                }
                downloadFileDir = downloadDir.getPath();// + File.separator;

                File administratorDir = new File(root.getAbsolutePath() + adminPath);
                if (!administratorDir.exists()) {
                    administratorDir.mkdirs();
                }
                adminDir = administratorDir.getPath() + File.separator;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDbDir(Context context) {
        if (baseRootDir == null) {
            initPath();
        }
        return dbDir;
    }

    public static String getImportFileDir() {
        if (baseRootDir == null) {
            initPath();
        }
        return importFileDir;
    }

    public static String getExportFileDir() {
        if (baseRootDir == null) {
            initPath();
        }
        return exportFileDir;
    }

    public static String getDownloadFileDir() {
        if (downloadFileDir == null) {
            initPath();
        }
        return downloadFileDir;
    }

    public static String getAdminDir() {
        if (adminDir == null) {
            initPath();
        }
        return adminDir;
    }

    public static void loadProperty(Context c) {
        prop = new Properties();
        try {
            File config = new File(getAdminDir() + Constant.CONFIG);
            if (!config.exists()) {
                config.createNewFile();
                String initConfig = Constant.getInitConfig();
                FileOutputStream outputStream = new FileOutputStream(config);
                outputStream.write(initConfig.getBytes());
                outputStream.close();
            }
            prop.load(new FileInputStream(getAdminDir() + Constant.CONFIG));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getOperateType() {
        String value = prop.getProperty(Constant.ARG_OPERATE_TYPE, "1");
//        if(StringUtil.isNumeric(value)){
//            return Integer.valueOf(value);
//        }
        return 1;
    }

    public static boolean isDebug() {
        return true;
    }

    public static String getFormatStr(int strId, Object... args) {
        String formatStr = mContext.getString(strId);
        return String.format(formatStr, args);
    }

    public static String getStr(int strId) {
        return mContext.getString(strId);
    }

    private void registerActivityListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    pushActivity(activity);
                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (null == mActivitys && mActivitys.isEmpty()) {
                        return;
                    } else {
                        if (mActivitys.contains(activity)) {
                            popActivity(activity);
                        } else {
                            // do nothing
                        }
                    }
                }
            });
        }
    }

    /**
     * @param activity 作用说明 ：添加一个activity到管理里
     */
    public void pushActivity(Activity activity) {
        mActivitys.add(activity);
        LogUtil.trace("activityList:size:" + mActivitys.size());
    }

    /**
     * @param activity 作用说明 ：删除一个activity在管理里
     */
    public void popActivity(Activity activity) {
        mActivitys.remove(activity);
        LogUtil.trace("activityList:size:" + mActivitys.size());
    }

    /**
     * @return 作用说明 ：获取当前最顶部activity的实例
     */
    public static Activity getTopActivity() {
        Activity mBaseActivity = null;
        synchronized (mActivitys) {
            final int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = mActivitys.get(size);
        }
        return mBaseActivity;
    }

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public static Activity getLatestActivity() {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return null;
        }

        Activity activity = mActivitys.get(mActivitys.size() - 1);
        return activity;
    }

    /**
     * @return 作用说明 ：获取当前最顶部的acitivity 名字
     */
    public static String getTopActivityName() {
        Activity mBaseActivity = null;
        synchronized (mActivitys) {
            final int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = mActivitys.get(size);
        }
        return mBaseActivity.getClass().getName();
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public static void finishCurrentActivity() {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }

        Activity activity = mActivitys.get(mActivitys.size() - 1);
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }

        if (activity != null) {
            mActivitys.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }
        for (Activity activity : mActivitys) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 按照指定类名找到activity
     *
     * @param cls
     * @return
     */
    public static Activity findActivity(Class<?> cls) {
        Activity targetActivity = null;
        if (mActivitys != null) {
            for (Activity activity : mActivitys) {
                if (activity.getClass().equals(cls)) {
                    targetActivity = activity;
                    break;
                }
            }
        }
        return targetActivity;
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        if (mActivitys == null) {
            return;
        }
        for (Activity activity : mActivitys) {
            activity.finish();
        }
        mActivitys.clear();
    }

    /**
     * 退出应用程序
     */
    public static void appExit() {
        try {
            LogUtil.trace("app exit");
            finishAllActivity();
        } catch (Exception e) {
        }
    }

    public static int getActivitStackSize() {
        if (mActivitys != null) {
            return mActivitys.size();
        }

        return 0;
    }

}
