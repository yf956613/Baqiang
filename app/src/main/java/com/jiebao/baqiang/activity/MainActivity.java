package com.jiebao.baqiang.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.updateData.ServerInfo;
import com.jiebao.baqiang.data.updateData.SyncServerTime;
import com.jiebao.baqiang.data.updateData.UpdateInterface;
import com.jiebao.baqiang.data.updateData.UpdateLiuCangType;
import com.jiebao.baqiang.data.updateData.UpdateSalesServiceData;
import com.jiebao.baqiang.data.updateData.UpdateShipmentType;
import com.jiebao.baqiang.data.updateData.UpdateVehicleInfo;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.global.IDownloadStatus;
import com.jiebao.baqiang.global.IServerInfoStatus;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.service.DownLoadApkFileService;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * 一级菜单界面，主要用户更新信息
 */

public class MainActivity extends BaseActivityWithTitleAndNumber implements
        View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String downloadApkAction = "com.jiebao.baqinag" +
            ".download";

    @ViewInject(R.id.btn_data_collect)
    private Button mBtnDataCollect;
    @ViewInject(R.id.ll_data_collect)
    private LinearLayout mLlDataCollect;

    @ViewInject(R.id.btn_query)
    private Button mBtnQuery;
    @ViewInject(R.id.ll_query)
    private LinearLayout mLlQuery;

    @ViewInject(R.id.btn_upload)
    private Button mBtnUpload;
    @ViewInject(R.id.ll_upload)
    private LinearLayout mLlUpload;

    @ViewInject(R.id.btn_phone_msg)
    private Button mBtnPhoneMsg;
    @ViewInject(R.id.ll_phone_msg)
    private LinearLayout mLlPhoneMsg;

    @ViewInject(R.id.btn_shipment_query)
    private Button mBtnShipmentQuery;
    @ViewInject(R.id.ll_shipment_query)
    private LinearLayout mLlShipmentQuery;

    @ViewInject(R.id.btn_area_query)
    private Button mBtnAreaQuery;
    @ViewInject(R.id.ll_area_query)
    private LinearLayout mLlAreaQuery;

    @ViewInject(R.id.btn_settings)
    private Button mBtnSettings;
    @ViewInject(R.id.ll_settings)
    private LinearLayout mLlSettings;

    private ProgressDialog mDownloadProgressDialog;

    private final View.OnFocusChangeListener mLlFocusChangeListener = new View
            .OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.btn_data_collect: {
                    setLinearLayoutBackground(mLlDataCollect, hasFocus);
                    break;
                }

                case R.id.btn_query: {
                    setLinearLayoutBackground(mLlQuery, hasFocus);
                    break;
                }

                case R.id.btn_upload: {
                    setLinearLayoutBackground(mLlUpload, hasFocus);
                    break;
                }

                case R.id.btn_phone_msg: {
                    setLinearLayoutBackground(mLlPhoneMsg, hasFocus);
                    break;
                }

                case R.id.btn_shipment_query: {
                    setLinearLayoutBackground(mLlShipmentQuery, hasFocus);
                    break;
                }

                case R.id.btn_area_query: {
                    setLinearLayoutBackground(mLlAreaQuery, hasFocus);
                    break;
                }

                case R.id.btn_settings: {
                    setLinearLayoutBackground(mLlSettings, hasFocus);
                    break;
                }
            }
        }
    };

    private BaQiangAPKDownloadReceiver mBaQiangAPKDownloadReceiver;

    class BaQiangAPKDownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            LogUtil.trace("BaQiangAPKDownloadReceiver；onReceive");
            Log.e("ljz", "BaQiangAPKDownloadReceiver onReceive");

            if (intent.getAction().equals(downloadApkAction)) {
                boolean state = intent.getBooleanExtra("downloadstate", false);
                if (state) {
                    updateLoadingDialogMsg(getString(R.string
                            .download_baqiangapk_success));
                } else {
                    updateLoadingDialogMsg(getString(R.string
                            .download_baqiangapk_failed));
                }
                closeLoadinDialog();
            }
        }
    }

    public static int downloadSuccessCnt = 0;
    public static int downloadFailedCnt = 0;
    public static int updateSucessCnt = 0;
    public static int downloadCnt = 0;

    private final DownloadStatusHandler mHandler = new DownloadStatusHandler
            (this);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBaQiangAPKDownloadReceiver != null) {
            unregisterReceiver(mBaQiangAPKDownloadReceiver);
        }
    }

    @Override
    public void initView() {
        setContent(R.layout.activity_main);
        setHeaderLeftViewText("速尔手持终端软件");
        x.view().inject(MainActivity.this);
    }

    @Override
    public void initData() {
        LogUtil.trace();

        initListener();

        // 同步捷宝服务器数据
        syncDataAction();

        mBaQiangAPKDownloadReceiver = new BaQiangAPKDownloadReceiver();
        registerReceiver(mBaQiangAPKDownloadReceiver, new IntentFilter
                (downloadApkAction));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 让容器默认获得焦点，渲染背景，选择第一个项目
        mBtnDataCollect.setFocusable(true);
        mBtnDataCollect.setFocusableInTouchMode(true);
        mBtnDataCollect.requestFocus();
        mBtnDataCollect.requestFocusFromTouch();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_data_collect: {
                Intent intent = new Intent(MainActivity.this,
                        DataCollectActivity.class);
                MainActivity.this.startActivity(intent);

                break;
            }

            case R.id.btn_query: {
                final android.support.v7.app.AlertDialog dialog = new android
                        .support.v7.app.AlertDialog.Builder(this).create();
                dialog.setView(LayoutInflater.from(this).inflate(R.layout
                        .alert_dialog_query, null));
                dialog.show();
                Button btnPositive = dialog.findViewById(R.id.btn_statistics);
                Button btnNegative = dialog.findViewById(R.id.btn_query);

                btnPositive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(MainActivity.this,
                                BussinessStatisticsActivity.class);
                        MainActivity.this.startActivity(intent);

                        dialog.dismiss();
                    }
                });
                btnNegative.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(MainActivity.this,
                                BussinessQueryActivity.class);
                        MainActivity.this.startActivity(intent);

                        dialog.dismiss();
                    }
                });

                break;
            }

            case R.id.btn_settings: {
                Intent intent = new Intent(MainActivity.this,
                        DetailMainSettingsActivity.class);
                MainActivity.this.startActivity(intent);

                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                // 提示是否切换账号
                final AlertDialog.Builder dialogBuilder = new AlertDialog
                        .Builder(MainActivity.this);
                dialogBuilder.setTitle("提示");

                View view = MainActivity.this.getLayoutInflater()
                        .inflate(R.layout.alert_dialog_toast, null);
                dialogBuilder.setView(view);
                final AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                Button btnCancel = view.findViewById(R.id.btn_cancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button btnSure = view.findViewById(R.id.btn_sure);
                btnSure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        // 回退到LoginActivity
                        MainActivity.this.finish();
                    }
                });

                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void initListener() {
        mBtnDataCollect.setOnClickListener(this);
        mBtnDataCollect.setOnFocusChangeListener(mLlFocusChangeListener);

        mBtnQuery.setOnClickListener(this);
        mBtnQuery.setOnFocusChangeListener(mLlFocusChangeListener);

        mBtnUpload.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnPhoneMsg.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnShipmentQuery.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnAreaQuery.setOnFocusChangeListener(mLlFocusChangeListener);

        mBtnSettings.setOnClickListener(this);
        mBtnSettings.setOnFocusChangeListener(mLlFocusChangeListener);

        // 让容器默认获得焦点，渲染背景，选择第一个项目
        mBtnDataCollect.setFocusable(true);
        mBtnDataCollect.setFocusableInTouchMode(true);
        mBtnDataCollect.requestFocus();
        mBtnDataCollect.requestFocusFromTouch();
    }

    private void syncDataAction() {

        ServerTimeSyncTask serverTimeSyncTask = new ServerTimeSyncTask();
        // FIXME 参数用于选择性下载，比如：start_param_1下载指定内容
        if (!Constant.DEBUG) {
            Log.e("ljz", "serverTimeSyncTask");
            serverTimeSyncTask.execute("serverTimeSyncTask");
        }

        ServerInfoSyncTask serverInfoSyncTask = new ServerInfoSyncTask();
        // FIXME 参数用于选择性下载，比如：start_param_1下载指定内容
        if (!Constant.DEBUG) {
            Log.e("ljz", "ServerInfoSyncTask");
            serverInfoSyncTask.execute("ServerInfoSyncTask");
            showLoadinDialog();
            updateLoadingDialogMsg(getString(R.string.sync_serverinfo));
        }
    }

    private void showProgressDialog() {
        if (mDownloadProgressDialog == null) {
            mDownloadProgressDialog = new ProgressDialog(MainActivity.this);
            mDownloadProgressDialog.setProgressStyle(ProgressDialog
                    .STYLE_HORIZONTAL);
            mDownloadProgressDialog.setCanceledOnTouchOutside(false);
            mDownloadProgressDialog.setCancelable(false);
            mDownloadProgressDialog.setTitle("提示信息：");
            mDownloadProgressDialog.setMessage("正在下载资料列表...");
            // 设置最大更新下载数据量
            mDownloadProgressDialog.setMax(Constant.MAX_DOWNLOAD_COUNT);
        }

        if (!mDownloadProgressDialog.isShowing()) {
            mDownloadProgressDialog.show();
        }
    }

    private void setLinearLayoutBackground(View v, boolean hasFocus) {
        if (hasFocus) {
            v.setBackgroundResource(R.color.back_transpant);
        } else {
            v.setBackgroundResource(R.color.bg_transparent);
        }
    }

    private int resolveServerAppVersionCode(String versionInfo) {
        String[] array = versionInfo.split("_");
        String versionCode = array[1];
        return Integer.parseInt(versionCode.replace(".apk", ""));
    }

    private int getCurrentVersionCode() {
        try {
            PackageManager packageManager = getPackageManager();
            //getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo
                    (getPackageName(), 0);
            LogUtil.d(TAG, "当前apk版本号：" + packInfo.versionCode);
            return packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }


    /**
     * 请求服务器时间，更新设备当前时间
     */
    class ServerTimeSyncTask extends AsyncTask<String, Integer, Long>
            implements IServerInfoStatus {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(String... strings) {

            Log.e("ljz", "doInBackground" + Arrays.toString
                    (strings));

            SyncServerTime.getInstance().setDataDownloadStatus(this);
            // 执行服务器数据请求
            SyncServerTime.getInstance().getRequestServerTime();

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // MainThread
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            // MainThread
        }

        @Override
        public void updateServerInfo(String serverinfo, String time, String
                apkVersion) {

            LogUtil.trace("ServerTimeSyncTask " + serverinfo + " " + time + "" +
                    " " + apkVersion);
            Log.e("ljz", "ServerTimeSyncTask " + serverinfo + " " + time + " " +
                    "" + apkVersion);

            if (time != null && time.length() > 0) {
                Intent timeIntent = new Intent();
                timeIntent.setAction("com.time.UPDATETIME");
                timeIntent.putExtra("time", Long.parseLong(time));
                MainActivity.this.sendBroadcast(timeIntent);
                LogUtil.trace("start to update system time");
                updateLoadingDialogMsg(getString(R.string.sync_servertimer));
            }
        }

        @Override
        public void showServerInfoError(String errorMsg) {
            LogUtil.trace("showServerInfoError ServerTimeSyncTask ");
        }

    }

    /**
     * 捷宝服务器请求更新apk，异步任务
     */
    class ServerInfoSyncTask extends AsyncTask<String, Integer, Long>
            implements IServerInfoStatus {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(String... strings) {

            Log.e("ljz", "doInBackground" + Arrays.toString
                    (strings));

            ServerInfo.getInstance().setDataDownloadStatus(this);
            // 执行服务器数据请求
            ServerInfo.getInstance().getServerInfo();


            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // MainThread
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            // MainThread
        }

        @Override
        public void updateServerInfo(String serverinfo, String time, String
                apkVersion) {
            LogUtil.trace("get updateServerInfo done ");

            Log.e("ljz", "ServerInfoSyncTask " + serverinfo + " " + time + " " +
                    "" + apkVersion);

            Message serverMsg = Message.obtain();
            // 捷宝服务器数据同步成功
            serverMsg.what = Constant.DOWNLOAD_SERVERINFO_SUCCESS;
            mHandler.sendMessage(serverMsg);
        }


        @Override
        public void showServerInfoError(String errorMsg) {
            LogUtil.trace("showServerInfoError ");

            // 捷宝服务器数据同步失败
            Message serverMsg = Message.obtain();
            serverMsg.what = Constant.UPDATE_SEVERINFO_FAILED;
            mHandler.sendMessage(serverMsg);
        }

    }

    /**
     * 异步下载任务
     * <p>
     * Params: 输入参数，对应excute()方法中传递的参数。如果不需要传递参数，则直接设为void即可
     * Progress：后台任务执行的百分比
     * Result：返回值类型，和doInBackground（）方法的返回值类型保持一致
     */
    class DataSyncTask extends AsyncTask<String, Integer, Long> implements
            IDownloadStatus {

        private UpdateInterface updateInterface;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public void setUpdateInterface(UpdateInterface uInterface) {
            updateInterface = uInterface;
        }

        @Override
        protected Long doInBackground(String... strings) {
            // WorkerThread
            LogUtil.trace("doInBackground parameters:" + Arrays.toString
                    (strings));

            Log.e("ljz", "doInBackground" + Arrays.toString
                    (strings));

            if (updateInterface != null) {
                updateInterface.setDataDownloadStatus(this);
                updateInterface.updateData();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // MainThread
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            // MainThread
        }

        public void startDownload(int infoId) {
            LogUtil.trace("startDownload " + infoId);

            Log.e("ljz", "startDownload " + infoId);

            Message startMsg = Message.obtain();
            startMsg.what = Constant.STARTDOWNLOAD_INFO + infoId;
            startMsg.arg1 = infoId;
            mHandler.sendMessage(startMsg);
        }

        @Override
        public void downloadFinish(int infoId) {
            LogUtil.trace("downloadFinish " + infoId);

            Log.e("ljz", "downloadFinish " + infoId);

            downloadSuccessCnt++;
            downloadCnt++;

            Message dlInfoMsg = Message.obtain();
            dlInfoMsg.what = Constant.DOWNLOAD_INFO_SUCCESS + infoId;
            mHandler.sendMessage(dlInfoMsg);

            Message dlsuccessMsg = Message.obtain();
            dlsuccessMsg.what = Constant.DOWNLOAD_SUCCESS;
            mHandler.sendMessage(dlsuccessMsg);

        }

        @Override
        public void updateDataFinish(int infoId) {
            LogUtil.trace("updateDataFinish " + +infoId);

            Log.e("ljz", "updateDataFinish " + infoId);

            updateSucessCnt++;

            Message msg = Message.obtain();
            msg.what = Constant.UPDATE_DATA_DONE + infoId;
            msg.arg1 = infoId;
            mHandler.sendMessage(msg);

            Message updateMsg = Message.obtain();
            updateMsg.what = Constant.DOWNLOAD_UPDATE_DONE;
            updateMsg.arg1 = infoId;
            mHandler.sendMessage(updateMsg);

        }

        @Override
        public void downLoadError(int infoId, String errorMsg) {
            LogUtil.trace("downLoadError infoId " + infoId + " errorMsg: " +
                    errorMsg);

            Log.e("ljz", "downLoadError " + infoId);

            downloadFailedCnt++;
            downloadCnt++;

            Message errMsg = Message.obtain();
            errMsg.what = Constant.UPDATE_DATA_FAILED + infoId;
            errMsg.arg1 = infoId;
            mHandler.sendMessage(errMsg);

            Message dlerrMsg = Message.obtain();
            dlerrMsg.what = Constant.DOWNLOAD_FAILED;
            mHandler.sendMessage(errMsg);
        }
    }

    private class DownloadStatusHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public DownloadStatusHandler(MainActivity activity) {
            this.mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity == null) {
                super.handleMessage(msg);
                return;
            }

            switch (msg.what) {
                case Constant.DOWNLOAD_SERVERINFO_SUCCESS: {
                    //update system time
//                    String time = ServerInfo.getInstance().getServerTime();
//
//                    if (time != null) {
//                        Intent timeIntent = new Intent();
//                        timeIntent.setAction("com.time.UPDATETIME");
//                        timeIntent.putExtra("time", Long.parseLong(time));
//                        MainActivity.this.sendBroadcast(timeIntent);
//                        LogUtil.trace("start to update system time");
//                        updateLoadingDialogMsg(getString(R.string
//                                .sync_servertimer));
//                    }

                    if ((!("unknown".equals(ServerInfo.getInstance()
                            .getServerApkVersin()))) &&
                            getCurrentVersionCode() <
                                    resolveServerAppVersionCode(ServerInfo
                                            .getInstance().getServerApkVersin
                                                    ())) {
                        LogUtil.trace("start to download apk");
                        String mApkFileDownloadUrl = SharedUtil
                                .getJiebaoServletAddresFromSP
                                        (BaqiangApplication.getContext(),
                                                NetworkConstant
                                                        .APK_DOWNLOAD_URL);
                        if (mApkFileDownloadUrl != null) {
                            Intent service = new Intent(MainActivity.this,
                                    DownLoadApkFileService.class);
                            service.putExtra("downloadurl",
                                    mApkFileDownloadUrl);
                            Toast.makeText(MainActivity.this, "正在下载更新APK中",
                                    Toast.LENGTH_LONG).show();
                            startService(service);
                            updateLoadingDialogMsg(getString(R.string
                                    .download_baqiangapk));
                        } else {
                            // do nothing
                            closeLoadinDialog();

                            // MainThread 强制更新后台数据
                            showProgressDialog();

                            //set count 0 begin download data
                            downloadCnt = 0;
                            downloadSuccessCnt = 0;
                            downloadFailedCnt = 0;
                            updateSucessCnt = 0;
                            DataSyncTask shipmentDataSyncTask = new
                                    DataSyncTask();
                            shipmentDataSyncTask.setUpdateInterface
                                    (UpdateShipmentType.getInstance());
                            if (!Constant.DEBUG) {
                                shipmentDataSyncTask.execute
                                        ("shipmentDataSyncTask");
                            }

                        }

                    } else {
                        closeLoadinDialog();

                        // MainThread 强制更新后台数据
                        showProgressDialog();

                        //set count 0 begin download data
                        downloadCnt = 0;
                        downloadSuccessCnt = 0;
                        downloadFailedCnt = 0;
                        updateSucessCnt = 0;
                        DataSyncTask shipmentDataSyncTask = new DataSyncTask();
                        shipmentDataSyncTask.setUpdateInterface
                                (UpdateShipmentType.getInstance());
                        if (!Constant.DEBUG) {
                            shipmentDataSyncTask.execute
                                    ("shipmentDataSyncTask");
                        }
                    }

                    break;
                }
                case Constant.UPDATE_SEVERINFO_FAILED: {
                    closeLoadinDialog();
                    /*Toast.makeText(MainActivity.this, activity.getString(R
                            .string
                            .sync_serverinfo_failed), Toast.LENGTH_LONG).show
                            ();*/

                    // MainThread 强制更新后台数据
                    showProgressDialog();

                    //set count 0 begin download data
                    downloadCnt = 0;
                    downloadSuccessCnt = 0;
                    downloadFailedCnt = 0;
                    updateSucessCnt = 0;
                    DataSyncTask shipmentDataSyncTask = new DataSyncTask();
                    shipmentDataSyncTask.setUpdateInterface
                            (UpdateShipmentType.getInstance());
                    if (!Constant.DEBUG) {
                        shipmentDataSyncTask.execute
                                ("shipmentDataSyncTask");
                    }

                    break;
                }


                case Constant.DOWNLOAD_FAILED: {
                    Log.e("ljz", "DOWNLOAD_FAILED downloadCnt " + downloadCnt
                            + " downloadSuccessCnt " + downloadSuccessCnt
                            + " downloadFailedCnt " + downloadFailedCnt
                            + " updateSucessCnt " + updateSucessCnt);

                    activity.mDownloadProgressDialog.incrementProgressBy
                            (Constant.MAX_DOWNLOAD_STEP);
                    if ((downloadCnt == Constant.MAX_DOWNLOAD_COUNT)) {
                        activity.mDownloadProgressDialog.dismiss();
                    }
                    break;
                }
                case Constant.DOWNLOAD_SUCCESS: {
                    break;
                }

                case Constant.UPDATE_SUCCESS: {
                    break;
                }
                case Constant.DOWNLOAD_UPDATE_DONE: {

                    Log.e("ljz", "DOWNLOAD_UPDATE_DONE downloadCnt " +
                            downloadCnt
                            + " downloadSuccessCnt " + downloadSuccessCnt
                            + " downloadFailedCnt " + downloadFailedCnt
                            + " updateSucessCnt " + updateSucessCnt);

                    activity.mDownloadProgressDialog.incrementProgressBy
                            (Constant.MAX_DOWNLOAD_STEP);
                    if (downloadCnt == Constant.MAX_DOWNLOAD_COUNT) {
                        activity.mDownloadProgressDialog.dismiss();
                    }
                    break;
                }


                case Constant.STARTDOWNLOAD_SALESINFO: {
                    activity.mDownloadProgressDialog.setMessage(activity
                            .getString(R.string
                                    .download_salesinfo));
                    break;
                }
                case Constant.DOWNLOAD_SALESINFO_SUCCESS: {
                    activity.mDownloadProgressDialog.setMessage(activity
                            .getString(R.string
                                    .update_salesinfo));

                    break;
                }
                case Constant.UPDATE_SALESINFO_DONE: {
                    Log.e("ljz", "UPDATE_SALESINFO_DONE");
                    DataSyncTask vehiceDataSyncTask = new DataSyncTask();
                    vehiceDataSyncTask.setUpdateInterface(UpdateVehicleInfo
                            .getInstance());
                    if (!Constant.DEBUG) {
                        vehiceDataSyncTask.execute("vehiceDataSyncTask");
                    }
                    break;
                }
                case Constant.UPDATE_SALESINFO_FAILED: {
                    Log.e("ljz", "UPDATE_SALESINFO_FAILED");
                    DataSyncTask vehiceDataSyncTask = new DataSyncTask();
                    vehiceDataSyncTask.setUpdateInterface(UpdateVehicleInfo
                            .getInstance());
                    if (!Constant.DEBUG) {
                        vehiceDataSyncTask.execute("vehiceDataSyncTask");
                    }
                    break;
                }


                case Constant.STARTDOWNLOAD_SHIPMENTTYPEINFO: {
                    activity.mDownloadProgressDialog.setMessage(activity
                            .getString(R.string
                                    .download_shipmenttypeinfo));
                    break;
                }
                case Constant.DOWNLOAD_SHIPMENTTYPEINFO_SUCCESS: {
                    activity.mDownloadProgressDialog.setMessage(activity
                            .getString(R.string
                                    .update_shipmenttypeinfo));
                    break;
                }
                case Constant.UPDATE_SHIPMENTTYPEINFO_DONE: {
                    Log.e("ljz", "UPDATE_SHIPMENTTYPEINFO_DONE");
                    DataSyncTask salesDataSyncTask = new DataSyncTask();
                    salesDataSyncTask.setUpdateInterface
                            (UpdateSalesServiceData.getInstance());
                    if (!Constant.DEBUG) {
                        salesDataSyncTask.execute("salesDataSyncTask");
                    }
                    break;
                }
                case Constant.UPDATE_SHIPMENTTYPEINFO_FAILED: {
                    Log.e("ljz", "UPDATE_SHIPMENTTYPEINFO_FAILED");
                    DataSyncTask salesDataSyncTask = new DataSyncTask();
                    salesDataSyncTask.setUpdateInterface
                            (UpdateSalesServiceData.getInstance());
                    if (!Constant.DEBUG) {
                        salesDataSyncTask.execute("salesDataSyncTask");
                    }
                    break;
                }

                //put it download in the end
                case Constant.STARTDOWNLOAD_LIUCANGTYPEINFO: {
                    activity.mDownloadProgressDialog.setMessage(activity
                            .getString(R.string
                                    .download_liucangtypeinfo));
                    break;
                }
                case Constant.DOWNLOAD_LIUCANGTYPEINFO_SUCCESS: {
                    activity.mDownloadProgressDialog.setMessage(activity
                            .getString(R.string
                                    .update_liucangtypeinfo));
                    break;
                }
                case Constant.UPDATE_LIUCANGTYPEINFO_DONE: {
                    Log.e("ljz", "UPDATE_LIUCANGTYPEINFO_DONE");
                    break;
                }
                case Constant.UPDATE_LIUCANGTYPEINFO_FAILED: {
                    Log.e("ljz", "UPDATE_LIUCANGTYPEINFO_FAILED");
                    break;
                }

                case Constant.STARTDOWNLOAD_VEHICEINFO: {
                    activity.mDownloadProgressDialog.setMessage(activity
                            .getString(R.string
                                    .download_vehiceinfo));
                    break;
                }
                case Constant.DOWNLOAD_VEHICEINFO_SUCCESS: {
                    activity.mDownloadProgressDialog.setMessage(activity
                            .getString(R.string
                                    .update_vehiceinfo));
                    break;
                }
                case Constant.UPDATE_VEHICEINFO_DONE: {
                    Log.e("ljz", "UPDATE_VEHICEINFO_DONE");
                    DataSyncTask liucangDataSyncTask = new DataSyncTask();
                    liucangDataSyncTask.setUpdateInterface(UpdateLiuCangType
                            .getInstance());
                    if (!Constant.DEBUG) {
                        liucangDataSyncTask.execute("liucangDataSyncTask");
                    }
                    break;
                }
                case Constant.UPDATE_VEHICEINFO_FAILED: {
                    Log.e("ljz", "UPDATE_VEHICEINFO_FAILED");
                    DataSyncTask liucangDataSyncTask = new DataSyncTask();
                    liucangDataSyncTask.setUpdateInterface(UpdateLiuCangType
                            .getInstance());
                    if (!Constant.DEBUG) {
                        liucangDataSyncTask.execute("liucangDataSyncTask");
                    }
                    break;
                }

                case Constant.DO_ALL_FINISH: {
                    activity.mDownloadProgressDialog.dismiss();
                }

                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
}
