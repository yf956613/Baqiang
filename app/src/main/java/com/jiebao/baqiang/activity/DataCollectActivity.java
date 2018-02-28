package com.jiebao.baqiang.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.jb.barcode.BarcodeManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class DataCollectActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "DataCollectActivity";
    private LinearLayout scanLayout;
    private LinearLayout systemSetLayout;
    private LinearLayout dataImportLayout;
    private LinearLayout storgeLayout;
    private LinearLayout outputLayout;
    private LinearLayout queryLayout;
    private TextView data_import_tv;

    private TextView deviceIdTv;
    private BroadcastReceiver mLanguageSetReceiver;

    private BarcodeManager barcodeManager;
    private long nowTime = 0;
    private long lastTime = 0;
    private TextView tv_purin;
    private TextView tv_purout;
    private String mLoginUrl = "";
    private String salesId,userName,psw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLanguageSetBroadCast();
      //  boolean isBright = CacheManager.getScreenBright();
      ///  AppUtil.setScreenBright(isBright);
        LogUtil.d("DataCollectActivity", "onCreate");
        if (Build.VERSION.SDK_INT >= 23 && BaqiangApplication.isSoftDecodeScan) {
            MPermissions.requestPermissions(this, REQUEST_CAMARA_CODE, Manifest.permission.CAMERA);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("DataCollectActivity", "onRestart");
    }

    public void initView() {
        /*LinearLayout footerLayout = (LinearLayout) View.inflate(this,R.layout.main_footer_layout,null);
        setFootLayout(footerLayout);*/
        setContent(R.layout.activity_data_collect);
    }

    public void gotoUpload() {
       Intent intent = new Intent(this, UnloadCargoArrivalActivity.class);
        startActivity(intent);
    }

    public void gotoZhuangche() {
        Intent intent = new Intent(this, ZhuangcheActivity.class);
        startActivity(intent);
    }

    public void fajian() {
        Intent intent = new Intent(this, FajianActivity.class);
        startActivity(intent);
    }

    public void daojian() {
        Intent intent = new Intent(this, DaojianActivity.class);
        startActivity(intent);
    }

    public void liucang() {
        Intent intent = new Intent(this, LiucangActivity.class);
        startActivity(intent);
    }

//    public void gotoImportData() {
//        int connectMode = CacheManager.getConnectMode();
//        if(connectMode == ParamConstant.CONNECT_WIFI) {
//            Intent intent = new Intent(this, WifiImportDataActivity.class);
//            startActivity(intent);
//        }else if(connectMode == ParamConstant.CONNECT_USB){
//            Intent intent = new Intent(this, UsbImportDataActivity.class);
//            startActivity(intent);
//        }
//    }

    @Override
    protected void onDestroy() {
        AppUtil.setScreenBright(false);
        super.onDestroy();
        if(mLanguageSetReceiver != null){
            unregisterReceiver(mLanguageSetReceiver);
        }
       // CacheManager.clear();
        ScanHelper.getInstance().Close_Barcode();
    }

    @Override
    public void onClick(View view) {
        salesId = SharedUtil.getString(this, Constant
                .PREFERENCE_KEY_SALE_SERVICE);
        userName = SharedUtil.getString(this, Constant
                .PREFERENCE_KEY_USERNAME);
        psw = SharedUtil.getString(this, Constant
                .PREFERENCE_KEY_PSW);
        switch (view.getId()) {
            case R.id.scanLayout:
                IfPrePay(salesId,userName,psw);
                break;

            case R.id.storgeLayout:
                gotoZhuangche();
                break;

            case R.id.outputLayout:
                fajian();
                break;

            case R.id.queryLayout:
                daojian();
                break;

            case R.id.systemSetLayout:
//                liucang();
                break;
            case R.id.dataImportLayout:
                liucang();
                break;
        }
    }

    @Override
    public void initData() {
        scanLayout = (LinearLayout) findViewById(R.id.scanLayout);
        systemSetLayout = (LinearLayout) findViewById(R.id.systemSetLayout);
        scanLayout = (LinearLayout) findViewById(R.id.scanLayout);
        systemSetLayout = (LinearLayout) findViewById(R.id.systemSetLayout);
        dataImportLayout = (LinearLayout) findViewById(R.id.dataImportLayout);
        storgeLayout = (LinearLayout) findViewById(R.id.storgeLayout);
        outputLayout = (LinearLayout) findViewById(R.id.outputLayout);
        queryLayout = (LinearLayout) findViewById(R.id.queryLayout);

        data_import_tv = (TextView) findViewById(R.id.data_import);
        tv_purin = (TextView) findViewById(R.id.tv_purin);
        tv_purout = (TextView) findViewById(R.id.tv_purout);
        //setImportMode();

        scanLayout.setOnClickListener(this);
        systemSetLayout.setOnClickListener(this);
        dataImportLayout.setOnClickListener(this);
        storgeLayout.setOnClickListener(this);
        outputLayout.setOnClickListener(this);
        queryLayout.setOnClickListener(this);
    }

//    private void setImportMode(){
//        int connectMode = CacheManager.getConnectMode();
//        if(connectMode == ParamConstant.CONNECT_USB){
//            data_import_tv.setText(getString(R.string.main_import));
//        }else if(connectMode == ParamConstant.CONNECT_WIFI){
//            data_import_tv.setText(getString(R.string.main_download));
//        }
//    }

    private void setMenuName(){
        //tv_check.setText(CacheManager.getMainMenuAlias(ParamConstant.ALIAS_CHECK,getString(R.string.main_check)));
       // tv_purin.setText(CacheManager.getMainMenuAlias(ParamConstant.ALIAS_PURIN,getString(R.string.main_storge)));
        //tv_purout.setText(CacheManager.getMainMenuAlias(ParamConstant.ALIAS_PUROUT,getString(R.string.main_output)));
    }
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
    //验证是否有预付款

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
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

    private void initLanguageSetBroadCast(){
        mLanguageSetReceiver = new BroadcastReceiver(){
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
        //setImportMode();
        setMenuName();
    }

    @PermissionGrant(REQUEST_CAMARA_CODE)
    public void requestCameraSuccess() {

    }

    @PermissionDenied(REQUEST_CAMARA_CODE)
    public void requestCameraFail() {
        //DialogUtil.showAlertDialog(this,"fail:camera permission is not granted");
        PermissionSettingManager.showPermissionSetting(false, this, getString(R.string.tip_camare_permission),
                getString(R.string.tip_permission_setting));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
