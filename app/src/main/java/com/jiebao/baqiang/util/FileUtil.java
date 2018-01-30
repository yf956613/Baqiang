package com.jiebao.baqiang.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.R;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.global.Constant;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {


    public static boolean isUsbConnect() {
        try {
            String storageState = Environment.getExternalStorageState();
            if(storageState.equals(Environment.MEDIA_SHARED) || storageState.equals(Environment.MEDIA_UNMOUNTED))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isCanUseSD() {
        try {
            return Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 描述：从sd卡中的文件读取到byte[].
     *
     * @param path sd卡中文件路径
     * @return byte[]
     */
    public static byte[] getByteArrayFromSD(String path) {
        byte[] bytes = null;
        ByteArrayOutputStream out = null;
        try {
            File file = new File(path);
            //SD卡是否存在
            if (!isCanUseSD()) {
                return null;
            }
            //文件是否存在
            if (!file.exists()) {
                return null;
            }

            long fileSize = file.length();
            if (fileSize > Integer.MAX_VALUE) {
                return null;
            }

            FileInputStream in = new FileInputStream(path);
            out = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int size = 0;
            while ((size = in.read(buffer)) != -1) {
                out.write(buffer, 0, size);
            }
            in.close();
            bytes = out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }
        return bytes;
    }

    /**
     * 描述：将byte数组写入文件.
     *
     * @param path    the path
     * @param content the content
     * @param create  the create
     */
    public static void writeByteArrayToSD(String path, byte[] content, boolean create) {
        FileOutputStream fos = null;
        try {
            File file = new File(path);
            //SD卡是否存在
            if (!isCanUseSD()) {
                return;
            }
            //文件是否存在
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();

                }
                if (!file.exists())
                    file.createNewFile();

            }
            fos = new FileOutputStream(path);
            fos.write(content);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static void copyDatabase(Context context, String dbName, String outDir) throws IOException {

        InputStream is = context.getResources().openRawResource(R.raw.dbstockbao);
        // 欲导入的数据库
        String outFileName = outDir + dbName;
        FileOutputStream fos = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = is.read(buffer)) > 0) {
            fos.write(buffer, 0, count);
        }
        fos.close();
        is.close();
    }

    public static boolean checkDatabase(String dbPath) {

        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(dbPath, null,
                    SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    public static String saveFileByBinary(byte[] data, String fileName) {                   /***加载附件***/
        String dirName = BaqiangApplication.getAdminDir()+ File.separator;
        File f = new File(dirName);
        if (!f.exists()) {      //判断文件夹是否存在
            f.mkdir();        //如果不存在、则创建一个新的文件夹
        }
        fileName = dirName + fileName;
        File file = new File(fileName);
        if (file.exists()) {    //如果目标文件已经存在
            file.delete();    //则删除旧文件
        }
        try {
            InputStream is = new ByteArrayInputStream(data);
            FileOutputStream os = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int len = 0;
            //开始读取
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
            }
            //完毕关闭所有连接
            is.close();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

//    public static void checkDBFile() throws IOException {
//        File dbFile = new File(StockBaoApplication.getDbDir(StockBaoApplication.getContext()) + DbHelper.DBNAME);
//        if (!dbFile.exists())
//            FileUtil.copyDatabase(StockBaoApplication.getContext(), DbHelper.DBNAME, StockBaoApplication.getDbDir(StockBaoApplication.getContext()));
//    }
//
//    public static String readDeviceIdInThread() {
//        SharedPreferences sharedPreferences = StockBaoApplication.getContext().getSharedPreferences(Constant.PREFERENCE_KEY_SYSTEM_ARG, Context.MODE_PRIVATE);
//        String deviceId = sharedPreferences.getString(Constant.PREFERENCE_KEY_DEVICE_ID, "");
//        if(!StringUtil.isEmpty(deviceId)) {
//            return deviceId;
//        }
//        try {
//            Process p = Runtime.getRuntime()
//                    .exec("getprop ro.serialno");
//            if (p != null) {
//                InputStream in = p.getInputStream();
//                int len = in.available();
//                int i = 0;
//                while (len <= 0 && i <= 10) {
//                    i++;
//                    Thread.sleep(100);
//                    len = in.available();
//
//                }
//                if (len != in.available()) {
//                    len = in.available();
//                }
//                byte[] buffer = new byte[len];
//                in.read(buffer);
//                p.destroy();
//                String sysytemDeviceId = new String(buffer);
//                //StockBaoApplication.deviceId = new String(buffer);
//                SharedPreferences.Editor edit = sharedPreferences.edit();
//                edit.putString(Constant.PREFERENCE_KEY_DEVICE_ID,sysytemDeviceId);
//                edit.commit();
//                return sysytemDeviceId;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    //获得指定文件的byte数组
    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static List<String> getFileContent(String filename) {
        List<String> lines = new ArrayList<>();
        //StringBuilder sb = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String s;
            //sb = new StringBuilder();
            while ((s = in.readLine()) != null)
                lines.add(s);
                //sb.append(s);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        //return sb.toString();
        return lines;

    }


    /**
     * 得到内置或外置SD卡的路径
     * @param isExSD   true=外置SD卡
     * @return
     */
    public static String getStoragePath(boolean isExSD) {
        StorageManager mStorageManager = (StorageManager) BaqiangApplication.getContext().getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (isExSD == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static String[] getExternalDirs(Context context) {
        Context mContext = context.getApplicationContext();
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            final String[] paths = new String[length];
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                paths[i] = (String) getPath.invoke(storageVolumeElement);
            }
            return paths;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static String getStoragePath(Context mContext, boolean is_removale) {
//
//        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
//        Class<?> storageVolumeClazz = null;
//        try {
//            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
//            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
//            Method getPath = storageVolumeClazz.getMethod("getPath");
//            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
//            Object result = getVolumeList.invoke(mStorageManager);
//            final int length = Array.getLength(result);
//            for (int i = 0; i < length; i++) {
//                Object storageVolumeElement = Array.get(result, i);
//                String path = (String) getPath.invoke(storageVolumeElement);
//                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
//                if (is_removale == removable) {
//                    return path;
//                }
//            }
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


    public static String rootPath = null;
    /**
     * 获取SD卡的根目录
     *
     * @return null：不存在SD卡
     */
    public static File getRootDirectory() {
        if(rootPath != null)
            return new File(rootPath);
        File root;
        String[] paths = getExternalDirs(BaqiangApplication.getContext());
//        String externalPath = getStoragePath(true);
//        if(!TextUtils.isEmpty(externalPath)){
//            root = new File(externalPath);
//        }else{
//            root = Environment.getExternalStorageDirectory();
//        }
        if(paths!= null && paths.length > 1&& !TextUtils.isEmpty(paths[1])) {
            //创建测试文件
            try {
                File testFile = new File(paths[1] + File.separator+"test.txt");
                boolean isSuccess = testFile.createNewFile();
                if(!isSuccess) {
                    root = Environment.getExternalStorageDirectory();
                }
                else {
                    testFile.delete();
                    root = new File(paths[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
                root = Environment.getExternalStorageDirectory();
            }
        }
        else {
            root = Environment.getExternalStorageDirectory();
        }
        return root;
    }

    public static boolean exist(String path){
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }else
            return true;

    }
}
