package com.jiebao.baqiang.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jiebao.baqiang.R;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.LoginResponse;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.util.AppUtil;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class LoginActivity extends BaseActivityWithTitleAndNumber implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    // TODO android:singleLine="true" 设置Enter按键动作
    @ViewInject(R.id.et_user_name)
    private EditText mEtUserName;
    @ViewInject(R.id.et_passward)
    private EditText mEtPassward;
    @ViewInject(R.id.btn_login)
    private Button mBtnLogin;
    @ViewInject(R.id.btn_wifi_setttings)
    private Button mBtnConfigurate;

    private String mLoginUrl = "";

    private View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.btn_login: {
                    setBtnBackground(mBtnLogin, hasFocus);
                    break;
                }

                case R.id.btn_wifi_setttings: {
                    setBtnBackground(mBtnConfigurate, hasFocus);
                    break;
                }
            }
        }
    };

    @Override
    public void initView() {
        LogUtil.trace();

        // TODO 登录界面底部 View
        /*LinearLayout footerLayout = (LinearLayout) View.inflate(this, R.layout
                .main_footer_layout, null);
        setFootLayout(footerLayout);*/

        setContent(R.layout.activity_login);
        x.view().inject(LoginActivity.this);
    }

    @Override
    public void initData() {
        LogUtil.trace();

        // TODO 申请权限及屏蔽相关操作
        verifyStoragePermissions(LoginActivity.this);
        sendBroadcastForAction();

        initListener();
    }

    private void initListener() {
        mBtnLogin.setOnClickListener(this);
        mBtnLogin.setOnFocusChangeListener(mFocusChangeListener);

        mBtnConfigurate.setOnClickListener(this);
        mBtnConfigurate.setOnFocusChangeListener(mFocusChangeListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login: {
                closeSoftKeyBoard();

                String userName = mEtUserName.getText().toString();
                String psw = mEtPassward.getText().toString();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(psw)) {
                    Toast.makeText(LoginActivity.this, "用户名或密码为空，请重新输入", Toast.LENGTH_SHORT).show();
                } else {
                    login(userName, psw);
                }

                break;
            }

            case R.id.btn_wifi_setttings: {
                Intent intent = new Intent();
                intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                startActivity(intent);

                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.trace("keyCode--->" + keyCode);

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                LogUtil.d(TAG, "---->按下了Back按键");
                // 消费Back事件
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void login(final String account, final String pwd) {
        // 退出app账户设置
        if ("888888".equals(account) && "159357".equals(pwd)) {
            LogUtil.trace("goto Launcher...");

            // 应用中退回到Launcher界面
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            LoginActivity.this.startActivity(intent);

            return;
        }

        // TODO 网络不可用，执行所有登录前先连接网络
        if (!AppUtil.IsNetworkAvailable()) {
            Toast.makeText(LoginActivity.this, "当前网络不可用，请先连接网络", Toast.LENGTH_SHORT).show();

            return;
        }

        showLoadinDialog();

        // TODO 管理员账号：000000 123695
        if ("000000".equals(account) && "123695".equals(pwd)) {
            LogUtil.trace("goto Administrator activity.");

            Intent intent = new Intent(LoginActivity.this, AdministratorSettingActivity.class);
            LoginActivity.this.startActivity(intent);

            closeLoadinDialog();
            return;
        }

        // 验证IP和端口
        if (!isCheckNetworkAddressAccess()) {
            Toast.makeText(LoginActivity.this, "数据服务器地址和端口未设置", Toast.LENGTH_SHORT).show();

            closeLoadinDialog();
            return;
        }

        mLoginUrl = SharedUtil.getServletAddresFromSP(BaqiangApplication.getContext(),
                NetworkConstant.LOGIN_SERVLET);
        LogUtil.trace("path:" + mLoginUrl);
        if (TextUtils.isEmpty(mLoginUrl)) {
            Toast.makeText(LoginActivity.this, "数据服务器地址或端口出错", Toast.LENGTH_SHORT).show();

            closeLoadinDialog();
            return;
        }

        RequestParams params = new RequestParams(mLoginUrl);
        params.addQueryStringParameter("saleId", SharedUtil.getString(LoginActivity.this,
                Constant.PREFERENCE_KEY_SALE_SERVICE));
        params.addQueryStringParameter("userName", SharedUtil.getString(LoginActivity.this,
                Constant.PREFERENCE_KEY_SALE_SERVICE) + SharedUtil.getString(LoginActivity.this,
                Constant.PREFERENCE_KEY_USERNAME));
        params.addQueryStringParameter("password", SharedUtil.getString(LoginActivity.this,
                Constant.PREFERENCE_KEY_PSW));

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
                        Toast.makeText(BaqiangApplication.getContext(), "登录成功", Toast
                                .LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(BaqiangApplication.getContext(), "用户名或密码错误，请重新登录", Toast
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
                Toast.makeText(LoginActivity.this, "服务器响应失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException e) {
                LogUtil.trace();
            }

            @Override
            public void onFinished() {
                if(Constant.DEBUG){
                    // TODO 测试阶段，正式环境删除
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }

                closeLoadinDialog();
            }
        });
    }

    /**
     * 判断IP地址和端口是否设置
     *
     * @return true 表示可供使用；false 表示不能使用
     */
    private boolean isCheckNetworkAddressAccess() {
        String dataServerAddress = SharedUtil.getString(LoginActivity.this, Constant
                .PREFERENCE_KEY_DATA_SERVER_ADDRESS);
        String dataServerPort = SharedUtil.getString(LoginActivity.this, Constant
                .PREFERENCE_KEY_DATA_SERVER_PORT);

        if (TextUtils.isEmpty(dataServerAddress) || TextUtils.isEmpty(dataServerPort)) {
            return false;
        }

        return true;
    }

    private static final String PERSIST_SETTINGS = "com.jiebao.persist.set";
    private static final String PERSIST_SETTINGS_KEY = "persistKey";
    private static final String PERSIST_SETTINGS_VALUE = "persistValue";
    private static final String KEY_HOME = "persist.sys.key_home";
    private static final String HIDE_STATUSBAR = "persist.sys.hide_statusbar";

    /**
     * 发送广播给Settings，做屏蔽HOME和下拉状态栏
     */
    private void sendBroadcastForAction() {
        Intent intent = new Intent();
        intent.setAction(PERSIST_SETTINGS);
        // TODO 屏蔽HOME按键
        intent.putExtra(PERSIST_SETTINGS_KEY, KEY_HOME);
        intent.putExtra(PERSIST_SETTINGS_VALUE, "false");

        // TODO 屏蔽状态栏下拉，值为true隐藏
        intent.putExtra(PERSIST_SETTINGS_KEY, HIDE_STATUSBAR);
        intent.putExtra(PERSIST_SETTINGS_VALUE, "true");

        LoginActivity.this.sendBroadcast(intent);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {"android.permission" + "" + "" + "" + "" + ""
            + ".READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    /**
     * App申请系统相关权限
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity, "android.permission" +
                    ".WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据是否获取焦点，改变View的背景
     *
     * @param v       当前处理的View
     * @param isFocus 是否获取了焦点
     */
    private void setBtnBackground(View v, boolean isFocus) {
        if (isFocus) {
            // v.setBackgroundResource(R.drawable.btn_login_bg_pressed);
            v.setBackgroundColor(this.getResources().getColor(R.color.colorPrimaryDark));
        } else {
            v.setBackgroundColor(this.getResources().getColor(R.color.status_view));
        }
    }

    /**
     * 关闭软键盘
     */
    private void closeSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context
                .INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            if (getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}