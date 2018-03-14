package com.jiebao.baqiang.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jiebao.baqiang.R;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.LoginResponse;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.global.PermissionSettingManager;
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
public class DataCollectActivity extends BaseActivityWithTitleAndNumber implements View
        .OnClickListener {
    private static final String TAG = "DataCollectActivity";

    private String mLoginUrl = "";
    private LinearLayout mLlSendPackage;

    private LinearLayout mLlLeavePackage;

    private LinearLayout mLlLoadSend;
    private Button mBtnLoadSend;

    private LinearLayout mLlUnloadReceivePackage;
    private Button mBtnUnloadReceivePackage;

    private LinearLayout mLlArrivePackage;
    private Button mBtnArrivePackage;
    private Button mBtnSendPackage;
    private Button mBtnLeavePackage;

    private final View.OnFocusChangeListener mLlFocusChangeListener = new View
            .OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.btn_load_send: {
                    setLinearLayoutBackground(mLlLoadSend, hasFocus);
                    break;
                }

                case R.id.btn_unload_receive_package: {
                    setLinearLayoutBackground(mLlUnloadReceivePackage, hasFocus);
                    break;
                }

                case R.id.btn_arrive_package: {
                    setLinearLayoutBackground(mLlArrivePackage, hasFocus);
                    break;
                }

                case R.id.btn_send_package: {
                    setLinearLayoutBackground(mLlSendPackage, hasFocus);
                    break;
                }

                case R.id.btn_leave_package: {
                    setLinearLayoutBackground(mLlLeavePackage, hasFocus);
                    break;
                }
            }
        }
    };

    @Override
    public void initView() {
        setHeaderLeftViewText("采集功能项");
        LogUtil.d(TAG, "onCreate");

        if (Build.VERSION.SDK_INT >= 23 && BaqiangApplication.isSoftDecodeScan) {
            MPermissions.requestPermissions(this, REQUEST_CAMARA_CODE, Manifest.permission.CAMERA);
        }

        setContent(R.layout.activity_data_collect);
    }

    @Override
    public void initData() {
        // 装车发件
        mLlLoadSend = DataCollectActivity.this.findViewById(R.id.ll_load_send);
        mBtnLoadSend = DataCollectActivity.this.findViewById(R.id.btn_load_send);
        mBtnLoadSend.setOnClickListener(this);
        mBtnLoadSend.setOnFocusChangeListener(mLlFocusChangeListener);

        // TODO 让容器默认获得焦点，渲染背景，选择第一个项目
        mBtnLoadSend.setFocusable(true);
        mBtnLoadSend.setFocusableInTouchMode(true);
        mBtnLoadSend.requestFocus();
        mBtnLoadSend.requestFocusFromTouch();

        // 卸车到件
        mLlUnloadReceivePackage = DataCollectActivity.this.findViewById(R.id
                .ll_unload_receive_package);
        mBtnUnloadReceivePackage = DataCollectActivity.this.findViewById(R.id
                .btn_unload_receive_package);
        mBtnUnloadReceivePackage.setOnClickListener(this);
        mBtnUnloadReceivePackage.setOnFocusChangeListener(mLlFocusChangeListener);

        // 到件
        mLlArrivePackage = DataCollectActivity.this.findViewById(R.id.ll_arrive_package);
        mBtnArrivePackage = DataCollectActivity.this.findViewById(R.id.btn_arrive_package);
        mBtnArrivePackage.setOnClickListener(this);
        mBtnArrivePackage.setOnFocusChangeListener(mLlFocusChangeListener);

        // 发件
        mLlSendPackage = DataCollectActivity.this.findViewById(R.id.ll_send_package);
        mBtnSendPackage = DataCollectActivity.this.findViewById(R.id.btn_send_package);
        mBtnSendPackage.setOnClickListener(this);
        mBtnSendPackage.setOnFocusChangeListener(mLlFocusChangeListener);

        // 留仓
        mLlLeavePackage = DataCollectActivity.this.findViewById(R.id.ll_leave_package);
        mBtnLeavePackage = DataCollectActivity.this.findViewById(R.id.btn_leave_package);
        mBtnLeavePackage.setOnClickListener(this);
        mBtnLeavePackage.setOnFocusChangeListener(mLlFocusChangeListener);
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

    @Override
    protected void onDestroy() {
        AppUtil.setScreenBright(false);
        super.onDestroy();

        // ScanHelper.getInstance().Close_Barcode();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 装车发件
            case R.id.btn_load_send: {
                gotoZhuangche();
                break;
            }

            // 卸车到件
            case R.id.btn_unload_receive_package: {
                gotoUpload();
                break;
            }

            // 发件
            case R.id.btn_send_package: {
                fajian();
                break;
            }

            // 到件
            case R.id.btn_arrive_package: {
                daojian();
                break;
            }

            // 留仓件
            case R.id.btn_leave_package: {
                liucang();

                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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

    @PermissionGrant(REQUEST_CAMARA_CODE)
    public void requestCameraSuccess() {

    }

    @PermissionDenied(REQUEST_CAMARA_CODE)
    public void requestCameraFail() {
        PermissionSettingManager.showPermissionSetting(false, this, getString(R.string
                .tip_camare_permission), getString(R.string.tip_permission_setting));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 卸车到件
     */
    private void gotoUpload() {
        Intent intent = new Intent(this, UnloadCargoArrivalActivity.class);
        startActivity(intent);
    }

    /**
     * 装车发件
     */
    private void gotoZhuangche() {
        Intent intent = new Intent(this, ZhuangcheActivity.class);
        startActivity(intent);
    }

    /**
     * 发件
     */
    private void fajian() {
        Intent intent = new Intent(this, FajianActivity.class);
        startActivity(intent);
    }

    /**
     * 到件
     */
    private void daojian() {
        // TODO 功能测试
        Intent intent = new Intent(this, DaojianActivity.class);
        startActivity(intent);
    }

    /**
     * 留仓
     */
    private void liucang() {
        Intent intent = new Intent(this, LiucangActivity.class);
        startActivity(intent);
    }

    /**
     * 设置预付款
     *
     * @param saleId
     * @param account
     * @param pwd
     */
    private void IfPrePay(final String saleId, final String account, final String pwd) {
        mLoginUrl = SharedUtil.getServletAddresFromSP(BaqiangApplication.getContext(),
                NetworkConstant.PREPAY_STATE);
        LogUtil.trace("path:" + mLoginUrl);

        RequestParams params = new RequestParams(mLoginUrl);
        params.addQueryStringParameter("saleId", saleId);
        params.addQueryStringParameter("userName", account);
        params.addQueryStringParameter("password", pwd);
        LogUtil.e(TAG, "saleId:" + saleId + "; userName:" + account + "; " + "pwd:" + pwd);

        // TODO 从日志看出，下述回调都是在MainThread运行的
        final Callback.Cancelable post = x.http().post(params, new Callback
                .CommonCallback<String>() {

            @Override
            public void onSuccess(String s) {
                LogUtil.trace("return s:" + s);

                Gson gson = new Gson();
                LoginResponse loginResponse = gson.fromJson(s, LoginResponse.class);
                if (loginResponse != null) {
                    if ("1".equals(loginResponse.getAuthRet())) {
                        gotoUpload();
                    } else {
                        Toast.makeText(BaqiangApplication.getContext(), "预付款不足", Toast
                                .LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BaqiangApplication.getContext(), "预付款不足", Toast.LENGTH_SHORT)
                            .show();
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
}
