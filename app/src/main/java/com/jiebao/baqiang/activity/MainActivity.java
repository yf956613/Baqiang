package com.jiebao.baqiang.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.data.updateData.UpdateLiuCangType;
import com.jiebao.baqiang.data.updateData.UpdateSalesServiceData;
import com.jiebao.baqiang.data.updateData.UpdateShipmentType;
import com.jiebao.baqiang.data.updateData.UpdateVehicleInfo;
import com.jiebao.baqiang.global.IDownloadStatus;
import com.jiebao.baqiang.util.LogUtil;

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
    public void initData() {
        LogUtil.trace();

        initListener();

        // FIXME 何时开始启动下载动作？在onCreate()中不是在onResume中
        syncDataAction();
    }

    private void syncDataAction() {
        DataSyncTask dataSyncTask = new DataSyncTask();
        // FIXME 参数用于选择性下载，比如：start_param_1下载指定内容
        dataSyncTask.execute("start_param");
    }

    private void initListener() {
        mBtnDataCollect.setOnClickListener(this);

        mBtnDataCollect.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnQuery.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnUpload.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnPhoneMsg.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnShipmentQuery.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnAreaQuery.setOnFocusChangeListener(mLlFocusChangeListener);
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
            mDownloadProgressDialog.setMax(MAX_DOWNLOAD_COUNT);
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

                break;
            }


        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 异步下载任务
     * <p>
     * Params: 输入参数，对应excute()方法中传递的参数。如果不需要传递参数，则直接设为void即可
     * Progress：后台任务执行的百分比
     * Result：返回值类型，和doInBackground（）方法的返回值类型保持一致
     */
    class DataSyncTask extends AsyncTask<String, Integer, Long> implements IDownloadStatus {
        private int mUpdateID = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 重置状态计数器
            mUpdateID = 0;

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

        @Override
        public void downloadFinish() {
            ++mUpdateID;
            LogUtil.trace("mUpdateID:" + mUpdateID);

            Message finishMsg = Message.obtain();
            if (mUpdateID == 4) {
                finishMsg.what = DOWNLOAD_DONE;
            } else {
                finishMsg.what = DOWNLOAD_SUCCESS;
            }
            finishMsg.arg1 = mUpdateID;

            mHandler.sendMessage(finishMsg);
        }

        @Override
        public void downLoadError(String errorMsg) {
            LogUtil.trace("errorMsg: " + errorMsg);
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

    private final DownloadStatusHandler mHandler = new DownloadStatusHandler(this);
    private static final int DOWNLOAD_FAILED = 0;
    private static final int DOWNLOAD_SUCCESS = 1;
    private static final int DOWNLOAD_DONE = 2;
    private static final int MAX_DOWNLOAD_COUNT = 4;
    private static final int MAX_DOWNLOAD_STEP = 1;

    private static class DownloadStatusHandler extends Handler {
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
                case DOWNLOAD_FAILED: {

                    break;
                }
                case DOWNLOAD_SUCCESS: {
                    activity.mDownloadProgressDialog.incrementProgressBy(MAX_DOWNLOAD_STEP);
                    break;
                }

                case DOWNLOAD_DONE: {
                    activity.mDownloadProgressDialog.dismiss();
                    break;
                }

                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
}
