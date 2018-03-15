package com.jiebao.baqiang.data.bean;

/**
 * 服务器更新网络请求
 */

public class AppUpdateBean {
    private String serverVersion;

    // apk名字，服务器存储apk版本
    private String baQiangApkVersion;

    public String getServerVersion() {
        return serverVersion;
    }

    public String getBaQiangApkVersion() {
        return baQiangApkVersion;
    }

    @Override
    public String toString() {
        return "AppUpdateBean{" +
                "serverVersion='" + serverVersion + '\'' +
                ", baQiangApkVersion='" + baQiangApkVersion + '\'' +
                '}';
    }
}
