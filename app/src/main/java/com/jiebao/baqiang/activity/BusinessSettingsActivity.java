package com.jiebao.baqiang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.util.LogUtil;

/**
 * Created by yaya on 2018/2/27.
 */

public class BusinessSettingsActivity extends Activity implements View
        .OnClickListener {

    private Button mBtnWeighForePayment;
    private Button mBtnWeighInputBussiness;
    private Button mBtnArrivalForePayment;
    private Button mBtnScanSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_business_settings);

        initData();
    }

    private void initData() {
        mBtnWeighForePayment = findViewById(R.id.btn_weigh_fore_payment);
        mBtnWeighInputBussiness = findViewById(R.id.btn_weigh_input_business);
        mBtnArrivalForePayment = findViewById(R.id.btn_arrival_fore_payment);
        mBtnScanSwitch = findViewById(R.id.scan_switch);

        initListener();
    }

    private void initListener() {
        mBtnWeighForePayment.setOnClickListener(this);
        mBtnWeighInputBussiness.setOnClickListener(this);
        mBtnArrivalForePayment.setOnClickListener(this);
        mBtnScanSwitch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_weigh_fore_payment: {
                LogUtil.trace("goto 称重预付款设置");

                Intent intent = new Intent(BusinessSettingsActivity.this,
                        BussinessSettingsInsideActivity.class);
                intent.putExtra("title_name", "称重预付款设置");
                intent.putExtra("title_number", "1");
                BusinessSettingsActivity.this.startActivity(intent);
                break;
            }

            case R.id.btn_weigh_input_business: {
                LogUtil.trace("goto 称重录单设置");

                Intent intent = new Intent(BusinessSettingsActivity.this,
                        BussinessSettingsInsideActivity.class);
                intent.putExtra("title_name", "称重录单设置");
                intent.putExtra("title_number", "2");
                BusinessSettingsActivity.this.startActivity(intent);
                break;
            }

            case R.id.btn_arrival_fore_payment: {
                LogUtil.trace("goto 到件预付款设置");

                Intent intent = new Intent(BusinessSettingsActivity.this,
                        BussinessSettingsInsideActivity.class);
                intent.putExtra("title_name", "到件预付款设置");
                intent.putExtra("title_number", "3");
                BusinessSettingsActivity.this.startActivity(intent);
                break;
            }

            case R.id.scan_switch: {
                LogUtil.trace("goto 到/发件扫描判断开关");

                Intent intent = new Intent(BusinessSettingsActivity.this,
                        BussinessSettingsInsideActivity.class);
                intent.putExtra("title_name", "到/发件扫描判断开关");
                intent.putExtra("title_number", "4");
                BusinessSettingsActivity.this.startActivity(intent);
                break;
            }
        }
    }
}
