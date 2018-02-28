package com.jiebao.baqiang.activity;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
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
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.LoginResponse;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.service.DataSyncService;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class LoginActivity extends BaseActivity implements View
        .OnClickListener, CompoundButton
        .OnCheckedChangeListener, DataSyncService.DataSyncNotifity {
    private static final String TAG = "LoginActivity";

    private EditText mEtUserName;
    private EditText mEtPassward;

    private Button mBtnLogin;
    private Button mBtnConfigurate;

    private CheckBox mCbRememberPassword;
    private LinearLayout mLLRemember;

    private String mLoginUrl = "";

    private DataSyncService dataSyncService;

    @Override
    public void initView() {
        LogUtil.trace();

        // TODO 登录界面底部 View
        /*LinearLayout footerLayout = (LinearLayout) View.inflate(this, R.layout
                .main_footer_layout, null);
        setFootLayout(footerLayout);*/

        setContent(R.layout.activity_login);
        verifyStoragePermissions(LoginActivity.this);
    }

    @Override
    public void initData() {
        LogUtil.trace();

        mEtUserName = LoginActivity.this.findViewById(R.id.et_user_name);
        mEtPassward = LoginActivity.this.findViewById(R.id.et_passward);

        mBtnLogin = findViewById(R.id.btn_login);
        mBtnConfigurate = LoginActivity.this.findViewById(R.id
                .btn_wifi_setttings);

        mCbRememberPassword = LoginActivity.this.findViewById(R.id
                .cv_psw_remember);
        mLLRemember = LoginActivity.this.findViewById(R.id.remember_layout);

        mBtnLogin.setOnClickListener(this);
        mBtnConfigurate.setOnClickListener(this);
        mCbRememberPassword.setOnCheckedChangeListener(this);
        mLLRemember.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isRememberPsw = SharedUtil.getBoolean(this, Constant
                .KEY_IS_REMEMBER_PSW);
        LogUtil.e(TAG, "initData:" + isRememberPsw);
        if (isRememberPsw) {
            // 之前用户选择保存数据，回显数据
            mCbRememberPassword.setChecked(isRememberPsw);

            String userName = SharedUtil.getString(this, Constant
                    .PREFERENCE_KEY_USERNAME);
            String psw = SharedUtil.getString(this, Constant
                    .PREFERENCE_KEY_PSW);

            // 显示数据
            mEtUserName.setText(userName);
            mEtPassward.setText(psw);

            LogUtil.d(TAG, "userName:" + userName + "; psw:" + psw);
        } else {
            // 手动清除EditText内容
            mEtUserName.setText("");
            mEtPassward.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login: {
                closeSoftKeyBoard();
                showLoadinDialog();

                // TODO 测试阶段
                /*String salesService = "贵州毕节";
                String userName = "贵州毕节";
                String psw = "123456789";*/

                String userName = mEtUserName.getText().toString();
                String psw = mEtPassward.getText().toString();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(psw)) {
                    Toast.makeText(LoginActivity.this, "用户名或密码为空，请重新输入",
                            Toast.LENGTH_SHORT).show();
                } else {
                    saveConfigurateLogin(userName, psw);
                    login(userName, psw);
                }
                break;
            }

            case R.id.remember_layout:
                mCbRememberPassword.setChecked(!mCbRememberPassword.isChecked
                        ());
                break;

            case R.id.btn_wifi_setttings: {
                Intent intent = new Intent();
                intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                startActivity(intent);

                break;
            }
        }
    }

    /**
     * 判断IP地址和端口是否设置
     *
     * @return true 表示可供使用；false 表示不能使用
     */
    private boolean isCheckNetworkAddressAccess() {
        SharedPreferences sp = BaqiangApplication
                .getContext().getSharedPreferences("ServerInfo", Context
                        .MODE_PRIVATE);
        if (sp != null) {
            String ip = sp.getString("Ip", "");
            String port = sp.getString("Port", "");
            LogUtil.trace("ip:" + ip + "; port:" + port);

            if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
                LogUtil.d(TAG, "set ip or port is null...");
                return false;
            } else if (!TextStringUtil.isIpAddressAvailable(ip) ||
                    !TextStringUtil.isPortCheckAvailable(port)) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LogUtil.trace("isChecked:" + isChecked);

        switch (buttonView.getId()) {
            case R.id.cv_psw_remember:
                // 保存是否记住用户名和密码标识
                SharedUtil.putBoolean(this, Constant.KEY_IS_REMEMBER_PSW,
                        isChecked);
                if (!isChecked) {
                    // clearConfigurateLogin();
                }
                break;
        }
    }

    public void login(final String account, final String
            pwd) {
        // TODO 管理员账号：000000 123695
        if ("000000".equals(account) && "123695".equals(pwd)) {
            LogUtil.trace("goto Administrator activity.");

            Intent intent = new Intent(LoginActivity.this,
                    AdministratorSettingActivity.class);
            LoginActivity.this.startActivity(intent);

            closeLoadinDialog();
            return;
        }

        mLoginUrl = SharedUtil.getServletAddresFromSP(BaqiangApplication
                        .getContext(),
                NetworkConstant.LOGIN_SERVLET);
        LogUtil.trace("path:" + mLoginUrl);
        if (TextUtils.isEmpty(mLoginUrl)) {
            Toast.makeText(LoginActivity.this, "数据服务器地址或端口出错", Toast
                    .LENGTH_SHORT).show();
            return;
        }

        RequestParams params = new RequestParams(mLoginUrl);
        params.addQueryStringParameter("saleId", SharedUtil.getString
                (LoginActivity.this, Constant.PREFERENCE_KEY_SALE_SERVICE));
        params.addQueryStringParameter("userName", SharedUtil.getString
                (LoginActivity.this, Constant.PREFERENCE_KEY_SALE_SERVICE) +
                SharedUtil.getString(LoginActivity.this, Constant
                        .PREFERENCE_KEY_USERNAME));
        params.addQueryStringParameter("password", SharedUtil.getString
                (LoginActivity.this, Constant.PREFERENCE_KEY_PSW));

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
                        Toast.makeText(BaqiangApplication.getContext(),
                                "登录成功", Toast
                                        .LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,
                                MainActivity.class));

                        startDataSync();
                    } else {
                        Toast.makeText(BaqiangApplication.getContext(),
                                "用户名或密码错误，请重新登录", Toast
                                        .LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BaqiangApplication.getContext(),
                            "服务器数据解析错误", Toast
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

//                    startActivity(new Intent(LoginActivity.this,
//                            DataCollectActivity.class));
                    /*startDataSync();*/

                closeLoadinDialog();
            }
        });

    }

    private void startDataSync() {
        startService(new Intent(getApplicationContext(), DataSyncService
                .class));
        bindService(new Intent(LoginActivity.this, DataSyncService.class),
                mServiceConnection,
                Service.BIND_AUTO_CREATE);
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.trace();

            DataSyncService.MyBinder myBinder = (DataSyncService.MyBinder)
                    service;
            dataSyncService = myBinder.getService();
            dataSyncService.setDataSyncNotifity(LoginActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.trace();
        }
    };

    public void onSyncFinished(Exception e) {
        showMainActivity();
    }

    protected void showMainActivity() {
        closeLoadinDialog();
        startActivity(new Intent(LoginActivity.this, DataCollectActivity.class));
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

    /**
     * 保存用户名和密码
     *
     * @param userName
     * @param password
     */
    private void saveConfigurateLogin(String userName,
                                      String password) {
        // 保存是否记住用户名和密码标识
        if (mCbRememberPassword.isChecked()) {
            SharedUtil.putBoolean(this, Constant.KEY_IS_REMEMBER_PSW,
                    true);
        } else {
            SharedUtil.putBoolean(this, Constant.KEY_IS_REMEMBER_PSW,
                    false);
        }
        SharedUtil.putString(LoginActivity.this, Constant
                .PREFERENCE_KEY_USERNAME, userName);
        SharedUtil.putString(LoginActivity.this, Constant.PREFERENCE_KEY_PSW,
                password);
    }

    private void clearConfigurateLogin() {
        SharedUtil.putString(LoginActivity.this, Constant
                .PREFERENCE_KEY_SALE_SERVICE, "");
        SharedUtil.putString(LoginActivity.this, Constant
                .PREFERENCE_KEY_USERNAME, "");
        SharedUtil.putString(LoginActivity.this, Constant.PREFERENCE_KEY_PSW,
                "");
    }

    // TODO 申请读写权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity,
                        PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLoginFailed(String msg) {
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}