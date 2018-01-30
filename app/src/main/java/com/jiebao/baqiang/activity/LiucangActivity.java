package com.jiebao.baqiang.activity;

import android.app.Activity;
import android.os.Bundle;
import com.jiebao.baqiang.R;

/**
 * Created by open on 2018/1/22.
 */

public class LiucangActivity  extends BaseActivity{

    @Override
    public void initView() {
        setContent(R.layout.liucang);
        initHeaderView();
    }

    public void initHeaderView() {
        setHeaderCenterViewText(getString(R.string.main_import));
    }

    @Override
    public void initData() {

    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }


}
