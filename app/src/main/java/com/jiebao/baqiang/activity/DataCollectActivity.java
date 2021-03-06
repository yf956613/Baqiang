package com.jiebao.baqiang.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jiebao.baqiang.R;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.LoginResponse;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
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
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 数据采集界面
 */
public class DataCollectActivity extends BaseActivityWithTitleAndNumber
        implements View
        .OnClickListener {
    private static final String TAG = "DataCollectActivity";

    private String mLoginUrl = "";

    private boolean isDBBaseExist = false;


    // 装车发件
    @ViewInject(R.id.ll_load_send)
    private LinearLayout mLlLoadSend;
    @ViewInject(R.id.btn_load_send)
    private Button mBtnLoadSend;

    // 卸车到件
    @ViewInject(R.id.ll_unload_receive_package)
    private LinearLayout mLlUnloadReceivePackage;
    @ViewInject(R.id.btn_unload_receive_package)
    private Button mBtnUnloadReceivePackage;

    // 到件
    @ViewInject(R.id.ll_arrive_package)
    private LinearLayout mLlArrivePackage;
    @ViewInject(R.id.btn_arrive_package)
    private Button mBtnArrivePackage;

    // 发件
    @ViewInject(R.id.ll_send_package)
    private LinearLayout mLlSendPackage;
    @ViewInject(R.id.btn_send_package)
    private Button mBtnSendPackage;

    // 称重发件
    @ViewInject(R.id.ll_weigh_send_package)
    private LinearLayout mLlWeighSendPackage;
    @ViewInject(R.id.btn_weigh_send_package)
    private Button mBtnWeighSendPackage;

    // 派件
    @ViewInject(R.id.ll_delivery_package)
    private LinearLayout mLlDeliveryPackage;
    @ViewInject(R.id.btn_delivery_package)
    private Button mBtnDeliveryPackage;

    // 签收
    @ViewInject(R.id.ll_sign_for)
    private LinearLayout mLlSignfor;
    @ViewInject(R.id.btn_sign_for)
    private Button mBtnSignfor;

    // 未签收
    @ViewInject(R.id.ll_un_sign_for)
    private LinearLayout mLlUnSignfor;
    @ViewInject(R.id.btn_un_sign_for)
    private Button mBtnUnSignfor;

    // 快速签收
    @ViewInject(R.id.ll_fast_sign_for)
    private LinearLayout mLlFastSignfor;
    @ViewInject(R.id.btn_fast_sign_for)
    private Button mBtnFastSignfor;

    // 回单到件
    @ViewInject(R.id.ll_replay_for_arrive_package)
    private LinearLayout mLlReplayForArrivePackage;
    @ViewInject(R.id.btn_replay_for_arrive_package)
    private Button mBtnReplayForArrivePackage;

    // 回单发件
    @ViewInject(R.id.ll_replay_for_send_package)
    private LinearLayout mLlReplayForSendPackage;
    @ViewInject(R.id.btn_replay_for_send_package)
    private Button mBtnReplayForSendPackage;

    // 留仓件
    @ViewInject(R.id.ll_leave_package)
    private LinearLayout mLlLeavePackage;
    @ViewInject(R.id.btn_leave_package)
    private Button mBtnLeavePackage;

    // 装袋
    @ViewInject(R.id.ll_bagging)
    private LinearLayout mLlBagging;
    @ViewInject(R.id.btn_bagging)
    private Button mBtnBagging;

    // 拆袋
    @ViewInject(R.id.ll_un_bagging)
    private LinearLayout mLlUnBagging;
    @ViewInject(R.id.btn_un_bagging)
    private Button mBtnUnBagging;

    // 托盘绑定
    @ViewInject(R.id.ll_tray_binding)
    private LinearLayout mLlTrayBinding;
    @ViewInject(R.id.btn_tray_binding)
    private Button mBtnTrayBinding;

    // 托盘拆分
    @ViewInject(R.id.ll_tray_un_bagging)
    private LinearLayout mLlTrayUnBinding;
    @ViewInject(R.id.btn_tray_un_bagging)
    private Button mBtnTrayUnBinding;

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
                    setLinearLayoutBackground(mLlUnloadReceivePackage,
                            hasFocus);
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

                case R.id.btn_weigh_send_package: {
                    setLinearLayoutBackground(mLlWeighSendPackage, hasFocus);
                    break;
                }

                case R.id.btn_delivery_package: {
                    setLinearLayoutBackground(mLlDeliveryPackage, hasFocus);
                    break;
                }

                case R.id.btn_sign_for: {
                    setLinearLayoutBackground(mLlSignfor, hasFocus);
                    break;
                }

                case R.id.btn_un_sign_for: {
                    setLinearLayoutBackground(mLlUnSignfor, hasFocus);
                    break;
                }

                case R.id.btn_fast_sign_for: {
                    setLinearLayoutBackground(mLlFastSignfor, hasFocus);
                    break;
                }

                case R.id.btn_replay_for_arrive_package: {
                    setLinearLayoutBackground(mLlReplayForArrivePackage,
                            hasFocus);
                    break;
                }

                case R.id.btn_replay_for_send_package: {
                    setLinearLayoutBackground(mLlReplayForSendPackage,
                            hasFocus);
                    break;
                }

                case R.id.btn_leave_package: {
                    setLinearLayoutBackground(mLlLeavePackage, hasFocus);
                    break;
                }

                case R.id.btn_bagging: {
                    setLinearLayoutBackground(mLlBagging, hasFocus);
                    break;
                }

                case R.id.btn_un_bagging: {
                    setLinearLayoutBackground(mLlUnBagging, hasFocus);
                    break;
                }

                case R.id.btn_tray_binding: {
                    setLinearLayoutBackground(mLlTrayBinding, hasFocus);
                    break;
                }

                case R.id.btn_tray_un_bagging: {
                    setLinearLayoutBackground(mLlTrayUnBinding, hasFocus);
                    break;
                }
            }
        }
    };

    @Override
    public void initView() {
        setContent(R.layout.activity_data_collect);
        setHeaderLeftViewText("采集功能项");
        x.view().inject(DataCollectActivity.this);

        if (Build.VERSION.SDK_INT >= 23 && BaqiangApplication
                .isSoftDecodeScan) {
            MPermissions.requestPermissions(this, REQUEST_CAMARA_CODE,
                    Manifest.permission.CAMERA);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBtnLoadSend.setFocusable(true);
        mBtnLoadSend.setFocusableInTouchMode(true);
        mBtnLoadSend.requestFocus();
        mBtnLoadSend.requestFocusFromTouch();
    }

    @Override
    public void initData() {
        // 装车发件
        mBtnLoadSend.setOnClickListener(this);
        mBtnLoadSend.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnLoadSend.setFocusable(true);
        mBtnLoadSend.setFocusableInTouchMode(true);
        mBtnLoadSend.requestFocus();
        mBtnLoadSend.requestFocusFromTouch();

        // 卸车到件
        mBtnUnloadReceivePackage.setOnClickListener(this);
        mBtnUnloadReceivePackage.setOnFocusChangeListener
                (mLlFocusChangeListener);

        // 到件
        mBtnArrivePackage.setOnClickListener(this);
        mBtnArrivePackage.setOnFocusChangeListener(mLlFocusChangeListener);

        // 发件
        mBtnSendPackage.setOnClickListener(this);
        mBtnSendPackage.setOnFocusChangeListener(mLlFocusChangeListener);

        // 称重发件
        mBtnWeighSendPackage.setOnClickListener(this);
        mBtnWeighSendPackage.setOnFocusChangeListener(mLlFocusChangeListener);

        // 派件
        mBtnDeliveryPackage.setOnClickListener(this);
        mBtnDeliveryPackage.setOnFocusChangeListener(mLlFocusChangeListener);

        // 签收
        mBtnSignfor.setOnClickListener(this);
        mBtnSignfor.setOnFocusChangeListener(mLlFocusChangeListener);

        // 未签收
        mBtnUnSignfor.setOnClickListener(this);
        mBtnUnSignfor.setOnFocusChangeListener(mLlFocusChangeListener);

        // 快速签收
        mBtnFastSignfor.setOnClickListener(this);
        mBtnFastSignfor.setOnFocusChangeListener(mLlFocusChangeListener);

        // 回单到件
        mBtnReplayForArrivePackage.setOnClickListener(this);
        mBtnReplayForArrivePackage.setOnFocusChangeListener
                (mLlFocusChangeListener);

        // 回单发件
        mBtnReplayForSendPackage.setOnClickListener(this);
        mBtnReplayForSendPackage.setOnFocusChangeListener
                (mLlFocusChangeListener);

        // 留仓
        mBtnLeavePackage.setOnClickListener(this);
        mBtnLeavePackage.setOnFocusChangeListener(mLlFocusChangeListener);

        // 装袋
        mBtnBagging.setOnClickListener(this);
        mBtnBagging.setOnFocusChangeListener(mLlFocusChangeListener);

        // 拆袋
        mBtnUnBagging.setOnClickListener(this);
        mBtnUnBagging.setOnFocusChangeListener(mLlFocusChangeListener);

        // 托盘绑定
        mBtnTrayBinding.setOnClickListener(this);
        mBtnTrayBinding.setOnFocusChangeListener(mLlFocusChangeListener);

        // 托盘拆分
        mBtnTrayUnBinding.setOnClickListener(this);
        mBtnTrayUnBinding.setOnFocusChangeListener(mLlFocusChangeListener);

        isDBBaseExist = BQDataBaseHelper.checkBaseDB();
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
        if (!isDBBaseExist) {
            Toast.makeText(DataCollectActivity.this, "资料信息数据库异常，请重新下载数据资料",
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        switch (view.getId()) {
            // 装车发件
            case R.id.btn_load_send: {
                // gotoZhuangche();
                break;
            }

            // 卸车到件
            case R.id.btn_unload_receive_package: {
                // gotoUpload();
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

            case R.id.btn_tray_un_bagging: {
                gotoTestMode();
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
                break;
        }
    }

    @PermissionGrant(REQUEST_CAMARA_CODE)
    public void requestCameraSuccess() {

    }

    @PermissionDenied(REQUEST_CAMARA_CODE)
    public void requestCameraFail() {
        PermissionSettingManager.showPermissionSetting(false, this, getString
                (R.string
                        .tip_camare_permission), getString(R.string
                .tip_permission_setting));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions,
                                           @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode,
                permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
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
        AlertDialog.Builder builder = new AlertDialog.Builder
                (DataCollectActivity.this);
        View view = LayoutInflater.from(DataCollectActivity.this).inflate(R
                .layout
                .alert_dialog_daojian_activity, null);
        builder.setView(view);
        final AlertDialog dialog = builder.show();

        Button btnPositive = view.findViewById(R.id
                .btn_activity_fast);
        Button btnNegative = view.findViewById(R.id
                .btn_activity_common);
        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(DataCollectActivity.this,
                        FastDaojianActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(DataCollectActivity.this,
                        DaojianActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
    }

    /**
     * 留仓
     */
    private void liucang() {
        Intent intent = new Intent(this, LiucangActivity.class);
        startActivity(intent);
    }

    private void gotoTestMode() {
        LogUtil.trace("进入测试模式");

        AlertDialog.Builder builder = new AlertDialog.Builder
                (DataCollectActivity.this);
        View view = LayoutInflater.from(DataCollectActivity.this).inflate(R
                .layout.test_mode_alert_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.show();

        Button btnPositive = view.findViewById(R.id.btn_add);
        Button btnNegative = view.findViewById(R.id.btn_cancel);
        final EditText etContent = view.findViewById(R.id.et_content);

        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String str = etContent.getText().toString();
                if (str == null || "".equals(str) || "".equals(str.trim())) {
                    etContent.setError("密码不能为空");
                } else {
                    if ("888888".equals(str)) {
                        Intent intent = new Intent(DataCollectActivity.this,
                                TestModeActivity.class);
                        DataCollectActivity.this.startActivity(intent);
                    } else {
                        // 密码错误
                        Toast.makeText(DataCollectActivity.this, "密码错误", Toast
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
     * 设置预付款
     *
     * @param saleId
     * @param account
     * @param pwd
     */
    private void IfPrePay(final String saleId, final String account, final
    String pwd) {
        mLoginUrl = SharedUtil.getServletAddresFromSP(BaqiangApplication
                        .getContext(),
                NetworkConstant.PREPAY_STATE);
        LogUtil.trace("path:" + mLoginUrl);

        RequestParams params = new RequestParams(mLoginUrl);
        params.addQueryStringParameter("saleId", saleId);
        params.addQueryStringParameter("userName", account);
        params.addQueryStringParameter("password", pwd);
        LogUtil.e(TAG, "saleId:" + saleId + "; userName:" + account + "; " +
                "pwd:" + pwd);

        final Callback.Cancelable post = x.http().post(params, new Callback
                .CommonCallback<String>() {

            @Override
            public void onSuccess(String s) {
                LogUtil.trace("return s:" + s);

                Gson gson = new Gson();
                LoginResponse loginResponse = gson.fromJson(s, LoginResponse
                        .class);
                if (loginResponse != null) {
                    if ("1".equals(loginResponse.getAuthRet())) {
                        gotoUpload();
                    } else {
                        Toast.makeText(BaqiangApplication.getContext(),
                                "预付款不足", Toast
                                        .LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BaqiangApplication.getContext(), "预付款不足",
                            Toast.LENGTH_SHORT)
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
