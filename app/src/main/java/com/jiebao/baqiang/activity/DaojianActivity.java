package com.jiebao.baqiang.activity;

import android.app.Activity;
import android.os.Bundle;
import com.jiebao.baqiang.R;

/**
 * Created by open on 2018/1/22.
 */

public class DaojianActivity extends BaseActivity{

    @Override
    public void initView() {
        setContent(R.layout.daojian);
        initHeaderView();
    }

    public void initHeaderView() {
        setHeaderCenterViewText(getString(R.string.main_query));
    }

    @Override
    public void initData() {

    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }


}
