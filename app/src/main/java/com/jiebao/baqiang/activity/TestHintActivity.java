package com.jiebao.baqiang.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/23 0023.
 */

public class TestHintActivity extends Activity implements AdapterView
        .OnItemClickListener {
    private static final String TAG = TestHintActivity.class.getSimpleName();

    private EditText mEdtProductName;
    private ListPopupWindow mListPopupWindow;
    private List<String> mPreviousStationInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_popup_window);
        initData();

        View.OnKeyListener onKeyListener = new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mEdtProductName.isFocused()) {
                    // 现在EditText获取了焦点
                    LogUtil.trace("keyCode:" + keyCode + "; event:" + event
                            .getAction());
                    // keyCode 66 表示Enter确认键
                    if (keyCode == 66) {
                        mListPopupWindow.show();
                    }
                }
                return false;
            }
        };

        mEdtProductName = findViewById(R.id.product_name);
        // mEdtProductName.addTextChangedListener(new EditChangedListener());
        mEdtProductName.setOnKeyListener(onKeyListener);

        // 监听EditText是否获取焦点
        mEdtProductName.setOnFocusChangeListener(new View
                .OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // EditText焦点变化事件：进入界面后，显示第一个提示列表
                switch (v.getId()) {
                    case R.id.product_name: {
                        if (hasFocus) {
                            LogUtil.trace("hasFocus");

                            mListPopupWindow.show();
                        } else {
                            LogUtil.trace("no hasFocus");
                        }

                        break;
                    }
                }
            }
        });
        mEdtProductName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mListPopupWindow.show();
            }
        });

        mListPopupWindow = new ListPopupWindow(TestHintActivity.this);
        mListPopupWindow.setAdapter(new ArrayAdapter(TestHintActivity.this, R
                .layout.list_item,
                mPreviousStationInfo));
        mListPopupWindow.setAnchorView(mEdtProductName);
        mListPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mListPopupWindow.setHeight(400);
        mListPopupWindow.setModal(true);
        mListPopupWindow.setOnItemClickListener(TestHintActivity.this);
    }

    private void initData() {
        // 准备上一站网点数据
        mPreviousStationInfo = resolvePreviousStationData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        /*mEdtProductName.setText(mVehicleInfos.get(position).get车牌号());
        mListPopupWindow.dismiss();*/
    }

    /**
     * 解析上一站网点信息
     */
    private List<String> resolvePreviousStationData() {
        LogUtil.trace();

        Boolean isOpen = SharedUtil.getBoolean(TestHintActivity.this, Constant
                .PREFERENCE_KEY_SCAN_SWITCH);
        LogUtil.trace("isOpen:" + isOpen);

        List<SalesService> mData = null;
        DbManager dbManager = BQDataBaseHelper.getDb();
        try {
            mData = dbManager.findAll(SalesService.class);
            if (isOpen) {
                // 过滤：只包含类型为网点的站点信息
                for (int index = 0; index < mData.size(); index++) {
                    if (!"网点".equals(mData.get(index).get类型())) {
                        mData.remove(mData.get(index));
                    }
                }
            }
        } catch (DbException e) {
            LogUtil.trace();
            e.printStackTrace();
        }

        // 拼接：网点编号和网点名称
        List<String> mArrayInfo = new ArrayList<>();
        for (int index = 0; index < mData.size(); index++) {
            // 采用固定格式，便于解析网点编号
            mArrayInfo.add(mData.get(index).get网点编号() + "  " + mData.get
                    (index).get网点名称());
        }

        return mArrayInfo;
    }

    /**
     * EditText 对内容的监听
     */
    class EditChangedListener implements TextWatcher {
        private CharSequence temp;//监听前的文本
        private int editStart;//光标开始位置
        private int editEnd;//光标结束位置
        private final int charMaxNum = 10;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int
                count) {
            LogUtil.trace("content:" + s);


        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    ;

}
