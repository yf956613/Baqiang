package com.jiebao.baqiang.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jiebao.baqiang.R;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.AppUpdateBean;
import com.jiebao.baqiang.data.updateData.UpdateInterface;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.service.DownLoadApkFileService;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by yaya on 2018/2/26.
 */

public class AdministratorSettingActivity extends Activity implements View
        .OnClickListener {
    private static final String TAG = AdministratorSettingActivity.class
            .getSimpleName();

    private LinearLayout mLlServer;
    private Button mServerID;
    private Button mDeviceID;
    private Button mBtnAppUpdate;
    private Button mServerConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_settings);

        initData();
    }

    private void initData() {
        // 服务器设置
        mServerConfig = findViewById(R.id.server_config);
        mServerConfig.setOnClickListener(this);

        // 网点编号
        mServerID = findViewById(R.id.server_id);
        mServerID.setOnClickListener(this);

        // 巴枪编号
        mDeviceID = findViewById(R.id.device_id);
        mDeviceID.setOnClickListener(this);

        // 软件更新
        mBtnAppUpdate = findViewById(R.id.app_update);
        mBtnAppUpdate.setOnClickListener(this);
    }

    private void showAlertDialogForServerID() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout
                .alert_dialog, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.alert_dialog);
        Button btnPositive = (Button) dialog.findViewById(R.id.btn_add);
        Button btnNegative = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText etContent = (EditText) dialog.findViewById(R.id
                .et_content);
        etContent.setText(SharedUtil.getString(AdministratorSettingActivity
                .this, "server_id"));

        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String str = etContent.getText().toString();
                if (isNullEmptyBlank(str)) {
                    etContent.setError("保存内容不能为空");
                } else {
                    dialog.dismiss();

                    // 保存网点编号
                    SharedUtil.putString(AdministratorSettingActivity.this,
                            "server_id", etContent.getText().toString());
                    /*LogUtil.trace("return:" + SharedUtil.getString
                            (AdministratorSettingActivity.this, "server_id"));*/
                    Toast.makeText(AdministratorSettingActivity.this,
                            "网点编号存储成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }

    private void showAlertDialogForDeviceID() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout
                .alert_dialog_device_id, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.alert_dialog_device_id);
        Button btnPositive = (Button) dialog.findViewById(R.id.btn_add);
        Button btnNegative = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText etContent = (EditText) dialog.findViewById(R.id
                .et_content);
        etContent.setText(SharedUtil.getString(AdministratorSettingActivity
                .this, "device_id"));

        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String str = etContent.getText().toString();
                if (isNullEmptyBlank(str)) {
                    etContent.setError("保存内容不能为空");
                } else {
                    dialog.dismiss();

                    // 保存网点编号
                    SharedUtil.putString(AdministratorSettingActivity.this,
                            "device_id", etContent.getText().toString());
                    /*LogUtil.trace("return:" + SharedUtil.getString
                            (AdministratorSettingActivity.this, "device_id"));*/
                    Toast.makeText(AdministratorSettingActivity.this,
                            "巴枪编号存储成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }

    private static boolean isNullEmptyBlank(String str) {
        if (str == null || "".equals(str) || "".equals(str.trim()))
            return true;
        return false;
    }

    private static String mUpdateAPPUrl = "";
    private static String mApkFileDownloadUrl = "";

    private void showAlertDialogForAppUpdate() {
        new AlertDialog.Builder(this)
                .setTitle("软件升级")
                .setMessage("确定升级软件？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 请求服务器获取最新apk信息 APP_UPDATE_INFO
                        mUpdateAPPUrl = SharedUtil.getServletAddresFromSP
                                (BaqiangApplication.getContext(),
                                        NetworkConstant
                                                .APP_UPDATE_INFO);
                        RequestParams params = new RequestParams(mUpdateAPPUrl);

                        params.addQueryStringParameter("saleId",
                                UpdateInterface.salesId);
                        params.addQueryStringParameter("userName",
                                UpdateInterface.userName);
                        params.addQueryStringParameter("password",
                                UpdateInterface.psw);

                        x.http().post(params, new Callback
                                .CommonCallback<String>() {

                            @Override
                            public void onSuccess(String serverInfo) {
                                LogUtil.trace();

                                Gson gson = new Gson();
                                AppUpdateBean appInfo = gson.fromJson
                                        (serverInfo,
                                                AppUpdateBean.class);
                                LogUtil.trace("appInfo:" + appInfo.toString());

                                if (getCurrentVersionCode() <
                                        resolveServerAppVersionCode(appInfo)) {
                                    LogUtil.trace("start to download");
                                    mApkFileDownloadUrl = SharedUtil
                                            .getServletAddresFromSP
                                                    (BaqiangApplication
                                                                    .getContext(),
                                                            NetworkConstant
                                                                    .APK_DOWNLOAD_URL);

                                    Intent service = new Intent
                                            (AdministratorSettingActivity.this,
                                                    DownLoadApkFileService
                                                            .class);
                                    service.putExtra("downloadurl",
                                            mApkFileDownloadUrl);
                                    Toast.makeText
                                            (AdministratorSettingActivity.this,
                                                    "正在下载中", Toast
                                                            .LENGTH_SHORT)
                                            .show();
                                    startService(service);
                                }
                            }

                            @Override
                            public void onError(Throwable throwable, boolean
                                    b) {
                                LogUtil.trace(throwable.getMessage());

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
                })
                .create().show();
    }

    private int resolveServerAppVersionCode(AppUpdateBean appInfo) {
        String versionInfo = appInfo.getBaQiangApkVersion();
        String[] array = versionInfo.split("_");
        String versionCode = array[1];

        return Integer.parseInt(versionCode.replace(".apk", ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.server_config: {
                LogUtil.trace();

                Intent intent = new Intent(AdministratorSettingActivity.this,
                        ServerConfigActivity.class);
                AdministratorSettingActivity.this.startActivity(intent);

                break;
            }

            case R.id.server_id: {
                LogUtil.trace("goto server id");
                showAlertDialogForServerID();

                break;
            }

            case R.id.device_id: {
                LogUtil.trace("goto device id");
                showAlertDialogForDeviceID();

                break;
            }

            case R.id.app_update: {
                LogUtil.trace("goto app update");
                showAlertDialogForAppUpdate();

                break;
            }
        }
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
}
