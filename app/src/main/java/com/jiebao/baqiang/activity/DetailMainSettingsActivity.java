package com.jiebao.baqiang.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class DetailMainSettingsActivity extends BaseActivityWithTitleAndNumber implements View
        .OnClickListener {
    private static final String TAG = DetailMainSettingsActivity.class.getSimpleName();

    @ViewInject(R.id.btn_auto_upload_time)
    private Button mBtnAutoUploadTime;
    @ViewInject(R.id.ll_auto_upload_time)
    private LinearLayout mLlAutoUploadTime;

    @ViewInject(R.id.btn_sign_settings)
    private Button mBtnSignSettings;
    @ViewInject(R.id.ll_sign_settings)
    private LinearLayout mLlSignSettings;

    @ViewInject(R.id.btn_redo_scan)
    private Button mBtnRedoScan;
    @ViewInject(R.id.ll_redo_scan)
    private LinearLayout mLlRedoScan;

    private final View.OnFocusChangeListener mLlFocusChangeListener = new View
            .OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.btn_auto_upload_time: {
                    setLinearLayoutBackground(mLlAutoUploadTime, hasFocus);
                    break;
                }

                case R.id.btn_sign_settings: {
                    setLinearLayoutBackground(mLlSignSettings, hasFocus);
                    break;
                }

                case R.id.btn_redo_scan: {
                    setLinearLayoutBackground(mLlRedoScan, hasFocus);
                    break;
                }
            }
        }
    };

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

    @Override
    public void initView() {
        setContent(R.layout.activity_detail_main_settings);
        setHeaderLeftViewText("业务设置界面");
        x.view().inject(DetailMainSettingsActivity.this);
    }

    @Override
    public void initData() {
        initListener();
    }

    private void initListener() {
        mBtnAutoUploadTime.setOnClickListener(this);
        mBtnAutoUploadTime.setOnFocusChangeListener(mLlFocusChangeListener);

        mBtnSignSettings.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnRedoScan.setOnFocusChangeListener(mLlFocusChangeListener);

        // 让容器默认获得焦点，渲染背景，选择第一个项目
        mBtnAutoUploadTime.setFocusable(true);
        mBtnAutoUploadTime.setFocusableInTouchMode(true);
        mBtnAutoUploadTime.requestFocus();
        mBtnAutoUploadTime.requestFocusFromTouch();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_auto_upload_time: {
                showAlertDialogForTimeSettings();
                break;
            }
        }
    }

    private void initAutoUploadRecords() {
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);

        int tenMinutes = 1 * 60 * 1000;
        long triggerAtMillis = System.currentTimeMillis() + tenMinutes;
        long intervalMillis = 1 * 60 * 1000;
        int requestCode = 0;

        Intent intent = new Intent();
        intent.setAction(Constant.AUTO_ACTION_UPLOAD_RECORDS);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        /**
         * int type：闹钟类型 AlarmManager.RTC_WAKEUP 使用绝对时间
         * long triggerAtMillis：闹钟首次执行时间，如果设置为（绝对时间）System.currentTimeMillis()会默认在5秒后执行首次
         *                          如果设置成System.currentTimeMillis() + 20 * 1000，首次执行在20秒后
         * long intervalMillis：两次执行时间间隔
         * PendingIntent operation：闹钟响应动作
         */
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis,
                mPendingIntent);
    }

    /**
     * 自动上传时间设置
     */
    private void showAlertDialogForTimeSettings() {
        final android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog
                .Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout.layout_auto_upload_records,
                null));
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setContentView(R.layout.layout_auto_upload_records);
        Button btnPositive = (Button) dialog.findViewById(R.id.btn_sure);
        Button btnNegative = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText etContent = (EditText) dialog.findViewById(R.id.edt_auto_time);

        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String setTime = etContent.getText().toString();
                if (!TextUtils.isEmpty(setTime)) {
                    int intervalMinute = Integer.parseInt(setTime);

                    AlarmManager mAlarmManager = (AlarmManager) getSystemService(Service
                            .ALARM_SERVICE);
                    Intent intent = new Intent();
                    intent.setAction(Constant.AUTO_ACTION_UPLOAD_RECORDS);

                    long intervalMillis = intervalMinute * 60 * 1000;
                    long triggerAtMillis = System.currentTimeMillis() + intervalMillis;
                    PendingIntent mPendingIntent = PendingIntent.getBroadcast
                            (DetailMainSettingsActivity.this, 0, intent, PendingIntent
                                    .FLAG_UPDATE_CURRENT);
                    mAlarmManager.cancel(mPendingIntent);

                    mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis,
                            intervalMillis, mPendingIntent);

                    dialog.dismiss();
                }
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
