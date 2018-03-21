package com.jiebao.baqiang.application;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.listener.UIHandler;
import com.jiebao.baqiang.util.AppUtil;
import com.jiebao.baqiang.util.FileUtil;

import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class BaqiangApplication extends Application {

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

    public static UIHandler handler = new UIHandler(Looper.getMainLooper());

    public static Context getContext() {
        return mContext;
    }

    public static String getFormatStr(int strId, Object... args) {
        String formatStr = mContext.getString(strId);
        return String.format(formatStr, args);
    }

    public static String getStr(int strId) {
        return mContext.getString(strId);
    }

    private static Handler mMainThreadHandler;

    public static Handler getmMainThreadHandler() {
        return mMainThreadHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
    }

    private void initAutoUploadRecords() {
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);

        int tenMinutes = 1 * 60 * 1000;
        long triggerAtMillis = System.currentTimeMillis() + tenMinutes;
        long intervalMillis = 1 * 60 * 1000;
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
}
