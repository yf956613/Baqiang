package com.jiebao.baqiang.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.gson.Gson;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.VehicleInfo;
import com.jiebao.baqiang.data.bean.VehicleInfoList;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.util.FileUtil;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.VersionUpdateUtil;

import org.xutils.DbManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018/2/23 0023.
 */

public class TestHintActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG = TestHintActivity.class.getSimpleName();

    EditText productName;
    ListPopupWindow listPopupWindow;
    List<VehicleInfo> mVehicleInfos = null;

    /**
     * EditText 对内容的监听
     */
    class EditChangedListener implements TextWatcher {
        private CharSequence temp;//监听前的文本
        private int editStart;//光标开始位置
        private int editEnd;//光标结束位置
        private final int charMaxNum = 10;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LogUtil.trace("content:"+s);


        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.trace();

        try {
            int versionCode = VersionUpdateUtil.getVersionCode(BaqiangApplication.getContext());
            LogUtil.trace("versionCode:"+versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_list_popup_window);

        testResolveData(testServiceBackContent());

        View.OnKeyListener onKeyListener = new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(productName.isFocused()){
                    // 现在EditText获取了焦点
                    LogUtil.trace("keyCode:"+keyCode+"; event:"+event.getAction());
                    if(keyCode == 66){
                        listPopupWindow.show();
                    }
                }

                return false;
            }
        };


        productName = (EditText) findViewById(R.id.product_name);
        productName.addTextChangedListener(new EditChangedListener());
        productName.setOnKeyListener(onKeyListener);

        listPopupWindow = new ListPopupWindow(TestHintActivity.this);
        listPopupWindow.setAdapter(new ArrayAdapter(TestHintActivity.this, R.layout.list_item,
                mVehicleInfos));
        listPopupWindow.setAnchorView(productName);
        listPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        listPopupWindow.setHeight(400);

        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(TestHintActivity.this);
        productName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listPopupWindow.show();
            }
        });

        // 监听EditText是否获取焦点
        productName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    LogUtil.trace("hasFocus");

                    // listPopupWindow.show();

                } else {
                    LogUtil.trace("no hasFocus");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        productName.setText(mVehicleInfos.get(position).get车牌号());
        listPopupWindow.dismiss();
    }


    /**
     * 从文件中读取数据
     *
     * @return
     */
    private String testServiceBackContent() {
        String value = "";
        try {
            LogUtil.trace("path:" + Environment.getExternalStorageDirectory() + "/Tmp/carInfo" +
                    ".txt");
            value = FileUtil.readSDFile(Environment.getExternalStorageDirectory() +
                    "/Tmp/carInfo" + ".txt");
        } catch (IOException e) {
            LogUtil.trace("file is not exist...");
            e.printStackTrace();
        }

        return value;
    }

    private VehicleInfoList testResolveData(String vehicleInfo) {
        LogUtil.d(TAG, "vehicleInfo:" + vehicleInfo);

        Gson gson = new Gson();
        VehicleInfoList vehicleInfoList = gson.fromJson(vehicleInfo, VehicleInfoList.class);
        LogUtil.d(TAG, "length:" + vehicleInfoList.getVehicleInfoCnt());

        mVehicleInfos = vehicleInfoList.getVehicleInfo();

        return vehicleInfoList;
    }

    /**
     * 保存数据到数据库
     *
     * @return
     */
    private boolean storageData(final VehicleInfoList vehicleInfoList) {
        LogUtil.trace();

        new Thread(new Runnable() {

            @Override
            public void run() {
                List<VehicleInfo> vehicleInfos;
                vehicleInfos = vehicleInfoList.getVehicleInfo();

                DbManager db = BQDataBaseHelper.getDb();
                for (int index = 0; index < vehicleInfos.size(); index++) {
                    try {
                        VehicleInfo vehicleInfo = new VehicleInfo(vehicleInfos.get(index).get车牌号
                                (), vehicleInfos.get(index).get车辆识别号());
                        LogUtil.trace(vehicleInfo.toString());

                        db.save(vehicleInfo);
                    } catch (Exception exception) {
                        LogUtil.trace(exception.getMessage());
                        exception.printStackTrace();
                    }
                }
            }
        }).start();

        return true;
    }
}
