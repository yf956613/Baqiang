package com.jiebao.baqiang.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.jiebao.baqiang.R;

/**
 * Created by yaya on 2018/2/27.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button mBtnDataCollect;

    @Override
    public void initView() {
        setHeaderCenterViewText("速尔手持终端软件");
        setContent(R.layout.activity_main);
    }

    @Override
    public void initData() {
        mBtnDataCollect = findViewById(R.id.btn_data_collect);

        initListener();

        // TODO 数据更新
    }

    private void initListener() {
        mBtnDataCollect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_data_collect: {
                Intent intent = new Intent(MainActivity.this,
                        DataCollectActivity.class);
                MainActivity.this.startActivity(intent);

                break;
            }
        }
    }
}
