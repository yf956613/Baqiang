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

    private String serverTime;
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
        mServerInfoUrl = SharedUtil.getJiebaoServletAddresFromSP
                (BaqiangApplication.getContext(), NetworkConstant
                        .SYNC_SERVER_TIME);

        RequestParams params = new RequestParams(mServerInfoUrl);
        params.addQueryStringParameter("saleId", salesId);
        params.addQueryStringParameter("userName", userName);
        params.addQueryStringParameter("password", psw);
        params.setConnectTimeout(45 * 1000);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String serverInfo) {
                Gson gson = new Gson();
                AppUpdateBean appInfo = gson.fromJson(serverInfo,
                        AppUpdateBean.class);
                LogUtil.trace("appInfo:" + appInfo.toString());

                serverTime = appInfo.getServerTime();
                mServerInfoStatus.updateServerInfo("", serverTime, "");
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
