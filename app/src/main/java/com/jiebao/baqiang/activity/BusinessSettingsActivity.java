package com.jiebao.baqiang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by yaya on 2018/2/27.
 */

public class BusinessSettingsActivity extends BaseActivityWithTitleAndNumber implements View
        .OnClickListener {

    @ViewInject(R.id.btn_weigh_fore_payment)
    private Button mBtnWeighForePayment;
    @ViewInject(R.id.ll_weigh_fore_payment)
    private LinearLayout mLlWeighForePayment;

    @ViewInject(R.id.btn_weigh_input_business)
    private Button mBtnWeighInputBussiness;
    @ViewInject(R.id.ll_weigh_input_business)
    private LinearLayout mLlWeighInputBussiness;

    @ViewInject(R.id.btn_arrival_fore_payment)
    private Button mBtnArrivalForePayment;
    @ViewInject(R.id.ll_arrival_fore_payment)
    private LinearLayout mLlArrivalForePayment;

    @ViewInject(R.id.btn_scan_switch)
    private Button mBtnScanSwitch;
    @ViewInject(R.id.ll_scan_switch)
    private LinearLayout mLlScanSwitch;

    @Override
    public void initView() {
        setContent(R.layout.activity_business_settings);
        setHeaderLeftViewText("业务设置");
        x.view().inject(BusinessSettingsActivity.this);
    }

    @Override
    public void initData() {
        mBtnWeighForePayment.setOnClickListener(this);
        mBtnWeighForePayment.setOnFocusChangeListener(mLlFocusChangeListener);
        // TODO 默认选择第一个选项
        mBtnWeighForePayment.setFocusable(true);
        mBtnWeighForePayment.setFocusableInTouchMode(true);
        mBtnWeighForePayment.requestFocus();
        mBtnWeighForePayment.requestFocusFromTouch();

        mBtnWeighInputBussiness.setOnClickListener(this);
        mBtnWeighInputBussiness.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnArrivalForePayment.setOnClickListener(this);
        mBtnArrivalForePayment.setOnFocusChangeListener(mLlFocusChangeListener);
        mBtnScanSwitch.setOnClickListener(this);
        mBtnScanSwitch.setOnFocusChangeListener(mLlFocusChangeListener);
    }

    /**
     * 根据Button的状态，改变LinearLayout的背景
     *
     * @param v
     * @param hasFocus
     */
    private void setLinearLayoutBackground(View v, boolean hasFocus) {
        if (hasFocus) {
            v.setBackgroundResource(R.color.back_transpant);
        } else {
            v.setBackgroundResource(R.color.bg_transparent);
        }
    }

    private final View.OnFocusChangeListener mLlFocusChangeListener = new View
            .OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.btn_weigh_fore_payment: {
                    setLinearLayoutBackground(mLlWeighForePayment, hasFocus);
                    break;
                }

                case R.id.btn_weigh_input_business: {
                    setLinearLayoutBackground(mLlWeighInputBussiness, hasFocus);
                    break;
                }

                case R.id.btn_arrival_fore_payment: {
                    setLinearLayoutBackground(mLlArrivalForePayment, hasFocus);
                    break;
                }

                case R.id.btn_scan_switch: {
                    setLinearLayoutBackground(mLlScanSwitch, hasFocus);
                    break;
                }
            }
        }
    };

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

            case R.id.btn_scan_switch: {
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
