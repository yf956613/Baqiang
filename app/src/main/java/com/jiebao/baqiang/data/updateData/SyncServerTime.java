package com.jiebao.baqiang.data.updateData;

import com.google.gson.Gson;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.AppUpdateBean;
import com.jiebao.baqiang.global.IServerInfoStatus;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 请求服务器时间
 */
public class SyncServerTime extends UpdateInterface {
    private static final String TAG = SyncServerTime.class.getSimpleName();

    private String serverVersion;
    private String serverTime;
    private String serverApkVersin;

    private volatile static SyncServerTime mInstance;
    private static String mServerInfoUrl = "";
    private IServerInfoStatus mServerInfoStatus;

    public static SyncServerTime getInstance() {
        if (mInstance == null) {
            synchronized (ServerInfo.class) {
                if (mInstance == null) {
                    mInstance = new SyncServerTime();
                }
            }
        }
        return mInstance;
    }

    public void setDataDownloadStatus(IServerInfoStatus serverInfoStatus) {
        this.mServerInfoStatus = serverInfoStatus;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void getRequestServerTime() {
        mServerInfoUrl = SharedUtil.getServletAddresFromSP
                (BaqiangApplication.getContext(), NetworkConstant
                        .SYNC_SERVER_TIME);

        RequestParams params = new RequestParams(mServerInfoUrl);
        params.addQueryStringParameter("saleId", salesId);
        params.addQueryStringParameter("userName", userName);
        params.addQueryStringParameter("password", psw);
        params.setConnectTimeout(15 * 1000);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String serverInfo) {
                Gson gson = new Gson();
                AppUpdateBean appInfo = gson.fromJson(serverInfo,
                        AppUpdateBean.class);
                if (appInfo != null) {
                    LogUtil.trace("appInfo:" + appInfo.toString());

                    serverVersion = appInfo.getServerVersion();
                    serverTime = appInfo.getServerTime();
                    serverApkVersin = appInfo.getBaQiangApkVersion();
                    mServerInfoStatus.updateServerInfo(serverVersion,
                            serverTime, serverApkVersin);
                } else {
                    // 解析出错
                    mServerInfoStatus.showServerInfoError("数据解析出错");
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.trace(throwable.getMessage());
                mServerInfoStatus.showServerInfoError(throwable.getMessage());
            }

            @Override
            public void onCancelled(CancelledException e) {
                LogUtil.trace();
            }

            @Override
            public void onFinished() {
                LogUtil.trace();
            }
        });

    }
}
