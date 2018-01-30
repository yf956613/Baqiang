package com.jiebao.baqiang.util;

import android.os.Environment;

import java.io.File;

/**
 * SD卡配置
 * Created by Administrator on 2016/1/28.
 */
public class TbdSdcardCofig {
    public static final String ROOT = "jiebaodz";
    public static final String DOWNLOAD = "download";
    public static final String IMAGE = "images";
    public static final String TEMP = "temps";
    public static final String PRINT_PRIVIEW = "printpriviews";

    /**
     * sd卡根路径
     */
    private static final String TBD_SDCARD_PATH =
            Environment.getExternalStorageDirectory() + File.separator;

    /**
     * sd卡快捷宝根路径
     */
    private static final String TBD_SDCARD_ROOT_PATH =
            TBD_SDCARD_PATH + ROOT + File.separator;
    /**
     * sd卡文件下载路径
     */
    private static final String TBD_SDCARD_DOWNLOAD_PATH =
            TBD_SDCARD_ROOT_PATH + DOWNLOAD + File.separator;


    /**
     * sd卡图片文件路径
     */
    private static final String TBD_SDCARD_IMAGE_PATH =
            TBD_SDCARD_ROOT_PATH + IMAGE + File.separator;

    /**
     * sd卡临时文件路径
     */
    private static final String TBD_SDCARD_TEMP_PATH =
            TBD_SDCARD_ROOT_PATH + TEMP + File.separator;
    /**
     * sd卡打印预览文件路径
     */
    private static final String TBD_SDCARD_PRINT_PRIVIEW_PATH =
            TBD_SDCARD_ROOT_PATH + PRINT_PRIVIEW + File.separator;

    /**
     * 创建根文件夹
     */
    private static void createRootDir() {
        File parent = new File(TBD_SDCARD_ROOT_PATH);
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }

    /**
     * 创建下载文件夹
     */
    private static void createDownLoadDir() {
        createRootDir();
        File parent = new File(TBD_SDCARD_DOWNLOAD_PATH);
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }

    /**
     * 创建图片文件夹
     */
    private static void createImagesDir() {
        createRootDir();
        File parent = new File(TBD_SDCARD_IMAGE_PATH);
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }

    /**
     * 创建临时文件夹
     */
    private static void createTempDir() {
        createRootDir();
        File parent = new File(TBD_SDCARD_TEMP_PATH);
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }
    /**
     * 创建打印预览文件夹
     */
    private static void createPrintPriviewDir() {
        createRootDir();
        File parent = new File(TBD_SDCARD_PRINT_PRIVIEW_PATH);
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }

    public static String getTbdSdcardRootPath() {
        createRootDir();
        return TBD_SDCARD_ROOT_PATH;
    }

    public static String getTbdSdcardDownloadPath() {
        createDownLoadDir();
        return TBD_SDCARD_DOWNLOAD_PATH;
    }

    public static String getTbdSdcardImagePath() {
        createImagesDir();
        return TBD_SDCARD_IMAGE_PATH;
    }

    public static String getTbdSdcardPath() {
        return TBD_SDCARD_PATH;
    }

    public static String getTbdSdcardTempPath() {
        createTempDir();
        return TBD_SDCARD_TEMP_PATH;
    }

    public static String getTbdSdcardPrintPriviewPath() {
        createPrintPriviewDir();
        return TBD_SDCARD_PRINT_PRIVIEW_PATH;
    }
}
