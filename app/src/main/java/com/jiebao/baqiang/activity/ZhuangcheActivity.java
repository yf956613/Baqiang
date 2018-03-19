package com.jiebao.baqiang.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.adapter.FajianAdatper;
import com.jiebao.baqiang.adapter.FilterListener;
import com.jiebao.baqiang.adapter.TestTipsAdatper;
import com.jiebao.baqiang.custView.CouldDeleteListView;
import com.jiebao.baqiang.data.bean.FajianListViewBean;
import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.data.bean.ShipmentType;
import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.bean.VehicleInfo;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.updateData.UpdateInterface;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianDispatchFileName;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.scan.ScanHelper;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 装车发件
 */

public class ZhuangcheActivity extends BaseActivityWithTitleAndNumber implements View
        .OnClickListener, CouldDeleteListView.DelButtonClickListener {
    private static final String TAG = "ZhuangcheActivity";

    private AutoCompleteTextView mTvNextStation;
    private AutoCompleteTextView mTvShipmentType;
    private AutoCompleteTextView mTvVehicleId;
    private EditText mEtDeliveryNumber;
    private Button mBtnSure, mBtnCancel;
    private CouldDeleteListView mListView;

    // 车牌识别号信息
    private List<String> mVehicleInfo;
    // 车辆信息数据适配器
    private TestTipsAdatper mVehicleInfoAdapter;

    // 下一站网点信息
    private List<String> mNextStationInfo;
    // 下一站快速提示数据适配器
    private TestTipsAdatper mNextStationAdapter;

    // 快件类型相关
    private List<ShipmentType> mShipmentTypeList;
    private TestTipsAdatper mShipmentType;

    // 用于更新ListView界面数据
    private List<FajianListViewBean> mListData;
    private FajianAdatper mFajianAdapter;

    // 此处作为全局扫描次数的记录，用于更新ListView的ID
    private int mScanCount;

    //往file 中写文件
    ZCFajianDispatchFileName mZcFajianDispatchFileName;
    ZCFajianFileContent mZcFajianFileContent;
    UploadServerFile mZcfajianUploadFile;

    private Vibrator mDeviceVibrator;

    private boolean mIsScanRunning = false;


    @Override
    protected void onDestroy() {
        super.onDestroy();

        ScanHelper.getInstance().barcodeManager.Barcode_Stop();
    }

    @Override
    public void initView() {
        setContent(R.layout.zhuangchefajian);
        setHeaderLeftViewText(getString(R.string.main_storge));
    }

    @Override
    public void initData() {
        prepareDataForView();

        ScanHelper.getInstance().barcodeManager.setScanTime(Constant.TIME_SCAN_DELAY);

        mDeviceVibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);

        mTvVehicleId = ZhuangcheActivity.this.findViewById(R.id.tv_vehicle_code);
        mTvVehicleId.setAdapter(mVehicleInfoAdapter);
        // 监听EditText是否获取焦点
        mTvVehicleId.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 如果当前内容为空，则提示；同时，编辑时自动提示
                    if (TextUtils.isEmpty(mTvVehicleId.getText())) {
                        mTvVehicleId.showDropDown();
                    }
                } else {
                    LogUtil.trace("mTvPreviousStation no hasFocus");
                }
            }
        });
        mTvVehicleId.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 一旦选定上一站，则解析网点编号，更新ShipmentFileContent实体内容
                String vehicleId = mTvVehicleId.getText().toString();
                LogUtil.d(TAG, "serverID:" + vehicleId);
                // 更新车辆识别码
                mZcFajianFileContent.setIdentify(vehicleId);
            }
        });
        mTvVehicleId.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DEL: {
                        mTvVehicleId.setText("", true);
                    }
                }
                return false;
            }
        });

        mTvNextStation = ZhuangcheActivity.this.findViewById(R.id.tv_next_station);
        mTvNextStation.setAdapter(mNextStationAdapter);
        // 监听EditText是否获取焦点
        mTvNextStation.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 如果当前内容为空，则提示；同时，编辑时自动提示
                    if (TextUtils.isEmpty(mTvNextStation.getText())) {
                        mTvNextStation.showDropDown();
                    }
                } else {
                    LogUtil.trace("mTvPreviousStation no hasFocus");
                }
            }
        });
        mTvNextStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 一旦选定下一站，则解析网点编号，更新ShipmentFileContent实体内容
                String serverID = mTvNextStation.getText().toString();
                LogUtil.d(TAG, "serverID:" + serverID);
                String[] arr = serverID.split("  ");
                // 更新下一站网点编号
                mZcFajianFileContent.setNextStation(arr[0]);
            }
        });
        mTvNextStation.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DEL: {
                        mTvNextStation.setText("", true);
                    }
                }
                return false;
            }
        });

        mTvShipmentType = ZhuangcheActivity.this.findViewById(R.id.tv_shipment_type);
        mTvShipmentType.setAdapter(mShipmentType);
        mTvShipmentType.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 如果当前内容为空，则提示；同时，编辑时自动提示
                    if (TextUtils.isEmpty(mTvShipmentType.getText())) {
                        mTvShipmentType.showDropDown();
                    }
                } else {
                    LogUtil.trace("mTvShipmentType no hasFocus");
                }
            }
        });
        mTvShipmentType.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 一旦选定快件类型，则解析快件类型编号，更新ShipmentFileContent实体内容
                String shipmentType = mTvShipmentType.getText().toString();
                LogUtil.d(TAG, "shipmentType:" + shipmentType);
                String[] arr = shipmentType.split("  ");
                mZcFajianFileContent.setShipmentType(arr[0]);
            }
        });
        mTvShipmentType.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DEL: {
                        mTvShipmentType.setText("", true);
                    }
                }
                return false;
            }
        });

        mEtDeliveryNumber = ZhuangcheActivity.this.findViewById(R.id.et_shipment_number);

        mBtnSure = ZhuangcheActivity.this.findViewById(R.id.btn_ensure);
        mBtnCancel = ZhuangcheActivity.this.findViewById(R.id.btn_back);
        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mListView = ZhuangcheActivity.this.findViewById(R.id.list_view_scan_data);
        mListView.setDelButtonClickListener(ZhuangcheActivity.this);
        mListView.setAdapter(mFajianAdapter);

        // 初次启动时刷新数据
        reQueryUnUploadDataForListView();
    }

    /**
     * 从数据库中找出所有未上传记录
     */
    private void reQueryUnUploadDataForListView() {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            // 查询数据库中标识位“未上传”的记录
            List<ZCFajianFileContent> data = db.selector(ZCFajianFileContent.class).where("是否上传",
                    "like", "未上传").and("是否可用", "=", "可用").findAll();
            if (null != data && data.size() != 0) {
                LogUtil.d(TAG, "未上传记录：" + data.size());

                // 清除数据
                mListData.clear();

                int count = 0;
                for (int index = 0; index < data.size(); index++) {
                    FajianListViewBean fajianListViewBean = new FajianListViewBean();
                    // TODO 一旦删除记录，则及时更新ID值
                    fajianListViewBean.setId(++count);
                    fajianListViewBean.setScannerData(data.get(index).getShipmentNumber());
                    fajianListViewBean.setStatus("未上传");
                    mListData.add(fajianListViewBean);
                }

                mFajianAdapter.notifyDataSetChanged();
                // 更新全局ID
                mScanCount = count;
            } else {
                // 清除数据
                mListData.clear();
                mFajianAdapter.notifyDataSetChanged();
                // 更新全局ID
                mScanCount = 0;
                LogUtil.trace("未上传 && 可用，过滤后无数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void prepareDataForView() {
        // 准备车辆码信息
        mVehicleInfo = resolveVehicleInfo();
        mVehicleInfoAdapter = new TestTipsAdatper(ZhuangcheActivity.this, mVehicleInfo, new
                FilterListener() {

            @Override
            public void getFilterData(List<String> list) {
                if (list != null) {
                    if (list.size() == 1) {
                        mTvVehicleId.dismissDropDown();
                        mTvVehicleId.setText(list.get(0), false);
                        Editable spannable = mTvVehicleId.getText();
                        Selection.setSelection(spannable, spannable.length());
                    }
                }
            }
        });

        // 准备下一站网点数据
        mNextStationInfo = resolveNextStationData();
        mNextStationAdapter = new TestTipsAdatper(ZhuangcheActivity.this, mNextStationInfo, new
                FilterListener() {

            @Override
            public void getFilterData(List<String> list) {
                if (list != null) {
                    if (list.size() == 1) {
                        String[] arr = list.get(0).toString().split("  ");
                        mTvNextStation.dismissDropDown();

                        if (arr != null && arr.length >= 2) {
                            mTvNextStation.setText(arr[1], false);
                        }

                        Editable spannable = mTvNextStation.getText();
                        Selection.setSelection(spannable, spannable.length());
                    }
                }
            }
        });

        // 解析快件类型信息
        resolveShipmentTypeData();

        mZcFajianFileContent = getZCFajianFileContent();


        mListData = new ArrayList<>();
        mFajianAdapter = new FajianAdatper(ZhuangcheActivity.this, mListData);
    }

    private List<String> resolveVehicleInfo() {
        LogUtil.trace();

        List<VehicleInfo> mData = null;
        DbManager dbManager = BQDataBaseHelper.getDb();
        try {
            mData = dbManager.findAll(VehicleInfo.class);
        } catch (DbException e) {
            LogUtil.trace();
            e.printStackTrace();
        }

        // 拼接：网点编号和网点名称
        List<String> mArrayInfo = new ArrayList<>();
        for (int index = 0; index < mData.size(); index++) {
            // 采用固定格式，便于解析网点编号
            mArrayInfo.add(mData.get(index).get车辆识别号());
        }

        return mArrayInfo;
    }

    private List<String> resolveNextStationData() {
        LogUtil.trace();

        List<SalesService> mData = null;
        DbManager dbManager = BQDataBaseHelper.getDb();
        try {
            mData = dbManager.findAll(SalesService.class);
        } catch (DbException e) {
            LogUtil.trace();
            e.printStackTrace();
        }

        // 拼接：网点编号和网点名称
        List<String> mArrayInfo = new ArrayList<>();
        for (int index = 0; index < mData.size(); index++) {
            // 采用固定格式，便于解析网点编号
            mArrayInfo.add(mData.get(index).get网点编号() + "  " + mData.get(index).get网点名称());
        }

        return mArrayInfo;
    }

    private void resolveShipmentTypeData() {
        mShipmentTypeList = queryShipmentTypeData();

        List<String> mShipmentData = new ArrayList<>();
        for (int index = 0; index < mShipmentTypeList.size(); index++) {
            // 采用固定格式便于解析快件类型
            mShipmentData.add(mShipmentTypeList.get(index).get类型编号() + "  " + mShipmentTypeList
                    .get(index).get类型名称());
        }

        // TODO 将 汽运 排到最先
        Collections.sort(mShipmentData);

        LogUtil.trace("size:" + mShipmentData.size());
        mShipmentType = new TestTipsAdatper(ZhuangcheActivity.this, mShipmentData, new
                FilterListener() {

            @Override
            public void getFilterData(List<String> list) {
                if (list != null) {
                    if (list.size() == 1) {
                        String[] arr = list.get(0).toString().split("  ");
                        mTvShipmentType.dismissDropDown();

                        if (arr != null && arr.length >= 2) {
                            mTvShipmentType.setText(arr[1], false);
                        }

                        Editable spannable = mTvShipmentType.getText();
                        Selection.setSelection(spannable, spannable.length());
                    }
                }
            }
        });
    }

    private ZCFajianFileContent getZCFajianFileContent() {
        // 下一站网点编码
        String nextStation = "";
        // 扫描日期
        String scanDate = TextStringUtil.getFormatTimeString();
        // 物品类型
        String goodsType = "";
        // 快件类型
        String shipmentType = "";
        // 运单编号
        String shipmentNumber = "";
        // 扫描员工编号
        String scanEmployeeNumber = UpdateInterface.userName;
        // 操作日期
        String operateDate = TextStringUtil.getFormatTime();
        // 重量
        String weight = "0.0";
        //车辆识别码
        String identify = "";
        // 是否上传状态
        String status = "未上传";
        // 是否可用
        String isUsed = "可用";

        return new ZCFajianFileContent(nextStation, scanDate, goodsType, shipmentType,
                shipmentNumber, scanEmployeeNumber, operateDate, weight, identify, status, isUsed);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case Constant.SCAN_KEY_CODE: {
                // FIXME 执行一次扫码操作，判断前置条件满足？
                if (TextUtils.isEmpty(mTvVehicleId.getText().toString()) || TextUtils.isEmpty
                        (mTvNextStation.getText().toString()) || TextUtils.isEmpty
                        (mTvShipmentType.getText().toString())) {
                    Toast.makeText(ZhuangcheActivity.this, "前置信息为空", Toast.LENGTH_SHORT).show();

                    mDeviceVibrator.vibrate(1000);
                    return true;
                } else {
                    LogUtil.trace("mIsScanRunning:" + mIsScanRunning);
                    // FIXME 启动扫描线程，需要考虑多次按下的问题
                    if (!mIsScanRunning) {
                        // 没有扫码，发出一次扫码广播
                        Intent intent = new Intent();
                        intent.setAction("com.jb.action.F4key");
                        intent.putExtra("F4key", "down");
                        ZhuangcheActivity.this.sendBroadcast(intent);

                        LogUtil.trace("3: mIsScanRunning=" + mIsScanRunning);

                        mIsScanRunning = true;
                    }
                }

                return true;
            }
            case Constant.F2_KEY_CODE: {
                // 1. 获取最后（最新）扫入的barcode
                if (mListData != null && mListData.size() != 0) {
                    LogUtil.trace("mListData.size:" + mListData.size() + "; " + "barcode:" +
                            mListData.get(mListData.size() - 1).getScannerData());

                    // 提示是否切换账号
                    final AlertDialog.Builder normalDialog = new AlertDialog.Builder
                            (ZhuangcheActivity.this);
                    normalDialog.setTitle("提示");
                    normalDialog.setCancelable(false);
                    normalDialog.setMessage("是否删除最新记录？");
                    normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 2. 设置数据库对应记录的“是否可用”状态为：不可用
                            deleteFindedBean(mListData.get(mListData.size() - 1).getScannerData());

                            // 3. 重新从数据库中查出所有记录,更新ListView
                            reQueryUnUploadDataForListView();
                        }
                    });
                    normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    normalDialog.show();
                }

                // 消费F2按键事件
                return true;
            }

            default:
                break;
        }

        // 需要调用super方法，让back起作用
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void timeout(long timeout) {
        super.timeout(timeout);

        LogUtil.trace("timeout:" + timeout);
        mIsScanRunning = false;
    }


    @Override
    protected void fillCode(String barcode) {
        LogUtil.d(TAG, "barcode:" + barcode);

        // 1. 查表：当前是名为zcfajian的表，判断是否有记录
        if (isExistCurrentBarcode(barcode)) {
            // 若有记录则提示重复；若没有，继续执行
            Toast.makeText(ZhuangcheActivity.this, "运单号已存在", Toast.LENGTH_SHORT).show();
            mDeviceVibrator.vibrate(1000);

            mIsScanRunning = true;

            // 发出一次扫码广播
            Intent intent = new Intent();
            intent.setAction("com.jb.action.F4key");
            intent.putExtra("F4key", "down");
            ZhuangcheActivity.this.sendBroadcast(intent);

            LogUtil.trace("1: mIsScanRunning=" + mIsScanRunning);

            return;
        }

        // 2. 插入到数据库中
        mZcFajianFileContent.setGoodsType("2");
        mZcFajianFileContent.setScanDate(TextStringUtil.getFormatTimeString());
        mZcFajianFileContent.setShipmentNumber(barcode);
        mZcFajianFileContent.setOperateDate(TextStringUtil.getFormatTime());
        insertDataToDatabase(mZcFajianFileContent);

        // 3. 填充EditText控件
        mEtDeliveryNumber.setText(barcode);

        // 4. 更新ListView的数据
        FajianListViewBean mFajianListViewBean = new FajianListViewBean();
        mFajianListViewBean.setId(++mScanCount);
        mFajianListViewBean.setScannerData(barcode);
        mFajianListViewBean.setStatus("未上传");

        mListData.add(mFajianListViewBean);
        mFajianAdapter.notifyDataSetChanged();

        // 发出一次扫码广播
        Intent intent = new Intent();
        intent.setAction("com.jb.action.F4key");
        intent.putExtra("F4key", "down");
        ZhuangcheActivity.this.sendBroadcast(intent);

        LogUtil.trace("2: mIsScanRunning=" + mIsScanRunning);

        mIsScanRunning = true;
    }

    private static final String DB_NAME = "zcfajian";

    /**
     * 判断数据库中是否有当前运单记录
     *
     * @param barcode
     * @return
     */
    private boolean isExistCurrentBarcode(String barcode) {
        if (tableIsExist(DB_NAME)) {
            // 存在保存发件数据的表，从该表中查询对应的单号
            DbManager dbManager = BQDataBaseHelper.getDb();

            try {
                // FIXME 查询数据库，是否有记录；增加时间戳
                // and("是否可用", "like", "可用")
                List<ZCFajianFileContent> bean = dbManager.selector(ZCFajianFileContent.class)
                        .where("运单编号", "like", barcode).and("是否可用", "like", "可用").findAll();

                if (bean != null && bean.size() != 0) {
                    LogUtil.trace("size:" + bean.size());

                    for (int index = 0; index < bean.size(); index++) {
                        long[] delta = TextStringUtil.getDistanceTimes(bean.get(index)
                                .getScanDate(), TextStringUtil.getFormatTimeString());
                        if (isTimeOutOfRange(delta)) {
                            // 超出指定时间，存入数据库 --> return false
                            LogUtil.trace("超出指定时间");

                            continue;
                        } else {
                            // 不需存入数据库 --> return true
                            LogUtil.trace("在指定时间之内");
                            return true;
                        }
                    }
                }// go to return false
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    // 差距时间：3小时
    private static final long DELTA_TIME_DISTANCE = 10800;

    /**
     * 判断时间差距是否超出预期值
     * <p>
     * 预期时间：3小时
     *
     * @param delta: {天, 时, 分, 秒}
     * @return
     */
    private boolean isTimeOutOfRange(long[] delta) {
        // 将时间数字转化为数值，判断数值是否超出即可
        long deltaValue = delta[0] * 24 * 60 * 60 + delta[1] * 60 * 60 + delta[2] * 60 + delta[3];
        if (deltaValue > DELTA_TIME_DISTANCE) {
            return true;
        }

        return false;
    }

    /**
     * 查询数据库文件中是否有车辆信息表
     *
     * @return false：没有该数据表；true：存在该数据表
     */
    public boolean tableIsExist(String tableName) {
        LogUtil.trace("tableName:" + tableName);
        boolean result = false;

        if (tableName == null) {
            return false;
        }

        DbManager dbManager = BQDataBaseHelper.getDb();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbManager.getDatabase();
            // 查询内置sqlite_master表，判断是否创建了对应表
            String sql = "select count(*) from sqlite_master where type " + "='table' and name "
                    + "='" + tableName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
        } catch (Exception e) {
            LogUtil.trace(e.getMessage());
            // TODO: handle exception
        }

        return result;
    }

    /**
     * 每次扫描后，先将数据存入数据库，需要的数据可根据ShipmentFileContent对应
     * <p>
     * 与之相关的数据库Table为：fajian
     */
    private void insertDataToDatabase(final ZCFajianFileContent zcFajianFileContent) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                DbManager db = BQDataBaseHelper.getDb();
                try {
                    db.save(zcFajianFileContent);
                } catch (DbException e) {
                    LogUtil.trace(e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ensure: {
                // 确定按键，上传文件
                LogUtil.trace();

                DbManager db = BQDataBaseHelper.getDb();
                List<ZCFajianFileContent> list = null;
                try {
                    // FIXME 1. 查询数据库中标识位是“未上传”的记录，且是数据可用
                    list = db.selector(ZCFajianFileContent.class).where("是否上传", "like", "未上传")
                            .and("是否可用", "=", "可用").findAll();
                    if (null != list && list.size() != 0) {
                        // 2. 获取随机文件名
                        mZcFajianDispatchFileName = new ZCFajianDispatchFileName();
                        if (mZcFajianDispatchFileName.linkToTXTFile()) {
                            // 3. 链接创建的文件和上传功能
                            mZcfajianUploadFile = new UploadServerFile(mZcFajianDispatchFileName
                                    .getFileInstance());
                            for (int index = 0; index < list.size(); index++) {
                                // 4. 创建写入文本的字符串，并写入文本
                                ZCFajianFileContent javaBean = list.get(index);
                                String content = javaBean.getmCurrentValue() + "\r\n";
                                if (mZcfajianUploadFile.writeContentToFile(content, true)) {
                                    // 不能删除数据，应该是否上传标志位为：已上传
                                    WhereBuilder whereBuilder = WhereBuilder.b();
                                    whereBuilder.and("运单编号", "=", javaBean.getShipmentNumber());
                                    // 5. 将当前数据库中对应数据“是否上传”标志置为：已上传
                                    db.update(ZCFajianFileContent.class, whereBuilder, new
                                            KeyValue("是否上传", "已上传"));
                                } else {
                                    // TODO 写入文件失败
                                    LogUtil.trace("写入文件失败");
                                }
                            }

                            // 6. 文件上传服务器
                            mZcfajianUploadFile.uploadFile();
                            ZhuangcheActivity.this.finish();
                        } else {
                            // TODO 创建文件失败
                            LogUtil.trace("创建文件失败");
                        }
                    } else {
                        LogUtil.trace("当前数据库没有需要上传数据");
                    }
                } catch (DbException e) {
                    LogUtil.d(TAG, "崩溃信息:" + e.getLocalizedMessage());
                    e.printStackTrace();
                }
                break;
            }

            case R.id.btn_back: {
                // 返回按键，不上传文件
                LogUtil.trace();
                ZhuangcheActivity.this.finish();

                // 测试阶段删除所有记录
                DbManager db = BQDataBaseHelper.getDb();
                try {
                    List<ZCFajianFileContent> list = db.findAll(ZCFajianFileContent.class);
                    if (list != null) {
                        LogUtil.d(TAG, "当前记录：" + list.size());
                        // db.delete(ShipmentFileContent.class);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

                break;
            }
        }

    }

    /**
     * 从数据库中取出快件类型数据
     *
     * @return
     */
    private List<ShipmentType> queryShipmentTypeData() {
        List<ShipmentType> mData = null;
        DbManager dbManager = BQDataBaseHelper.getDb();
        try {
            mData = dbManager.findAll(ShipmentType.class);
        } catch (DbException e) {
            LogUtil.trace();
            e.printStackTrace();
        }
        return mData;
    }

    @Override
    public void clickHappend(int position) {
        // 删除按键
        LogUtil.trace("position:" + position);

        // 1. 找到当前position的运单号
        LogUtil.d(TAG, "待删除的内容:" + mListData.get(position).getScannerData());

        // 2. 删除数据库中对应的记录
        deleteFindedBean(mListData.get(position).getScannerData());

        // 3. 重新从数据库中查出所有记录,更新ListView
        reQueryUnUploadDataForListView();
    }

    /**
     * 点击删除按键后，删除对应的数据项
     *
     * @param barcode
     */
    private void deleteFindedBean(final String barcode) {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            db.delete(ZCFajianFileContent.class, WhereBuilder.b("运单编号", "like", barcode));
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void syncViewAfterUpload() {
        super.syncViewAfterUpload();

        reQueryUnUploadDataForListView();
    }

}
