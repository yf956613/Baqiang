package com.jiebao.baqiang.data.bean;

/**
 * 服务器更新网络请求
 *
 * {"serverVersion":"0.0.1","serverTime":"1521509493867","baQiangApkVersion":"1.0.2_2.apk"}
 */

public class AppUpdateBean {
    private String serverVersion;

    // apk名字，服务器存储apk版本
    private String baQiangApkVersion;

    private String serverTime;

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public String getBaQiangApkVersion() {
        return baQiangApkVersion;
    }

    @Override
    public String toString() {
        return "AppUpdateBean{" + "serverVersion='" + serverVersion + '\'' + ", " +
                "baQiangApkVersion='" + baQiangApkVersion + '\'' + ", serverTime='" + serverTime
                + '\'' + '}';
    }
}
