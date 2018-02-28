package com.jiebao.baqiang.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

/**
 * Created by yaya on 2018/2/26.
 */

public class ServerConfigActivity extends BaseActivity implements View
        .OnClickListener {
    private static final String TAG = ServerConfigActivity.class
            .getSimpleName();

    private EditText mEdtExpressAddress;
    private EditText mEdtDataServerAddress;
    private EditText mEdtDataServerPort;
    private EditText mEdtApkUpdateAddress;
    private EditText mEdtApkUpdatePort;
    private Button mBtnSure;
    private Button mBtnCancel;

    @Override
    public void initView() {
        // TODO Activity的头部视图名称
        setHeaderCenterViewText("服务器设置");

        // TODO Activity的底部视图，底部设置包含两个Button
        LinearLayout footerLayout = (LinearLayout) View.inflate(this, R
                .layout.bottom_button, null);
        setFootLayout(footerLayout);

        // TODO Activity的中部视图，主界面
        setContent(R.layout.activity_server_config);
    }

    @Override
    public void initData() {
        // 快件查询地址：正式服务器地址
        mEdtExpressAddress = findViewById(R.id.edt_express_address);
        mEdtExpressAddress.setText(SharedUtil.getString(ServerConfigActivity
                        .this,
                Constant.PREFERENCE_KEY_EXPRESS_QUERY_ADDRESS));

        // 数据服务器地址：正式数据库服务器地址
        mEdtDataServerAddress = findViewById(R.id.data_server_address);
        mEdtDataServerAddress.setText(SharedUtil.getString
                (ServerConfigActivity.this, Constant
                        .PREFERENCE_KEY_DATA_SERVER_ADDRESS));

        // 数据服务器端口：正式数据库服务器访问端口
        mEdtDataServerPort = findViewById(R.id.data_server_port);
        mEdtDataServerPort.setText(SharedUtil.getString
                (ServerConfigActivity.this, Constant
                        .PREFERENCE_KEY_DATA_SERVER_PORT));

        // apk升级地址：捷宝服务器地址
        mEdtApkUpdateAddress = findViewById(R.id.apk_update_address);
        mEdtApkUpdateAddress.setText(SharedUtil.getString
                (ServerConfigActivity.this,
                        Constant.PREFERENCE_KEY_JB_SERVER));

        // apk升级端口：捷宝服务器访问端口
        mEdtApkUpdatePort = findViewById(R.id.apk_update_port);
        mEdtApkUpdatePort.setText(SharedUtil.getString(ServerConfigActivity
                .this, Constant.PREFERENCE_KEY_JB_SERVER_PORT));

        mBtnSure = ServerConfigActivity.this.findViewById(R.id.make_sure);

        mBtnCancel = ServerConfigActivity.this.findViewById(R.id.cancel);

        initListener();
    }

    private void initListener() {
        mEdtExpressAddress.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
                LogUtil.trace("beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                LogUtil.trace("onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                LogUtil.trace("afterTextChanged");
            }
        });

        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.make_sure: {
                // 保存快件查询地址
                String expressAddress = mEdtExpressAddress.getText().toString();
                SharedUtil.putString(ServerConfigActivity.this,
                        Constant.PREFERENCE_KEY_EXPRESS_QUERY_ADDRESS,
                        expressAddress);

                // 保存数据服务器地址：
                String dataServerAddress = mEdtDataServerAddress.getText()
                        .toString();
                SharedUtil.putString(ServerConfigActivity.this,
                        Constant.PREFERENCE_KEY_DATA_SERVER_ADDRESS,
                        dataServerAddress);

                // 保存数据服务器端口
                String dataServerPort = mEdtDataServerPort.getText().toString();
                SharedUtil.putString(ServerConfigActivity.this,
                        Constant.PREFERENCE_KEY_DATA_SERVER_PORT,
                        dataServerPort);

                // 保存捷宝服务器地址
                String jbServerAddress = mEdtApkUpdateAddress.getText()
                        .toString();
                SharedUtil.putString(ServerConfigActivity.this,
                        Constant.PREFERENCE_KEY_JB_SERVER, jbServerAddress);

                // 保存捷宝服务器端口
                String jbServerPort = mEdtApkUpdatePort.getText().toString();
                SharedUtil.putString(ServerConfigActivity.this,
                        Constant.PREFERENCE_KEY_JB_SERVER_PORT, jbServerPort);

                Toast.makeText(ServerConfigActivity.this, "保存成功", Toast
                        .LENGTH_SHORT).show();
                ServerConfigActivity.this.finish();
                break;
            }

            case R.id.cancel: {
                ServerConfigActivity.this.finish();

                LogUtil.d(TAG, "-->" + SharedUtil.getString
                        (ServerConfigActivity.this, "data_server_address"));

                break;
            }
        }
    }
}
