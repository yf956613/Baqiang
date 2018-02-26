package com.jiebao.baqiang.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.jiebao.baqiang.R;

/**
 * Created by yaya on 2018/2/26.
 */

public class ServerConfigActivity extends BaseActivity {
    @Override
    public void initView() {
        setHeaderCenterViewText("服务器设置");
        LinearLayout footerLayout = (LinearLayout) View.inflate(this,R.layout.bottom_button,null);
        setFootLayout(footerLayout);

        setContent(R.layout.activity_server_config);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
