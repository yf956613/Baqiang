package com.jiebao.baqiang.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.AppUpdateBean;
import com.jiebao.baqiang.data.updateData.ServerInfo;
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

public class MainActivity extends BaseActivityWithTitleAndNumber implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

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

    @Override
    public void initView() {
        setContent(R.layout.activity_main);
        setHeaderLeftViewText("速尔手持终端软件");
        x.view().inject(MainActivity.this);
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
    public void initData() {
        LogUtil.trace();

        initListener();

        syncDataAction();
    }

    private void syncDataAction() {
//        DataSyncTask dataSyncTask = new DataSyncTask();
//        // FIXME 参数用于选择性下载，比如：start_param_1下载指定内容
//        if(!Constant.DEBUG){
//            dataSyncTask.execute("start_param");
//        }

        ServerInfoSyncTask serverInfoSyncTask = new ServerInfoSyncTask();
        // FIXME 参数用于选择性下载，比如：start_param_1下载指定内容
        if (!Constant.DEBUG) {
            Log.e("linjiazhi", "ServerInfoSyncTask");
            serverInfoSyncTask.execute("start_param");
        }

    }

    private void initListener() {
        mBtnDataCollect.setOnClickListener(this);
        mBtnDataCollect.setOnFocusChangeListener(mLlFocusChangeListener);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_data_collect: {
                Intent intent = new Intent(MainActivity.this, DataCollectActivity.class);
                MainActivity.this.startActivity(intent);

                break;
            }

            case R.id.btn_settings: {
                Intent intent = new Intent(MainActivity.this, DetailMainSettingsActivity.class);
                MainActivity.this.startActivity(intent);

                break;
            }
        }
    }

    /**
     * 显示ProgressDialog下载进度条
     */
    private void showProgressDialog() {
        if (mDownloadProgressDialog == null) {
            mDownloadProgressDialog = new ProgressDialog(MainActivity.this);
            mDownloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDownloadProgressDialog.setCanceledOnTouchOutside(false);
            mDownloadProgressDialog.setCancelable(false);
            mDownloadProgressDialog.setTitle("提示信息：");
            mDownloadProgressDialog.setMessage("正在下载资料列表...");
            // 设置最大更新下载数据量
            mDownloadProgressDialog.setMax(Constant.MAX_DOWNLOAD_COUNT + 1);
        }
        mDownloadProgressDialog.show();
    }

    /**
     * 隐藏ProgressDialog下载进度条
     */
    private void closeProgressDialog() {
        if (mDownloadProgressDialog != null) {
            mDownloadProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                // 提示是否切换账号
                final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
                normalDialog.setTitle("提示");
                normalDialog.setCancelable(false);
                normalDialog.setMessage("是否退出当前账号？");
                normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 回退到LoginActivity
                        MainActivity.this.finish();
                    }
                });
                normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                normalDialog.show();

                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }


    class ServerInfoSyncTask extends AsyncTask<String, Integer, Long> implements IServerInfoStatus {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(String... strings) {
            ServerInfo.getInstance().setDataDownloadStatus(this);
            ServerInfo.getInstance().getServerInfo();
            Log.e("linjiazhi", "doInBackground");

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
        public void updateServerInfo(String serverinfo, String time, String apkVersion) {
            Log.e("linjiazhi", "serverinfo " + serverinfo);
            Log.e("linjiazhi", "time " + time);
            Log.e("linjiazhi", "apkVersion " + apkVersion);
            Message serverMsg = Message.obtain();
            serverMsg.what = Constant.DOWNLOAD_SERVERINFO_SUCCESS;
            mHandler.sendMessage(serverMsg);
        }


        @Override
        public void showServerInfoError(String errorMsg) {
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
    class DataSyncTask extends AsyncTask<String, Integer, Long> implements IDownloadStatus {

        private int updataCnt = 0;
        private int sucessCnt = 0;
        private int failedCnt = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            updataCnt = 0;
            sucessCnt = 0;
            failedCnt = 0;

            // MainThread 强制更新后台数据
            showProgressDialog();
        }

        @Override
        protected Long doInBackground(String... strings) {
            // WorkerThread
            LogUtil.trace("doInBackground parameters:" + Arrays.toString(strings));

            // 更新网点数据
            UpdateSalesServiceData.getInstance().setDataDownloadStatus(this);
            UpdateSalesServiceData.getInstance().updateSalesService();
            // 更新快件类型正常
            UpdateShipmentType.getInstance().setDataDownloadStatus(this);
            UpdateShipmentType.getInstance().updateShipmentType();
            // 更新留仓原因正常
            UpdateLiuCangType.getInstance().setDataDownloadStatus(this);
            UpdateLiuCangType.getInstance().updateLiuCangType();
            // 更新车辆信息正常
            UpdateVehicleInfo.getInstance().setDataDownloadStatus(this);
            UpdateVehicleInfo.getInstance().updateVehicleInfo();

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

            LogUtil.trace("updataCnt:" + updataCnt + "; failedCnt:" + failedCnt + "; sucessCnt:"
                    + sucessCnt);

            Message startMsg = Message.obtain();
            startMsg.what = Constant.STARTDOWNLOAD_INFO + infoId;
            startMsg.arg1 = infoId;
            mHandler.sendMessage(startMsg);
        }

        @Override
        public void downloadFinish(int infoId) {
            updataCnt++;
            LogUtil.trace("updataCnt:" + updataCnt + "; failedCnt:" + failedCnt + "; sucessCnt:"
                    + sucessCnt);
            LogUtil.trace("downloadFinish " + infoId);
            Message dlInfoMsg = Message.obtain();
            dlInfoMsg.what = Constant.DOWNLOAD_INFO_SUCCESS + infoId;
            mHandler.sendMessage(dlInfoMsg);

            Message dlSuccessMsg = Message.obtain();
            dlSuccessMsg.what = Constant.DOWNLOAD_SUCCESS;
            dlSuccessMsg.arg1 = infoId;
            mHandler.sendMessage(dlSuccessMsg);
        }

        @Override
        public void updateDataFinish(int infoId) {
            sucessCnt++;
            LogUtil.trace("updataCnt:" + updataCnt + "; failedCnt:" + failedCnt + "; sucessCnt:"
                    + sucessCnt);
            LogUtil.trace("updateDataFinish" + +infoId);
            Message updateMsg = Message.obtain();
            updateMsg.what = Constant.DOWNLOAD_UPDATE_DONE + infoId;
            updateMsg.arg1 = infoId;
            mHandler.sendMessage(updateMsg);

            if (updataCnt == Constant.MAX_DOWNLOAD_COUNT) {
                Message alldone = Message.obtain();
                alldone.what = Constant.DO_ALL_FINISH;
                mHandler.sendMessage(alldone);
            }

        }

        @Override
        public void downLoadError(int infoId, String errorMsg) {
            updataCnt++;
            failedCnt++;
            LogUtil.trace("updataCnt:" + updataCnt + "; failedCnt:" + failedCnt + "; sucessCnt:"
                    + sucessCnt);
            LogUtil.trace("infoId " + infoId + " errorMsg: " + errorMsg);
            Message errMsg = Message.obtain();
            errMsg.what = Constant.UPDATE_DATA_FAILED + infoId;
            errMsg.arg1 = infoId;
            mHandler.sendMessage(errMsg);

            Message dlFaildMsg = Message.obtain();
            dlFaildMsg.what = Constant.DOWNLOAD_FAILED;
            dlFaildMsg.arg1 = infoId;
            mHandler.sendMessage(dlFaildMsg);

        }
    }

    /**
     * 根据Button的状态，改变LinearLayout的背景
     *
     * @param v
     * @param hasFocus
     */
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
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            LogUtil.d(TAG, "当前apk版本号：" + packInfo.versionCode);
            return packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private final DownloadStatusHandler mHandler = new DownloadStatusHandler(this);

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
                    String time = ServerInfo.getInstance().getServerTime();

                    if (time != null) {
                        Log.e("linjiazhi", "update system time " + time);
                        Intent timeIntent = new Intent();
                        timeIntent.setAction("com.time.UPDATETIME");
                        timeIntent.putExtra("time", Long.parseLong(time));
                        MainActivity.this.sendBroadcast(timeIntent);
                    }

                    if ((!("unknown".equals(ServerInfo.getInstance().getServerApkVersin()))) &&
                            getCurrentVersionCode() < resolveServerAppVersionCode(ServerInfo
                                    .getInstance().getServerApkVersin())) {
                        LogUtil.trace("start to download");
                        String mApkFileDownloadUrl = SharedUtil.getJiebaoServletAddresFromSP
                                (BaqiangApplication.getContext(), NetworkConstant.APK_DOWNLOAD_URL);
                        if (mApkFileDownloadUrl != null) {
                            Intent service = new Intent(MainActivity.this, DownLoadApkFileService
                                    .class);
                            service.putExtra("downloadurl", mApkFileDownloadUrl);
                            Toast.makeText(MainActivity.this, "正在下载更新APK中", Toast.LENGTH_SHORT)
                                    .show();
                            startService(service);
                        }

                    } else {
                        Log.e("linjiazhi", "update data");
                        Toast.makeText(MainActivity.this, "APK已经是最新版本!", Toast.LENGTH_SHORT).show();
                        DataSyncTask dataSyncTask = new DataSyncTask();
                        // FIXME 参数用于选择性下载，比如：start_param_1下载指定内容
                        if (!Constant.DEBUG) {
                            dataSyncTask.execute("start_param");
                        }

                    }

                    break;
                }

                case Constant.UPDATE_SEVERINFO_FAILED: {
                    break;
                }

                case Constant.DOWNLOAD_FAILED: {
                    activity.mDownloadProgressDialog.incrementProgressBy(Constant
                            .MAX_DOWNLOAD_STEP);
                    break;
                }

                case Constant.DOWNLOAD_SUCCESS: {
                    activity.mDownloadProgressDialog.incrementProgressBy(Constant
                            .MAX_DOWNLOAD_STEP);
                    break;
                }

                case Constant.UPDATE_SUCCESS: {
                    break;
                }

                case Constant.DOWNLOAD_UPDATE_DONE: {
                    break;
                }

                case Constant.STARTDOWNLOAD_SALESINFO: {
                    break;
                }
                case Constant.DOWNLOAD_SALESINFO_SUCCESS: {
                    activity.mDownloadProgressDialog.setMessage(activity.getString(R.string
                            .download_salesinfo_success));
                    break;
                }
                case Constant.UPDATE_SALESINFO_DONE: {

                    break;
                }
                case Constant.UPDATE_SALESINFO_FAILED: {
                    break;
                }


                case Constant.STARTDOWNLOAD_SHIPMENTTYPEINFO: {
                    break;
                }
                case Constant.DOWNLOAD_SHIPMENTTYPEINFO_SUCCESS: {
                    activity.mDownloadProgressDialog.setMessage(activity.getString(R.string
                            .download_shipmenttypeinfo_success));
                    break;
                }
                case Constant.UPDATE_SHIPMENTTYPEINFO_DONE: {
                    break;
                }
                case Constant.UPDATE_SHIPMENTTYPEINFO_FAILED: {
                    break;
                }


                case Constant.STARTDOWNLOAD_LIUCANGTYPEINFO: {
                    break;
                }
                case Constant.DOWNLOAD_LIUCANGTYPEINFO_SUCCESS: {
                    activity.mDownloadProgressDialog.setMessage(activity.getString(R.string
                            .download_liucangtypeinfo_success));
                    break;
                }
                case Constant.UPDATE_LIUCANGTYPEINFO_DONE: {
                    break;
                }
                case Constant.UPDATE_LIUCANGTYPEINFO_FAILED: {
                    break;
                }


                case Constant.STARTDOWNLOAD_VEHICEINFO: {
                    break;
                }
                case Constant.DOWNLOAD_VEHICEINFO_SUCCESS: {
                    activity.mDownloadProgressDialog.setMessage(activity.getString(R.string
                            .download_vehiceinfo_success));
                    break;
                }
                case Constant.UPDATE_VEHICEINFO_DONE: {
                    break;
                }
                case Constant.UPDATE_VEHICEINFO_FAILED: {
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
