package com.jiebao.baqiang.activity;

import android.content.Intent;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.x;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class SearchRecordsActivity extends BaseActivityWithTitleAndNumber {

    @Override
    public void initView() {
        setContent(R.layout.activity_search_records);
        x.view().inject(SearchRecordsActivity.this);
    }

    @Override
    public void initData() {
        Intent intent = this.getIntent();
        String searchType = intent.getStringExtra("search_type");
        String beginTime = intent.getStringExtra("start_time");
        String endTime = intent.getStringExtra("end_time");
        LogUtil.trace("type:" + searchType + "; begin:" + beginTime + "; end:" + endTime);

        if (Constant.SEARCH_NAME_ZCFJ.equals(searchType)) {
            setHeaderLeftViewText("装车发件查询");
        } else if (Constant.SEARCH_NAME_XCDJ.equals(searchType)) {
            setHeaderLeftViewText("卸车到件查询");
        } else if (Constant.SEARCH_NAME_DJ.equals(searchType)) {
            setHeaderLeftViewText("到件查询");
        } else if (Constant.SEARCH_NAME_FJ.equals(searchType)) {
            setHeaderLeftViewText("发件查询");
        } else if (Constant.SEARCH_NAME_LCJ.equals(searchType)) {
            setHeaderLeftViewText("留仓件查询");
        } else {
            // do nothing
        }
    }
}
