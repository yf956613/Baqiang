package com.jiebao.baqiang.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jiebao.baqiang.R;
import com.jiebao.baqiang.SetServerInfoActivity;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.LoginResponse;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.service.DataSyncService;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * @author dengyuanming
 * @ClassName: LoginActivity
 * @Description:登陆activity
 * @date 2016年11月2日
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, CompoundButton
        .OnCheckedChangeListener, DataSyncService.DataSyncNotifity {

    private EditText mEtSalesService;
    private EditText et_user_name;
    private EditText et_passward;
    private Button btn_login;
    private Button mBtnConfigurate;
    private CheckBox cv_remember_psw;
    private LinearLayout remember_layout;
    private String mLoginUrl = "";
    private static final String TAG = "LoginActivity";
    private DataSyncService dataSyncService;

    @Override
    public void initView() {
        LogUtil.trace();

        LinearLayout footerLayout = (LinearLayout) View.inflate(this, R.layout
                .main_footer_layout, null);
        setFootLayout(footerLayout);

        setContent(R.layout.activity_login);
    }

    @Override
    public void initData() {
        LogUtil.trace();

        mEtSalesService = LoginActivity.this.findViewById(R.id.et_sale_service);
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_passward = (EditText) findViewById(R.id.et_passward);
        btn_login = (Button) findViewById(R.id.btn_login);
        mBtnConfigurate = LoginActivity.this.findViewById(R.id.btn_ip_configurate);
        cv_remember_psw = (CheckBox) findViewById(R.id.cv_psw_remember);
        remember_layout = (LinearLayout) findViewById(R.id.remember_layout);

        btn_login.setOnClickListener(this);
        mBtnConfigurate.setOnClickListener(this);
        cv_remember_psw.setOnCheckedChangeListener(this);
        remember_layout.setOnClickListener(this);

        boolean isRememberPsw = SharedUtil.getBoolean(this, Constant.KEY_IS_REMEMBER_PSW);
        LogUtil.d(TAG, "isRememberPsq:" + isRememberPsw);
        if (isRememberPsw) {
            cv_remember_psw.setChecked(isRememberPsw);
            String salesService = SharedUtil.getString(LoginActivity.this, Constant
                    .PREFERENCE_KEY_SALE_SERVICE);
            String userName = SharedUtil.getString(this, Constant.PREFERENCE_KEY_USERNAME);
            String psw = SharedUtil.getString(this, Constant.PREFERENCE_KEY_PSW);
            mEtSalesService.setText(salesService);
            et_user_name.setText(userName);
            et_passward.setText(psw);
            LogUtil.d(TAG, "salesService:" + salesService + "; userName:" + userName + "; psw:" +
                    psw);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login: {
                if (TextUtils.isEmpty(et_user_name.getText().toString())) {
                    //DialogUtil.showAlertDialog(this,getString(R.string.hint_user_name));
                    return;
                }
                if (TextUtils.isEmpty(et_passward.getText().toString())) {
                    // DialogUtil.showAlertDialog(this,getString(R.string.hint_passward));
                    return;
                }
                closeSoftKeyBoard();

                String salesService = mEtSalesService.getText().toString().trim();
                String userName = et_user_name.getText().toString().trim();
                String psw = et_passward.getText().toString().trim();

                login(salesService, userName, psw);
                setConfigurateLogin(salesService, userName, psw);

                break;
            }

            case R.id.remember_layout:
                cv_remember_psw.setChecked(!cv_remember_psw.isChecked());
                break;

            case R.id.btn_ip_configurate: {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, SetServerInfoActivity.class);
                startActivity(intent);

                break;
            }

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LogUtil.trace("isChecked:" + isChecked);

        switch (buttonView.getId()) {
            case R.id.cv_psw_remember:
                SharedUtil.putBoolean(this, Constant.KEY_IS_REMEMBER_PSW, isChecked);

                if (!isChecked) {
                    SharedUtil.putString(LoginActivity.this, Constant
                            .PREFERENCE_KEY_SALE_SERVICE, "");
                    SharedUtil.putString(LoginActivity.this, Constant.PREFERENCE_KEY_USERNAME, "");
                    SharedUtil.putString(LoginActivity.this, Constant.PREFERENCE_KEY_PSW, "");
                }
                break;
        }
    }

    public void login(final String saleId, final String account, final String pwd) {
        mLoginUrl = SharedUtil.getServletAddresFromSP(BaqiangApplication.getContext(),
                NetworkConstant.LOGIN_SERVLET);
        LogUtil.trace("path:" + mLoginUrl);

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd)) {
            //listener.loginFailed("账户名或者密码不能为空");
        } else {
            RequestParams params = new RequestParams(mLoginUrl);
            params.addQueryStringParameter("saleId", "捷宝");
            params.addQueryStringParameter("userName", "捷宝");
            params.addQueryStringParameter("password", "捷宝");
            LogUtil.d(TAG, "saleId:" + saleId + "; userName:" + account + "; pwd:" + pwd);

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
                            Toast.makeText(BaqiangApplication.getContext(), "登陆成功", Toast
                                    .LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BaqiangApplication.getContext(), "用户名或密码", Toast
                                    .LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(BaqiangApplication.getContext(), "服务器数据解析错误", Toast
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

                    /*showLoadinDialog();
                    startDataSync();*/
                }
            });
        }
    }

    private void startDataSync() {
        //启动数据同步
        startService(new Intent(getApplicationContext(), DataSyncService.class));
        bindService(new Intent(LoginActivity.this, DataSyncService.class), connection, Service
                .BIND_AUTO_CREATE);
    }

    /**
     * 连接数据同步服务
     */
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DataSyncService.MyBinder myBinder = (DataSyncService.MyBinder) service;
            dataSyncService = myBinder.getService();
            dataSyncService.setDataSyncNotifity(LoginActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void onSyncFinished(Exception e) {
        showMainActivity();
    }

    protected void showMainActivity() {
        closeLoadinDialog();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    public void closeSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context
                .INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            if (getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void setConfigurateLogin(String salesService, String userName, String password) {
        SharedUtil.putString(LoginActivity.this, Constant.PREFERENCE_KEY_SALE_SERVICE,
                salesService);
        SharedUtil.putString(LoginActivity.this, Constant.PREFERENCE_KEY_USERNAME, userName);
        SharedUtil.putString(LoginActivity.this, Constant.PREFERENCE_KEY_PSW, password);
    }

    private void clearConfigurateLogin() {
        SharedUtil.putString(LoginActivity.this, Constant.PREFERENCE_KEY_SALE_SERVICE, "");
        SharedUtil.putString(LoginActivity.this, Constant.PREFERENCE_KEY_USERNAME, "");
        SharedUtil.putString(LoginActivity.this, Constant.PREFERENCE_KEY_PSW, "");
    }

}