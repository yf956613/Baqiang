package com.jiebao.baqiang.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.adapter.ScannerBaseAdatper;
import com.jiebao.baqiang.adapter.FilterListener;
import com.jiebao.baqiang.adapter.TestTipsAdatper;
import com.jiebao.baqiang.custView.CouldDeleteListView;
import com.jiebao.baqiang.data.bean.FileContentHelper;
import com.jiebao.baqiang.data.bean.ScannerListViewBean;
import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.db.SalesServiceDBHelper;
import com.jiebao.baqiang.data.db.ShipmentTypeDBHelper;
import com.jiebao.baqiang.data.db.VehicleInfoDBHelper;
import com.jiebao.baqiang.data.db.ZcFajianDBHelper;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianDispatchFileName;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.scan.ScanHelper;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;
import com.jiebao.baqiang.util.TimeUtil;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

public class ZhuangcheActivity extends BaseActivityWithTitleAndNumber implements View
        .OnClickListener, CouldDeleteListView.DelButtonClickListener {
    private static final String TAG = ZhuangcheActivity.class.getSimpleName();

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
    private List<String> mShipmentTypeInfo;
    // 快件类型相关数据适配器
    private TestTipsAdatper mShipmentTypeAdapter;

    // 保存当前功能项录入的记录
    private List<ScannerListViewBean> mListData;
    private ScannerBaseAdatper mScannerBaseAdatper;

    private ZCFajianDispatchFileName mZcFajianDispatchFileName;
    private ZCFajianFileContent mZcFajianFileContent;
    private UploadServerFile mZcfajianUploadFile;

    private Vibrator mDeviceVibrator;
    private int mScanCount;
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

        prepareDataForView();
    }


    @Override
    public void initData() {
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
                String vehicleId = mTvVehicleId.getText().toString();
                LogUtil.d(TAG, "serverID:" + vehicleId);
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
                String serverName = mTvNextStation.getText().toString();
                String serverID = SalesServiceDBHelper.getServerIdFromName(serverName);
                if (!TextUtils.isEmpty(serverID)) {
                    // 更新下一站网点编号
                    mZcFajianFileContent.setNextStation(serverID);
                }
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
        mTvShipmentType.setAdapter(mShipmentTypeAdapter);
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
                String shipmentTypeName = mTvShipmentType.getText().toString();
                String shipmentTypeID = ShipmentTypeDBHelper.getShipmentTypeIDFromName
                        (shipmentTypeName);
                if (!TextUtils.isEmpty(shipmentTypeID)) {
                    mZcFajianFileContent.setShipmentType(shipmentTypeID);
                }
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
        mListView.setAdapter(mScannerBaseAdatper);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case Constant.SCAN_KEY_CODE: {
                if (!VehicleInfoDBHelper.checkVehicleInfo(mTvVehicleId.getText().toString())) {
                    Toast.makeText(ZhuangcheActivity.this, "车辆码信息异常", Toast.LENGTH_SHORT).show();

                    mDeviceVibrator.vibrate(1000);
                    return true;
                } else if (!SalesServiceDBHelper.checkServerInfo(mTvNextStation.getText()
                        .toString())) {
                    Toast.makeText(ZhuangcheActivity.this, "下一站网点信息异常", Toast.LENGTH_SHORT).show();

                    mDeviceVibrator.vibrate(1000);
                    return true;
                } else if (!ShipmentTypeDBHelper.checkShipmentType(mTvShipmentType.getText()
                        .toString())) {
                    Toast.makeText(ZhuangcheActivity.this, "快件类型信息异常", Toast.LENGTH_SHORT).show();

                    mDeviceVibrator.vibrate(1000);
                    return true;
                } else {
                    LogUtil.trace("mIsScanRunning:" + mIsScanRunning);
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
                deleteLastestRecord();
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
    protected void fillCode(String barcode) {
        LogUtil.d(TAG, "barcode:" + barcode);

        if (ZcFajianDBHelper.isExistCurrentBarcode(barcode)) {
            Toast.makeText(ZhuangcheActivity.this, "运单号已存在", Toast.LENGTH_SHORT).show();
            mDeviceVibrator.vibrate(1000);

            mIsScanRunning = true;
            Intent intent = new Intent();
            intent.setAction("com.jb.action.F4key");
            intent.putExtra("F4key", "down");
            ZhuangcheActivity.this.sendBroadcast(intent);
            LogUtil.trace("1: mIsScanRunning=" + mIsScanRunning);

            return;
        }

        mZcFajianFileContent.setGoodsType("2");
        mZcFajianFileContent.setScanDate(TextStringUtil.getFormatTimeString());
        mZcFajianFileContent.setShipmentNumber(barcode);
        mZcFajianFileContent.setOperateDate(TextStringUtil.getFormatTime());
        ZcFajianDBHelper.insertDataToDatabase(mZcFajianFileContent);

        mEtDeliveryNumber.setText(barcode);

        ScannerListViewBean mFajianListViewBean = new ScannerListViewBean();
        mFajianListViewBean.setId(++mScanCount);
        mFajianListViewBean.setScannerData(barcode);
        mFajianListViewBean.setStatus("未上传");

        mListData.add(0, mFajianListViewBean);
        mScannerBaseAdatper.notifyDataSetChanged();

        if (!mListView.isStackFromBottom()) {
            mListView.setStackFromBottom(true);
        }
        mListView.setStackFromBottom(false);

        Intent intent = new Intent();
        intent.setAction("com.jb.action.F4key");
        intent.putExtra("F4key", "down");
        ZhuangcheActivity.this.sendBroadcast(intent);

        LogUtil.trace("2: mIsScanRunning=" + mIsScanRunning);

        mIsScanRunning = true;
    }

    @Override
    protected void timeout(long timeout) {
        super.timeout(timeout);
        LogUtil.trace("timeout:" + timeout);
        mIsScanRunning = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ensure: {
                LogUtil.trace();

                DbManager db = BQDataBaseHelper.getDb();
                List<ZCFajianFileContent> list = null;
                try {
                    list = db.selector(ZCFajianFileContent.class).where("是否上传", "like", "未上传")
                            .and("是否可用", "=", "可用").findAll();
                    if (null != list && list.size() != 0) {
                        mZcFajianDispatchFileName = new ZCFajianDispatchFileName();
                        if (mZcFajianDispatchFileName.linkToTXTFile()) {
                            mZcfajianUploadFile = new UploadServerFile(mZcFajianDispatchFileName
                                    .getFileInstance());
                            for (int index = 0; index < list.size(); index++) {
                                ZCFajianFileContent javaBean = list.get(index);
                                String content = javaBean.getmCurrentValue() + "\r\n";
                                if (mZcfajianUploadFile.writeContentToFile(content, true)) {
                                    WhereBuilder whereBuilder = WhereBuilder.b();
                                    whereBuilder.and("运单编号", "=", javaBean.getShipmentNumber());
                                    whereBuilder.and("是否可用", "=", "可用");
                                    db.update(ZCFajianFileContent.class, whereBuilder, new
                                            KeyValue("是否上传", "已上传"));
                                } else {
                                    LogUtil.trace("写入文件失败");
                                }
                            }

                            // 6. 文件上传服务器
                            mZcfajianUploadFile.uploadFile();
                            ZhuangcheActivity.this.finish();
                        } else {
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
                ZhuangcheActivity.this.finish();

                LogUtil.trace("All size:" + ZcFajianDBHelper.findAllBean());
                break;
            }
        }
    }

    @Override
    public void clickHappend(int position) {
        deleteChooseRecord(position);
    }

    @Override
    public void syncViewAfterUpload() {
        super.syncViewAfterUpload();
    }

    private void prepareDataForView() {
        mVehicleInfo = VehicleInfoDBHelper.getAllVehicleID();
        mVehicleInfoAdapter = new TestTipsAdatper(ZhuangcheActivity.this, mVehicleInfo, new
                FilterListener() {

            @Override
            public void getFilterData(List<String> list) {
                if (list != null) {
                    if (list.size() == 1) {
                        mTvVehicleId.dismissDropDown();
                        mTvVehicleId.setText(list.get(0), false);

                        mZcFajianFileContent.setIdentify(list.get(0));

                        Editable spannable = mTvVehicleId.getText();
                        Selection.setSelection(spannable, spannable.length());
                    } else {
                        // do nothing
                    }
                } else {
                    // do nothing
                }
            }
        });

        mNextStationInfo = SalesServiceDBHelper.getAllSalesServiceData();
        mNextStationAdapter = new TestTipsAdatper(ZhuangcheActivity.this, mNextStationInfo, new
                FilterListener() {

            @Override
            public void getFilterData(List<String> list) {
                if (list != null) {
                    if (list.size() == 1) {
                        String[] arr = list.get(0).toString().split("  ");
                        mTvNextStation.dismissDropDown();

                        if (arr != null) {
                            if (arr.length >= 2) {
                                mTvNextStation.setText(arr[1], false);
                                mZcFajianFileContent.setNextStation(arr[0]);
                            }
                        } else {
                            mTvNextStation.setText(list.get(0).toString(), false);
                        }

                        Editable spannable = mTvNextStation.getText();
                        Selection.setSelection(spannable, spannable.length());
                    } else {
                        // do nothing
                    }
                } else {
                    // do nothing
                }
            }
        });

        mShipmentTypeInfo = ShipmentTypeDBHelper.getShipmentTypeInfo();
        mShipmentTypeAdapter = new TestTipsAdatper(ZhuangcheActivity.this, mShipmentTypeInfo, new
                FilterListener() {

            @Override
            public void getFilterData(List<String> list) {
                if (list != null) {
                    if (list.size() == 1) {
                        String[] arr = list.get(0).toString().split("  ");
                        mTvShipmentType.dismissDropDown();

                        if (arr != null) {
                            if (arr.length >= 2) {
                                mTvShipmentType.setText(arr[1], false);
                                mZcFajianFileContent.setShipmentType(arr[0]);
                            }
                        } else {
                            mTvShipmentType.setText(list.get(0).toString(), false);
                        }

                        Editable spannable = mTvShipmentType.getText();
                        Selection.setSelection(spannable, spannable.length());
                    } else {
                        // do nothing
                    }
                } else {
                    // do nothing
                }
            }
        });

        mZcFajianFileContent = FileContentHelper.getZCFajianFileContent();

        mListData = new ArrayList<>();
        mScannerBaseAdatper = new ScannerBaseAdatper(ZhuangcheActivity.this, mListData);
    }

    /**
     * F2按键触发删除最新扫码记录，ListView最上一条记录
     */
    private void deleteLastestRecord() {
        if (mListData != null && mListData.size() != 0) {
            LogUtil.trace("mListData.size:" + mListData.size() + "; " + "barcode:" + mListData
                    .get(0).getScannerData());

            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(ZhuangcheActivity
                    .this);
            normalDialog.setTitle("提示");
            normalDialog.setCancelable(false);
            normalDialog.setMessage("是否删除最新记录？");
            normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ZcFajianDBHelper.deleteFindedBean(mListData.get(0).getScannerData());
                    updateListViewForDelete(DeleteAction.DELETE_ACTION_F2, mListData.get(0)
                            .getScannerData());
                }
            });
            normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            normalDialog.show();
        } else {
            // do nothing
        }
    }

    /**
     * 通过滑动删除指定记录
     *
     * @param position
     */
    private void deleteChooseRecord(final int position) {
        if (mListData != null && mListData.size() != 0) {
            LogUtil.trace("position:" + position + "; " + "barcode:" + mListData.get(position)
                    .getScannerData());

            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(ZhuangcheActivity
                    .this);
            normalDialog.setTitle("提示");
            normalDialog.setCancelable(false);
            normalDialog.setMessage("是否记录？");
            normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ZcFajianDBHelper.deleteFindedBean(mListData.get(position).getScannerData());
                    updateListViewForDelete(DeleteAction.DELETE_ACTION_CHOOSE, mListData.get
                            (position).getScannerData());
                }
            });
            normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            normalDialog.show();
        } else {
            // do nothing
        }

    }

    /**
     * 删除操作执行后，触发刷新显示当前Activity的ListView
     */
    private void updateListViewForDelete(DeleteAction action, String barcode) {
        switch (action) {
            case DELETE_ACTION_F2: {
                // 现象：删除后，新增扫码ID跳数

                break;
            }
            case DELETE_ACTION_CHOOSE: {
                // 现象：删除中间记录后，ID不会更新
                break;
            }
        }

        if (mListData != null) {
            for (int index = 0; index < mListData.size(); index++) {
                if (!TextUtils.isEmpty(barcode) && barcode.equals(mListData.get(index)
                        .getScannerData())) {
                    mListData.remove(mListData.get(index));
                    mScannerBaseAdatper.notifyDataSetChanged();

                    break;
                } else {
                    // do nothing
                }
            }
        }


        if (!mListView.isStackFromBottom()) {
            mListView.setStackFromBottom(true);
        }
        mListView.setStackFromBottom(false);
    }

    public enum DeleteAction {
        DELETE_ACTION_F2, DELETE_ACTION_CHOOSE
    }

}
