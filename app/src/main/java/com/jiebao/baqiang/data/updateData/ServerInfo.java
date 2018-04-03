package com.jiebao.baqiang.data.updateData;

import com.google.gson.Gson;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.AppUpdateBean;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.global.IServerInfoStatus;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by Administrator on 2018/3/20.
 */

public class ServerInfo extends UpdateInterface {

    private static final String TAG = "ServerInfo";
    private static String mServerInfoUrl = "";
    private static String mApkFileDownloadUrl = "";
    private volatile static ServerInfo mInstance;

    private IServerInfoStatus mServerInfoStatus;

    private String serverVersion;
    private String serverTime;
    private String serverApkVersin;


    public String getServerVersion() {
        return serverVersion;
    }

    public String getServerTime() {
        return serverTime;
    }

    public String getServerApkVersin() {
        return serverApkVersin;
    }


    private ServerInfo() {
        infoId = Constant.SERVER_INFO_ID;
    }

    public static ServerInfo getInstance() {
        if (mInstance == null) {
            synchronized (ServerInfo.class) {
                if (mInstance == null) {
                    mInstance = new ServerInfo();
                }
            }
        }
        return mInstance;
    }

    public void setDataDownloadStatus(IServerInfoStatus serverInfoStatus) {
        this.mServerInfoStatus = serverInfoStatus;
    }

    public void getServerInfo() {
        mServerInfoUrl = SharedUtil.getJiebaoServletAddresFromSP
                (BaqiangApplication
                        .getContext(), NetworkConstant.APP_UPDATE_INFO);

        RequestParams params = new RequestParams(mServerInfoUrl);
        params.addQueryStringParameter("saleId", UpdateInterface.getSalesId());
        params.addQueryStringParameter("userName", UpdateInterface
                .getUserName());
        params.addQueryStringParameter("password", UpdateInterface.getPsw());
        params.setConnectTimeout(6 * 1000);

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
