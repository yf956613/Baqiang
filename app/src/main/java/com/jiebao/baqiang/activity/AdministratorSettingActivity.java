package com.jiebao.baqiang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
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
import com.jiebao.baqiang.data.arrival.CargoArrivalFileContent;
import com.jiebao.baqiang.data.arrival.UnloadArrivalFileContent;
import com.jiebao.baqiang.data.bean.AppUpdateBean;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.dispatch.ShipmentFileContent;
import com.jiebao.baqiang.data.stay.StayHouseFileContent;
import com.jiebao.baqiang.data.updateData.UpdateInterface;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.service.DownLoadApkFileService;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by yaya on 2018/2/26.
 */

public class AdministratorSettingActivity extends
        BaseActivityWithTitleAndNumber implements View
        .OnClickListener {
    private static final String TAG = AdministratorSettingActivity.class
            .getSimpleName();

    @ViewInject(R.id.btn_server_config)
    private Button mBtnServerConfig;
    @ViewInject(R.id.ll_server_config)
    private LinearLayout mLlServerConfig;

    @ViewInject(R.id.btn_server_id)
    private Button mBtnServerID;
    @ViewInject(R.id.ll_server_id)
    private LinearLayout mLlServerID;

    @ViewInject(R.id.btn_device_id)
    private Button mBtnDeviceID;
    @ViewInject(R.id.ll_device_id)
    private LinearLayout mLlDeviceID;

    @ViewInject(R.id.btn_bussiness)
    private Button mBtnBussinessSettings;
    @ViewInject(R.id.ll_bussiness)
    private LinearLayout mLlBussinessSettings;

    @ViewInject(R.id.btn_factory)
    private Button mBtnFactory;
    @ViewInject(R.id.ll_factory)
    private LinearLayout mLlFactory;

    @ViewInject(R.id.btn_bussiness_before)
    private Button mBtnBussinessBefore;
    @ViewInject(R.id.ll_bussiness_before)
    private LinearLayout mLlBussinessBefore;

    @ViewInject(R.id.btn_wipe_data)
    private Button mBtnSwipeData;
    @ViewInject(R.id.ll_wipe_data)
    private LinearLayout mLlSwipeData;

    @ViewInject(R.id.btn_logcat)
    private Button mBtnLogcat;
    @ViewInject(R.id.ll_logcat)
    private LinearLayout mLlLogcat;

    @ViewInject(R.id.btn_wifi_settings)
    private Button mBtnWifiSetting;
    @ViewInject(R.id.ll_wifi_settings)
    private LinearLayout mLlWifiSetting;

    @ViewInject(R.id.btn_app_update)
    private Button mBtnAppUpdate;
    @ViewInject(R.id.ll_app_update)
    private LinearLayout mLlAppUpdate;

    private final View.OnFocusChangeListener mLlFocusChangeListener = new View
            .OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.btn_server_config: {
                    setLinearLayoutBackground(mLlServerConfig, hasFocus);
                    break;
                }

                case R.id.btn_server_id: {
                    setLinearLayoutBackground(mLlServerID, hasFocus);
                    break;
                }

                case R.id.btn_device_id: {
                    setLinearLayoutBackground(mLlDeviceID, hasFocus);
                    break;
                }

                case R.id.btn_bussiness: {
                    setLinearLayoutBackground(mLlBussinessSettings, hasFocus);
                    break;
                }

                case R.id.btn_factory: {
                    setLinearLayoutBackground(mLlFactory, hasFocus);
                    break;
                }

                case R.id.btn_bussiness_before: {
                    setLinearLayoutBackground(mLlBussinessBefore, hasFocus);
                    break;
                }


                case R.id.btn_wipe_data: {
                    setLinearLayoutBackground(mLlSwipeData, hasFocus);
                    break;
                }

                case R.id.btn_logcat: {
                    setLinearLayoutBackground(mLlLogcat, hasFocus);
                    break;
                }

                case R.id.btn_wifi_settings: {
                    setLinearLayoutBackground(mLlWifiSetting, hasFocus);
                    break;
                }

                case R.id.btn_app_update: {
                    setLinearLayoutBackground(mLlAppUpdate, hasFocus);
                    break;
                }
            }
        }
    };

    @Override
    public void initView() {
        setContent(R.layout.activity_admin_settings);
        setHeaderLeftViewText("管理员账号设置");
        x.view().inject(AdministratorSettingActivity.this);
    }

    @Override
    public void initData() {
        // 服务器设置
        mBtnServerConfig.setOnClickListener(this);
        mBtnServerConfig.setOnFocusChangeListener(mLlFocusChangeListener);

        // TODO 默认选择第一个选项
        mBtnServerConfig.setFocusable(true);
        mBtnServerConfig.setFocusableInTouchMode(true);
        mBtnServerConfig.requestFocus();
        mBtnServerConfig.requestFocusFromTouch();

        // 网点编号
        mBtnServerID.setOnClickListener(this);
        mBtnServerID.setOnFocusChangeListener(mLlFocusChangeListener);

        // 巴枪编号
        mBtnDeviceID.setOnClickListener(this);
        mBtnDeviceID.setOnFocusChangeListener(mLlFocusChangeListener);

        // 业务设置
        mBtnBussinessSettings.setOnClickListener(this);
        mBtnBussinessSettings.setOnFocusChangeListener(mLlFocusChangeListener);

        // 出厂预设
        mBtnFactory.setOnClickListener(this);
        mBtnFactory.setOnFocusChangeListener(mLlFocusChangeListener);

        // 业务预设
        mBtnBussinessBefore.setOnClickListener(this);
        mBtnBussinessBefore.setOnFocusChangeListener(mLlFocusChangeListener);

        // 巴枪清空
        mBtnSwipeData.setOnClickListener(this);
        mBtnSwipeData.setOnFocusChangeListener(mLlFocusChangeListener);

        // 日志管理
        mBtnLogcat.setOnClickListener(this);
        mBtnLogcat.setOnFocusChangeListener(mLlFocusChangeListener);

        // WIFI 设置
        mBtnWifiSetting.setOnClickListener(this);
        mBtnWifiSetting.setOnFocusChangeListener(mLlFocusChangeListener);

        // 软件更新
        mBtnAppUpdate.setOnClickListener(this);
        mBtnAppUpdate.setOnFocusChangeListener(mLlFocusChangeListener);
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

    /**
     * 网点编号设置，限制：只能输入数字和英文字母
     */
    private void showAlertDialogForServerID() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout
                .alert_dialog_set_serviceid, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.alert_dialog_set_serviceid);
        Button btnPositive = (Button) dialog.findViewById(R.id.btn_add);
        Button btnNegative = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText etContent = (EditText) dialog.findViewById(R.id
                .et_content);
        etContent.setText(SharedUtil.getString(AdministratorSettingActivity
                .this, Constant.PREFERENCE_KEY_SALE_SERVICE));

        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String str = etContent.getText().toString();
                if (isNullEmptyBlank(str)) {
                    etContent.setError("保存内容不能为空");
                } else {
                    dialog.dismiss();

                    SharedUtil.putString(AdministratorSettingActivity.this,
                            Constant.PREFERENCE_KEY_SALE_SERVICE, etContent
                                    .getText()
                                    .toString());
                    /*LogUtil.trace("return:" + SharedUtil.getString
                            (AdministratorSettingActivity.this, "server_id"));*/
                    Toast.makeText(AdministratorSettingActivity.this,
                            "网点编号存储成功", Toast
                                    .LENGTH_SHORT).show();
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
                            Constant
                                    .PREFERENCE_KEY_DEVICE_ID, etContent
                                    .getText()
                                    .toString());
                    /*LogUtil.trace("return:" + SharedUtil.getString
                            (AdministratorSettingActivity.this, "device_id"));*/
                    Toast.makeText(AdministratorSettingActivity.this,
                            "巴枪编号存储成功", Toast
                                    .LENGTH_SHORT).show();
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
        if (str == null || "".equals(str) || "".equals(str.trim())) return true;
        return false;
    }

    private static String mUpdateAPPUrl = "";
    private static String mApkFileDownloadUrl = "";

    private void showAlertDialogForAppUpdate() {
        new AlertDialog.Builder(this).setTitle("软件升级").setMessage("确定升级软件？")
                .setNegativeButton
                        ("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int
                                    which) {

                            }
                        }).setPositiveButton("确定", new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 请求服务器获取最新apk信息 APP_UPDATE_INFO
                mUpdateAPPUrl = SharedUtil.getJiebaoServletAddresFromSP
                        (BaqiangApplication
                                .getContext(), NetworkConstant.APP_UPDATE_INFO);
                RequestParams params = new RequestParams(mUpdateAPPUrl);

                params.addQueryStringParameter("saleId", UpdateInterface
                        .salesId);
                params.addQueryStringParameter("userName", UpdateInterface
                        .userName);
                params.addQueryStringParameter("password", UpdateInterface.psw);

                x.http().post(params, new Callback.CommonCallback<String>() {

                    @Override
                    public void onSuccess(String serverInfo) {
                        LogUtil.trace();

                        Gson gson = new Gson();
                        AppUpdateBean appInfo = gson.fromJson(serverInfo,
                                AppUpdateBean.class);
                        LogUtil.trace("appInfo:" + appInfo.toString());

                        if ("unknown".equals(appInfo.getBaQiangApkVersion())) {
                            Toast.makeText(AdministratorSettingActivity.this,
                                    "服务器未放置Apk文件",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (getCurrentVersionCode() <
                                resolveServerAppVersionCode(appInfo)) {
                            LogUtil.trace("start to download");
                            mApkFileDownloadUrl = SharedUtil
                                    .getJiebaoServletAddresFromSP
                                            (BaqiangApplication.getContext(),
                                                    NetworkConstant
                                                            .APK_DOWNLOAD_URL);

                            Intent service = new Intent
                                    (AdministratorSettingActivity.this,
                                            DownLoadApkFileService.class);
                            service.putExtra("downloadurl",
                                    mApkFileDownloadUrl);
                            Toast.makeText(AdministratorSettingActivity.this,
                                    "正在下载中", Toast
                                            .LENGTH_LONG).show();
                            startService(service);
                        } else {
                            Toast.makeText(AdministratorSettingActivity.this,
                                    "APK已经是最新版本!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
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
        }).create().show();
    }

    private int resolveServerAppVersionCode(AppUpdateBean appInfo) {
        String versionInfo = appInfo.getBaQiangApkVersion();
        String[] array = versionInfo.split("_");
        String versionCode = array[1];

        return Integer.parseInt(versionCode.replace(".apk", ""));
    }

    private void showDialogForWipeData() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout
                .alert_dialog_wipe_data, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.alert_dialog_wipe_data);
        Button btnPositive = (Button) dialog.findViewById(R.id.btn_add);
        Button btnNegative = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText etContent = (EditText) dialog.findViewById(R.id
                .et_content);

        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String str = etContent.getText().toString();
                if (isNullEmptyBlank(str)) {
                    etContent.setError("密码不能为空");
                } else {
                    if ("888888".equals(str)) {
                        // 清空数据密码为：888888，执行清空数据操作
                        wipeAppData();
                    } else {
                        // 密码错误
                        Toast.makeText(AdministratorSettingActivity.this,
                                "密码错误", Toast
                                        .LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
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

    /**
     * 删除sdcard存储的文件，包括：bqDB目录、BaQiang目录、bqapk目录
     */
    private void wipeAppData() {
        File rootFile = new File(Environment.getExternalStorageDirectory()
                .getPath());
        LogUtil.trace("path:" + rootFile.getAbsolutePath());

        // 删除目录
        deleteFile(new File(rootFile.getPath() + "/bqapk"));
        deleteFile(new File(rootFile.getPath() + "/BaQiang"));

        // FIXME 暂时不删除数据库文件，修改为删除Table中的Records
        // deleteFile(new File(rootFile.getPath() + "/bqDB"));

        deleteTableRecords();
    }


    // 7天
    private static long SEVEN_TIME_DATE = 604800000L;

    /**
     * 删除DB中存放的记录，具体参考Constant中字段
     */
    private void deleteTableRecords() {
        Date dateLimited = new Date(new Date().getTime() -
                SEVEN_TIME_DATE);
        LogUtil.trace("当前时间：" + new SimpleDateFormat("yyyyMMddHHmmss").format
                (new Date()) + "; 清除：" + new SimpleDateFormat("yyyyMMddHHmmss")
                .format(dateLimited) + "; 之前的数据");

        try {
            // 装车发件
            DbManager dbManager = BQDataBaseHelper.getDb();
            if (dbManager != null) {
                List<ZCFajianFileContent> zcFajianFileContents = dbManager
                        .selector
                                (ZCFajianFileContent.class).where
                                ("ScanDate", "<=", dateLimited).findAll();
                if (zcFajianFileContents != null) {
                    for (int index = 0; index < zcFajianFileContents.size();
                         index++) {
                        dbManager.delete(ZCFajianFileContent.class,
                                WhereBuilder.b("id", "=", zcFajianFileContents
                                        .get(index).getId()));
                    }
                }

                // 卸车到件
                List<UnloadArrivalFileContent> unloadArrivalFileContents =
                        dbManager.selector(UnloadArrivalFileContent.class).where
                                ("ScanDate", "<=", dateLimited).findAll();
                if (unloadArrivalFileContents != null) {
                    for (int index = 0; index < unloadArrivalFileContents
                            .size();
                         index++) {
                        dbManager.delete(UnloadArrivalFileContent.class,
                                WhereBuilder.b("id", "=",
                                        unloadArrivalFileContents
                                                .get(index).getId()));
                    }
                }

                // 到件
                List<CargoArrivalFileContent> cargoArrivalFileContents =
                        dbManager.selector(CargoArrivalFileContent.class).where
                                ("ScanDate", "<=", dateLimited).findAll();
                if (cargoArrivalFileContents != null) {
                    for (int index = 0; index < cargoArrivalFileContents.size();
                         index++) {
                        dbManager.delete(CargoArrivalFileContent.class,
                                WhereBuilder.b("id", "=",
                                        cargoArrivalFileContents
                                                .get(index).getId()));
                    }
                }

                // 发件
                List<ShipmentFileContent> shipmentFileContents =
                        dbManager.selector(ShipmentFileContent.class).where
                                ("ScanDate", "<=", dateLimited).findAll();
                if (shipmentFileContents != null) {
                    for (int index = 0; index < shipmentFileContents.size();
                         index++) {
                        dbManager.delete(ShipmentFileContent.class,
                                WhereBuilder.b("id", "=", shipmentFileContents
                                        .get(index).getId()));
                    }
                }

                // 留仓件
                List<StayHouseFileContent> stayHouseFileContents =
                        dbManager.selector(StayHouseFileContent.class).where
                                ("ScanDate", "<=", dateLimited).findAll();

                if (stayHouseFileContents != null) {
                    for (int index = 0; index < stayHouseFileContents.size();
                         index++) {
                        dbManager.delete(StayHouseFileContent.class,
                                WhereBuilder.b("id", "=", stayHouseFileContents
                                        .get(index)
                                        .getId()));
                    }
                }
            } else {
                // do nothing
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            // 注释这行：保留文件夹，只删除文件
            file.delete();
        } else if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // TODO 服务器设置
            case R.id.btn_server_config: {
                LogUtil.trace();

                Intent intent = new Intent(AdministratorSettingActivity.this,
                        ServerConfigActivity.class);
                AdministratorSettingActivity.this.startActivity(intent);

                break;
            }

            case R.id.btn_server_id: {
                LogUtil.trace("goto server id");
                // TODO 网点编号
                showAlertDialogForServerID();

                break;
            }

            case R.id.btn_device_id: {
                LogUtil.trace("goto device id");
                // TODO 巴枪编号
                showAlertDialogForDeviceID();

                break;
            }

            case R.id.btn_bussiness: {
                LogUtil.trace("goto business settings");

                // 业务预设
                Intent intent = new Intent(AdministratorSettingActivity.this,
                        BusinessSettingsActivity.class);
                AdministratorSettingActivity.this.startActivity(intent);

                break;
            }

            case R.id.btn_wipe_data: {
                // 巴枪清空
                LogUtil.trace("goto wipe data");
                showDialogForWipeData();

                break;
            }

            case R.id.btn_wifi_settings: {
                LogUtil.trace("goto wifi settings");

                // TODO WIFI设置
                Intent intent = new Intent();
                intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                startActivity(intent);

                break;
            }

            case R.id.btn_app_update: {
                // 软件更新
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
