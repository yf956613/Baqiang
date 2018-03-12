package com.jiebao.baqiang.activity;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.service.DataSyncService;
import com.jiebao.baqiang.util.LogUtil;

/**
 * 一级菜单界面，主要用户更新信息
 */

public class MainActivity extends BaseActivity implements View
        .OnClickListener, DataSyncService.DataSyncNotifity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mBtnDataCollect;
    private DataSyncService mDataSyncService;
    private ProgressDialog mDownloadProgressDialog;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.trace();

            DataSyncService.MyBinder myBinder = (DataSyncService.MyBinder)
                    service;
            mDataSyncService = myBinder.getService();
            mDataSyncService.setDataSyncNotifity(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.trace();
        }
    };
    private LinearLayout mLlDataCollect;
    private LinearLayout mLlQuery;
    private LinearLayout mLlUpload;
    private LinearLayout mLlPhoneMsg;
    private LinearLayout mLlShipmentQuery;
    private LinearLayout mLlAreaQuery;
    private LinearLayout mLlSettings;
    private Button mBtnQuery;
    private Button mBtnUpload;
    private Button mBtnPhoneMsg;
    private Button mBtnShipmentQuery;
    private Button mBtnAreaQuery;
    private Button mBtnSettings;

    @Override
    public void initView() {
        setHeaderCenterViewText("速尔手持终端软件");
        setContent(R.layout.activity_main);
    }

    @Override
    public void initData() {
        LogUtil.trace();

        // TODO 需要设置第一项默认被选中
        mBtnDataCollect = MainActivity.this.findViewById(R.id.btn_data_collect);
        mLlDataCollect = MainActivity.this.findViewById(R.id.ll_data_collect);

        mBtnQuery = MainActivity.this.findViewById(R.id.btn_query);
        mLlQuery = MainActivity.this.findViewById(R.id.ll_query);

        mBtnUpload = MainActivity.this.findViewById(R.id.btn_upload);
        mLlUpload = MainActivity.this.findViewById(R.id.ll_upload);

        mBtnPhoneMsg = MainActivity.this.findViewById(R.id.btn_phone_msg);
        mLlPhoneMsg = MainActivity.this.findViewById(R.id
                .ll_phone_msg);

        mBtnShipmentQuery = MainActivity.this.findViewById(R.id
                .btn_shipment_query);
        mLlShipmentQuery = MainActivity.this.findViewById(R.id
                .ll_shipment_query);

        mBtnAreaQuery = MainActivity.this.findViewById(R.id.btn_area_query);
        mLlAreaQuery = MainActivity.this.findViewById(R.id
                .ll_area_query);

        mBtnSettings = MainActivity.this.findViewById(R.id.btn_settings);
        mLlSettings = MainActivity.this.findViewById(R.id
                .ll_settings);

        initListener();

        startDataSync();
    }

    /**
     * 根据Button的状态，改变LinearLayout的背景
     *
     * @param v
     * @param hasFocus
     */
    private void setLinearLayoutBackground(View v, boolean hasFocus) {
        if (hasFocus) {
            v.setBackgroundResource(R.color.material_blue_500);
        } else {
            v.setBackgroundResource(R.color.bg_transparent);
        }
    }

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

    private void initListener() {
        mBtnDataCollect.setOnClickListener(this);
        mBtnDataCollect.setOnFocusChangeListener(mLlFocusChangeListener);

        mBtnQuery.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnUpload.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnPhoneMsg.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnShipmentQuery.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnAreaQuery.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnSettings.setOnFocusChangeListener(mLlFocusChangeListener);

        // TODO 让容器默认获得焦点，渲染背景，选择第一个项目
        mBtnDataCollect.setFocusable(true);
        mBtnDataCollect.setFocusableInTouchMode(true);
        mBtnDataCollect.requestFocus();
        mBtnDataCollect.requestFocusFromTouch();
    }

    private void startDataSync() {
        startService(new Intent(getApplicationContext(), DataSyncService
                .class));
        bindService(new Intent(MainActivity.this, DataSyncService.class),
                mServiceConnection,
                Service.BIND_AUTO_CREATE);

        showProgressDialog();
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
        }
    }

    @Override
    public void onSyncFinished(Exception e) {
        closeProgressDialog();
    }

    private void showProgressDialog() {
        if (mDownloadProgressDialog == null) {
            mDownloadProgressDialog = new ProgressDialog(MainActivity.this);
            mDownloadProgressDialog.setMessage("正在下载资料列表...");
            mDownloadProgressDialog.setCanceledOnTouchOutside(false);
            mDownloadProgressDialog.setCancelable(false);
        }
        mDownloadProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (mDownloadProgressDialog != null) {
            mDownloadProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                LogUtil.d(TAG, "---->按下了Back按键");
                // 消费Back事件
                // return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
