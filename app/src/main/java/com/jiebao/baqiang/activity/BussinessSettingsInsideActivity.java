package com.jiebao.baqiang.activity;

import android.view.KeyEvent;
import android.widget.RadioButton;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

/**
 * Created by yaya on 2018/2/27.
 */

public class BussinessSettingsInsideActivity extends BaseActivity {

    private int mTitleId;
    private RadioButton mRbOpen;
    private RadioButton mRbClose;

    @Override
    public void initView() {
        String title = BussinessSettingsInsideActivity.this.getIntent()
                .getStringExtra("title_name");
       String titleId = BussinessSettingsInsideActivity.this.getIntent()
                .getStringExtra("title_number");
       mTitleId = Integer.valueOf(titleId);

        setHeaderCenterViewText(title);
        setContent(R.layout.activity_business_settings_inside);
    }

    @Override
    public void initData() {
        LogUtil.trace("mTitleId:" + mTitleId);
        mRbOpen = findViewById(R.id.btn_open);
        mRbClose = findViewById(R.id.btn_close);

        boolean booleanFlag = false;

        switch (mTitleId) {
            case 1: {
                booleanFlag = SharedUtil.getBoolean
                        (BussinessSettingsInsideActivity
                                .this, Constant
                                .PREFERENCE_KEY_WEIGH_FORE_PAYMENT);
                break;
            }

            case 2: {
                booleanFlag = SharedUtil.getBoolean
                        (BussinessSettingsInsideActivity
                                .this, Constant
                                .PREFERENCE_KEY_WEIGH_INPUT_BUSINESS);
                break;
            }

            case 3: {
                booleanFlag = SharedUtil.getBoolean
                        (BussinessSettingsInsideActivity
                                .this, Constant
                                .PREFERENCE_KEY_ARRIVAL_FORE_PAYMENT);
                break;
            }

            case 4: {
                booleanFlag = SharedUtil.getBoolean
                        (BussinessSettingsInsideActivity
                                .this, Constant.PREFERENCE_KEY_SCAN_SWITCH);
                break;
            }
        }

        LogUtil.trace("booleanFlag:" + booleanFlag);
        // 数据回显
        if (booleanFlag) {
            mRbOpen.setChecked(true);
        } else {
            mRbClose.setChecked(true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.trace("keyCode:" + keyCode + "; event:" + event.getAction());
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (mTitleId) {
                case 1: {
                    // 称重预付款设置
                    if (mRbClose.isChecked()) {
                        LogUtil.trace("close is checked");

                        SharedUtil.putBoolean(BussinessSettingsInsideActivity
                                        .this,
                                Constant.PREFERENCE_KEY_WEIGH_FORE_PAYMENT,
                                false);
                    } else if (mRbOpen.isChecked()) {
                        LogUtil.trace("open is checked");

                        SharedUtil.putBoolean(BussinessSettingsInsideActivity
                                        .this,
                                Constant.PREFERENCE_KEY_WEIGH_FORE_PAYMENT,
                                true);
                    }

                    break;
                }

                case 2: {
                    // 称重录单设置
                    if (mRbClose.isChecked()) {
                        LogUtil.trace("close is checked");

                        SharedUtil.putBoolean(BussinessSettingsInsideActivity
                                        .this,
                                Constant.PREFERENCE_KEY_WEIGH_INPUT_BUSINESS,
                                false);
                    } else if (mRbOpen.isChecked()) {
                        LogUtil.trace("open is checked");

                        SharedUtil.putBoolean(BussinessSettingsInsideActivity
                                        .this,
                                Constant.PREFERENCE_KEY_WEIGH_INPUT_BUSINESS,
                                true);
                    }
                    break;
                }

                case 3: {
                    // 到件预付款设置
                    if (mRbClose.isChecked()) {
                        LogUtil.trace("close is checked");

                        SharedUtil.putBoolean(BussinessSettingsInsideActivity
                                        .this,
                                Constant.PREFERENCE_KEY_ARRIVAL_FORE_PAYMENT,
                                false);
                    } else if (mRbOpen.isChecked()) {
                        LogUtil.trace("open is checked");

                        SharedUtil.putBoolean(BussinessSettingsInsideActivity
                                        .this,
                                Constant.PREFERENCE_KEY_ARRIVAL_FORE_PAYMENT,
                                true);
                    }
                    break;
                }

                case 4: {
                    // 到/发件扫描判断开关
                    if (mRbClose.isChecked()) {
                        LogUtil.trace("close is checked");

                        SharedUtil.putBoolean(BussinessSettingsInsideActivity
                                        .this,
                                Constant.PREFERENCE_KEY_SCAN_SWITCH, false);
                    } else if (mRbOpen.isChecked()) {
                        LogUtil.trace("open is checked");

                        SharedUtil.putBoolean(BussinessSettingsInsideActivity
                                        .this,
                                Constant.PREFERENCE_KEY_SCAN_SWITCH, true);
                    }
                    break;
                }

                default:

            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
