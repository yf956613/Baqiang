package com.jiebao.baqiang.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jiebao.baqiang.R;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.LoginResponse;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.global.PermissionSettingManager;
import com.jiebao.baqiang.scan.ScanHelper;
import com.jiebao.baqiang.util.AppUtil;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 数据采集界面
 */
public class DataCollectActivity extends BaseActivity implements View
        .OnClickListener {
    private static final String TAG = "DataCollectActivity";

    private BroadcastReceiver mLanguageSetReceiver;
    private String mLoginUrl = "";
    private LinearLayout mLlSendPackage;
    private LinearLayout mLlArrivePackage;
    private LinearLayout mLlLeavePackage;
    private LinearLayout mLlUnloadReceivePackage;
    private LinearLayout mLlLoadSend;

    public void initView() {
        initLanguageSetBroadCast();

        setHeaderCenterViewText("采集功能项");
        LogUtil.d("DataCollectActivity", "onCreate");

        if (Build.VERSION.SDK_INT >= 23 && BaqiangApplication
                .isSoftDecodeScan) {
            MPermissions.requestPermissions(this, REQUEST_CAMARA_CODE,
                    Manifest.permission.CAMERA);
        }

        /*LinearLayout footerLayout = (LinearLayout) View.inflate(this,R
        .layout.main_footer_layout,null);
        setFootLayout(footerLayout);*/
        setContent(R.layout.activity_data_collect);
    }

    @Override
    public void initData() {
        // 装车发件
        mLlLoadSend = DataCollectActivity.this.findViewById(R.id.ll_load_send);
        mLlLoadSend.setOnClickListener(this);

        // 卸车到件
        mLlUnloadReceivePackage = DataCollectActivity.this.findViewById(R.id
                .ll_unload_receive_package);
        mLlUnloadReceivePackage.setOnClickListener(this);

        // 到件
        mLlArrivePackage = DataCollectActivity.this.findViewById(R.id
                .ll_arrive_package);
        mLlArrivePackage.setOnClickListener(this);

        // 发件
        mLlSendPackage = DataCollectActivity.this.findViewById(R.id
                .ll_send_package);
        mLlSendPackage.setOnClickListener(this);

        // 留仓
        mLlLeavePackage = DataCollectActivity.this.findViewById(R.id
                .ll_leave_package);
        mLlLeavePackage.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        LogUtil.d("DataCollectActivity", "onRestart");
    }

    @Override
    protected void onDestroy() {
        AppUtil.setScreenBright(false);
        super.onDestroy();
        if (mLanguageSetReceiver != null) {
            unregisterReceiver(mLanguageSetReceiver);
        }
        // CacheManager.clear();
        ScanHelper.getInstance().Close_Barcode();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            // 装车发件
            case R.id.ll_load_send:{
                gotoZhuangche();
                break;
            }

            // 卸车到件
            case R.id.ll_unload_receive_package:{
                gotoUpload();
                break;
            }

            // 发件
            case R.id.ll_send_package: {
                fajian();
                break;
            }

            // 到件
            case R.id.ll_arrive_package: {
                daojian();
                break;
            }

            // 留仓件
            case R.id.ll_leave_package: {
                liucang();

                break;
            }
        }
    }

    /**
     * 设置预付款
     *
     * @param saleId
     * @param account
     * @param pwd
     */
    public void IfPrePay(final String saleId, final String account, final String
            pwd) {
        mLoginUrl = SharedUtil.getServletAddresFromSP(BaqiangApplication
                        .getContext(),
                NetworkConstant.PREPAY_STATE);
        LogUtil.trace("path:" + mLoginUrl);

        RequestParams params = new RequestParams(mLoginUrl);
        // TODO 测试阶段写死
            /*params.addQueryStringParameter("saleId", saleId);
            params.addQueryStringParameter("userName", account);
            params.addQueryStringParameter("password", pwd);*/
        params.addQueryStringParameter("saleId", saleId);
        params.addQueryStringParameter("userName", account);
        params.addQueryStringParameter("password", pwd);
        LogUtil.e(TAG, "saleId:" + saleId + "; userName:" + account + "; " +
                "pwd:" + pwd);

        // TODO 从日志看出，下述回调都是在MainThread运行的
        final Callback.Cancelable post = x.http().post(params, new Callback
                .CommonCallback<String>() {

            @Override
            public void onSuccess(String s) {
                LogUtil.trace("return s:" + s);

                Gson gson = new Gson();
                LoginResponse loginResponse = gson.fromJson(s,
                        LoginResponse.class);
                if (loginResponse != null) {
                    if ("1".equals(loginResponse.getAuthRet())) {
                        gotoUpload();
                    } else {
                        Toast.makeText(BaqiangApplication.getContext(),
                                "预付款不足", Toast
                                        .LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BaqiangApplication.getContext(),
                            "预付款不足", Toast
                                    .LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.trace("error exception: " + throwable.getMessage());
            }

            @Override
            public void onCancelled(CancelledException e) {
                LogUtil.trace();
            }

            @Override
            public void onFinished() {
                LogUtil.trace();
                gotoUpload();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_1:
                    gotoUpload();
                    break;
                case KeyEvent.KEYCODE_2:
                    gotoZhuangche();
                    break;
                case KeyEvent.KEYCODE_3:
                    fajian();
                    break;
                case KeyEvent.KEYCODE_4:
                    daojian();
                    break;
                case KeyEvent.KEYCODE_5:
                    liucang();
                    break;
                case KeyEvent.KEYCODE_6:
                    //gotoSystemSet();
                    break;
                default:
                    break;
            }
        }*/

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void handler(Message msg) {
        switch (msg.what) {
            case Constant.QUERY_DEVICE_ID:
//                deviceIdTv.setText(getString(R.string.device_id)
//                        + new String((byte[]) msg.obj));
                break;
        }
    }

    private void initLanguageSetBroadCast() {
        mLanguageSetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Intent it = new Intent(context, DataCollectActivity.class);
//                Intent it = new Intent(context, SplashActivity.class);
//                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(it);
                //  AppManager.getAppManager().finishAllActivity();
                AppUtil.restartApp();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("multiLanguageChanged");
        registerReceiver(mLanguageSetReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("DataCollectActivity", "onResume");
    }

    @PermissionGrant(REQUEST_CAMARA_CODE)
    public void requestCameraSuccess() {

    }

    @PermissionDenied(REQUEST_CAMARA_CODE)
    public void requestCameraFail() {
        //DialogUtil.showAlertDialog(this,"fail:camera permission is not
        // granted");
        PermissionSettingManager.showPermissionSetting(false, this, getString
                        (R.string.tip_camare_permission),
                getString(R.string.tip_permission_setting));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode,
                permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
    }

    /**
     * 卸车到件
     */
    public void gotoUpload() {
        Intent intent = new Intent(this, UnloadCargoArrivalActivity.class);
        startActivity(intent);
    }

    /**
     * 装车发件
     */
    public void gotoZhuangche() {
        Intent intent = new Intent(this, ZhuangcheActivity.class);
        startActivity(intent);
    }

    /**
     * 发件
     */
    public void fajian() {
        Intent intent = new Intent(this, FajianActivity.class);
        startActivity(intent);
    }

    /**
     * 到件
     */
    public void daojian() {
        // TODO 功能测试
        Intent intent = new Intent(this, DaojianActivity.class);
        startActivity(intent);
    }

    /**
     * 留仓
     */
    public void liucang() {
        Intent intent = new Intent(this, LiucangActivity.class);
        startActivity(intent);
    }
}
