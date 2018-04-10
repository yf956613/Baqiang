package com.jiebao.baqiang.activity;

import android.view.View;
import android.widget.Button;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.data.db.DaojianDBHelper;
import com.jiebao.baqiang.data.db.FajianDBHelper;
import com.jiebao.baqiang.data.db.LiucangDBHelper;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 数据测试模式
 */

public class TestModeActivity extends BaseActivityWithTitleAndNumber
        implements View.OnClickListener {

    @ViewInject(R.id.btn_fajian_add)
    private Button mBtnFajianAdd;
    @ViewInject(R.id.btn_fajian_status_reversal)
    private Button mBtnFajianReversal;

    @ViewInject(R.id.btn_daojian_add)
    private Button mBtnDaojianAdd;
    @ViewInject(R.id.btn_daojian_status_reversal)
    private Button mBtnDaojianReversal;

    @ViewInject(R.id.btn_liucang_add)
    private Button mBtnLiucangAdd;
    @ViewInject(R.id.btn_liucang_status_reversal)
    private Button mBtnLiucangReversal;

    @Override
    public void initView() {
        setContent(R.layout.activity_test_mode);
        setHeaderLeftViewText("数据测试");
        x.view().inject(TestModeActivity.this);
    }

    @Override
    public void initData() {
        mBtnDaojianAdd.setOnClickListener(TestModeActivity.this);
        mBtnDaojianReversal.setOnClickListener(TestModeActivity.this);
        mBtnFajianAdd.setOnClickListener(TestModeActivity.this);
        mBtnFajianReversal.setOnClickListener(TestModeActivity.this);
        mBtnLiucangAdd.setOnClickListener(TestModeActivity.this);
        mBtnLiucangReversal.setOnClickListener(TestModeActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fajian_add: {
                FajianDBHelper.addSpecialNumberRecords();
                break;
            }
            case R.id.btn_fajian_status_reversal: {
                FajianDBHelper.reversalAllRecords();
                break;
            }

            case R.id.btn_daojian_add: {
                DaojianDBHelper.addSpecialNumberRecords();
                break;
            }
            case R.id.btn_daojian_status_reversal: {
                DaojianDBHelper.reversalAllRecords();
                break;
            }

            case R.id.btn_liucang_add: {
                LiucangDBHelper.addSpecialNumberRecords();
                break;
            }
            case R.id.btn_liucang_status_reversal: {
                LiucangDBHelper.reversalAllRecords();
                break;
            }

        }
    }
}
