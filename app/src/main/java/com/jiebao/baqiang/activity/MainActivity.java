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

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.service.DataSyncService;
import com.jiebao.baqiang.util.LogUtil;

/**
 * 一级菜单界面，主要用户更新信息
 */

public class MainActivity extends BaseActivity implements View
        .OnClickListener, DataSyncService.DataSyncNotifity {
    private static final String TAG = MainActivity.class.getSimpleName();

    // 数据采集
    private Button mBtnDataCollect;
    private DataSyncService dataSyncService;

    private ProgressDialog mDownloadProgressDialog;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.trace();

            DataSyncService.MyBinder myBinder = (DataSyncService.MyBinder)
                    service;
            dataSyncService = myBinder.getService();
            dataSyncService.setDataSyncNotifity(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.trace();
        }
    };

    @Override
    public void initView() {
        setHeaderCenterViewText("速尔手持终端软件");
        setContent(R.layout.activity_main);
    }

    @Override
    public void initData() {
        mBtnDataCollect = findViewById(R.id.btn_data_collect);
        initListener();

        // TODO 数据更新
        startDataSync();
    }

    private void initListener() {
        mBtnDataCollect.setOnClickListener(this);
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
