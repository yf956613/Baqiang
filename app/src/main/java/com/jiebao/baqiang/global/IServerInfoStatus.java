package com.jiebao.baqiang.global;

/**
 * Created by Administrator on 2018/3/20.
 */

public interface IServerInfoStatus {
    void updateServerInfo(String serverinfo, String time, String apkVersion);
    void showServerInfoError(String errorMsg);
}
