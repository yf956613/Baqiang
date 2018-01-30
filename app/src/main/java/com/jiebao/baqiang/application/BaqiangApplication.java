package com.jiebao.baqiang.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
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
    public static String deviceId = "";
    public static double longitude;
    public static double latitude;
    public static String versionChars;
    public static Activity mTopActivity;//看见的Activity
    public static boolean isSoftDecodeScan = false;

    public static UIHandler handler = new UIHandler(Looper.getMainLooper());

    public static Context getContext() {
        return mContext;
    }

    public static String getDeviceId() {
//        if(StringUtil.isEmpty(deviceId)) {
//            SharedPreferences sharedPreferences = BaqiangApplication.getContext().getSharedPreferences(Constant.PREFERENCE_KEY_SYSTEM_ARG, Context.MODE_PRIVATE);
//            String deviceId = sharedPreferences.getString(Constant.PREFERENCE_KEY_DEVICE_ID, "");
//            if(!StringUtil.isEmpty(deviceId)) {
//                return deviceId;
//            }
//        }
        return deviceId.replace("\n", "").trim();
    }

    public static String getFormatStr(int strId, Object... args) {
        String formatStr = mContext.getString(strId);
        return String.format(formatStr, args);
    }

    public static String getStr(int strId) {
        return mContext.getString(strId);
    }

    /**
     * 主线程Handler
     */
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

        // Xutils框架初始化
        x.Ext.init(this);
        // 输出debug日志，开启会影响性能
        x.Ext.setDebug(false);
    }

    public static void initPath() {
        String baseRootPath = File.separator + Constant.APP_NAME + File.separator;
        // 默认DB目录.
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
                File baseDir = new File(root.getAbsolutePath()
                        + baseRootPath);
                if (!baseDir.exists()) {
                    baseDir.mkdirs();
                }
                baseRootDir = baseDir.getPath();
                File dbDirFile = new File(root.getAbsolutePath() + dbPath);
                if (!dbDirFile.exists()) {
                    dbDirFile.mkdirs();
                }
                dbDir = dbDirFile.getPath() + File.separator;

                File dir = new File(root.getAbsolutePath()
                        + importDir);

                if (!dir.exists()) {
                    dir.mkdirs();
                }
                importFileDir = dir.getPath() + File.separator;

                File exDir = new File(root.getAbsolutePath()
                        + exportDir);

                if (!exDir.exists()) {
                    exDir.mkdirs();
                }
                exportFileDir = exDir.getPath() + File.separator;

                File downloadDir = new File(root.getAbsolutePath()
                        + downloadPath);
                if (!dbDirFile.exists()) {
                    dbDirFile.mkdirs();
                }
                downloadFileDir = downloadDir.getPath();// + File.separator;

                File administratorDir = new File(root.getAbsolutePath()
                        + adminPath);
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
            if(!config.exists()){
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

    public static boolean getUseCamera(){
        String value =  prop.getProperty(Constant.ARG_USE_CAMERA,"0");
//        if(StringUtil.isNumeric(value)){
//            return Integer.valueOf(value) == 1;
//        }
        return false;
    }

    public static int getOperateType(){
        String value =  prop.getProperty(Constant.ARG_OPERATE_TYPE,"1");
//        if(StringUtil.isNumeric(value)){
//            return Integer.valueOf(value);
//        }
        return 1;
    }

    public static int getBillRows(){
        String value =  prop.getProperty(Constant.ARG_BILL_ROWS,"50");
//        if(StringUtil.isNumeric(value)){
//            return Integer.valueOf(value);
//        }
        return 50;
    }

    public static boolean isDebug(){
        return true;
    }
}
