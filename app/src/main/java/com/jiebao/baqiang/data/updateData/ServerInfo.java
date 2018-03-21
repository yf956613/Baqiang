package com.jiebao.baqiang.data.updateData;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jiebao.baqiang.activity.AdministratorSettingActivity;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.AppUpdateBean;
import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.data.bean.SalesServiceList;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.global.IDownloadStatus;
import com.jiebao.baqiang.global.IServerInfoStatus;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.service.DownLoadApkFileService;
import com.jiebao.baqiang.util.FileUtil;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018/3/20.
 */

public class ServerInfo extends UpdateInterface  {

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

        mServerInfoUrl = SharedUtil.getJiebaoServletAddresFromSP(BaqiangApplication
                .getContext(), NetworkConstant.APP_UPDATE_INFO);

        Log.e("linjiazhi", "mServerInfoUrl " + mServerInfoUrl);

        RequestParams params = new RequestParams(mServerInfoUrl);
        params.addQueryStringParameter("saleId", salesId);
        params.addQueryStringParameter("userName", userName);
        params.addQueryStringParameter("password", psw);
        params.setConnectTimeout(30 * 1000);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String serverInfo) {

                Gson gson = new Gson();
                AppUpdateBean appInfo = gson.fromJson(serverInfo, AppUpdateBean.class);
                LogUtil.trace("appInfo:" + appInfo.toString());
                Log.e("linjiazhi", "appInfo:" + appInfo.toString());
                if ("unknown".equals(appInfo.getBaQiangApkVersion())) {
                }

                serverVersion = appInfo.getServerVersion();
                serverTime = appInfo.getServerTime();
                serverApkVersin = appInfo.getBaQiangApkVersion();

                mServerInfoStatus.updateServerInfo(serverVersion, serverTime, serverApkVersin);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.trace(throwable.getMessage());

                Log.e("linjiazhi", throwable.getMessage());

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
