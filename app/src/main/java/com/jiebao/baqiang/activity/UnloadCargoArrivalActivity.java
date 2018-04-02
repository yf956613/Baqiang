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
import com.jiebao.baqiang.adapter.FilterListener;
import com.jiebao.baqiang.adapter.TestTipsAdatper;
import com.jiebao.baqiang.custView.CouldDeleteListView;
import com.jiebao.baqiang.data.arrival.UnloadArrivalFileContent;
import com.jiebao.baqiang.data.bean.CommonDbHelperToUploadFile;
import com.jiebao.baqiang.data.bean.CommonScannerBaseAdapter;
import com.jiebao.baqiang.data.bean.CommonScannerListViewBean;
import com.jiebao.baqiang.data.bean.FileContentHelper;
import com.jiebao.baqiang.data.bean.IDbHelperToUploadFileCallback;
import com.jiebao.baqiang.data.bean.IFileContentBean;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.db.SalesServiceDBHelper;
import com.jiebao.baqiang.data.db.VehicleInfoDBHelper;
import com.jiebao.baqiang.data.db.XcdjDBHelper;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.scan.ScanHelper;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.NetworkUtils;
import com.jiebao.baqiang.util.SharedUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.DbManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 卸车到件
 */

public class UnloadCargoArrivalActivity extends
        BaseActivityWithTitleAndNumber implements View
        .OnClickListener, CouldDeleteListView.DelButtonClickListener {
    private static final String TAG = UnloadCargoArrivalActivity.class
            .getSimpleName();

    private AutoCompleteTextView mTvVehicleId, mTvPreviousStation;
    private Button mBtnSure, mBtnCancel;
    private CouldDeleteListView mListView;
    private EditText mEtDeliveryNumber;

    // 车牌识别号信息
    private List<String> mVehicleInfo;
    // 车辆信息数据适配器
    private TestTipsAdatper mVehicleInfoAdapter;

    // 上一站网点信息
    private List<String> mPreviousStationInfo;
    // 上一站快速提示数据适配器
    private TestTipsAdatper mPreviousStationAdapter;

    // 待插入数据库内容
    private UnloadArrivalFileContent mUnloadArrivalFileContent;
    // 用于更新ListView界面数据，复用发件功能
    private List<CommonScannerListViewBean> mListData;
    private CommonScannerBaseAdapter mScannerBaseAdatper;

    // 此处作为全局扫描次数的记录，用于更新ListView的ID
    private int mScanCount;
    private Vibrator mDeviceVibrator;
    private boolean mIsScanRunning = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ScanHelper.getInstance().barcodeManager.Barcode_Stop();
    }

    @Override
    public void initView() {
        setContent(R.layout.activity_unload_shipment_arrival);
        setHeaderLeftViewText(getString(R.string.main_check));

        prepareDataForView();
    }

    @Override
    public void initData() {
        ScanHelper.getInstance().barcodeManager.setScanTime(Constant
                .TIME_SCAN_DELAY);

        mDeviceVibrator = (Vibrator) this.getSystemService(this
                .VIBRATOR_SERVICE);

        mTvVehicleId = UnloadCargoArrivalActivity.this.findViewById(R.id
                .tv_car_code);
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
                    LogUtil.trace("mTvVehicleId no hasFocus");
                }
            }
        });
        mTvVehicleId.setOnItemClickListener(new AdapterView
                .OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int
                    position, long id) {
                // 一旦选定上一站，则解析网点编号，更新ShipmentFileContent实体内容
                String vehicleId = mTvVehicleId.getText().toString();
                LogUtil.d(TAG, "serverID:" + vehicleId);
                mUnloadArrivalFileContent.setVehicleID(vehicleId);
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

        mTvPreviousStation = UnloadCargoArrivalActivity.this.findViewById(R
                .id.tv_before_station);
        mTvPreviousStation.setAdapter(mPreviousStationAdapter);
        // 监听EditText是否获取焦点
        mTvPreviousStation.setOnFocusChangeListener(new View
                .OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 如果当前内容为空，则提示；同时，编辑时自动提示
                    if (TextUtils.isEmpty(mTvPreviousStation.getText())) {
                        mTvPreviousStation.showDropDown();
                    }
                } else {
                    LogUtil.trace("mTvPreviousStation no hasFocus");
                }
            }
        });
        mTvPreviousStation.setOnItemClickListener(new AdapterView
                .OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int
                    position, long id) {
                // 一旦选定上一站，则解析网点编号，更新ShipmentFileContent实体内容
                String serverName = mTvPreviousStation.getText().toString();
                String serverID = SalesServiceDBHelper.getServerIdFromName
                        (serverName);
                if (!TextUtils.isEmpty(serverID)) {
                    // 更新上一站网点编号
                    mUnloadArrivalFileContent.setPreviousStation(serverID);
                }
            }
        });
        mTvPreviousStation.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DEL: {
                        mTvPreviousStation.setText("", true);
                    }
                }
                return false;
            }
        });

        mEtDeliveryNumber = UnloadCargoArrivalActivity.this.findViewById(R.id
                .et_shipment_number);

        mBtnSure = UnloadCargoArrivalActivity.this.findViewById(R.id
                .btn_ensure);
        mBtnCancel = UnloadCargoArrivalActivity.this.findViewById(R.id
                .btn_back);
        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mListView = UnloadCargoArrivalActivity.this.findViewById(R.id
                .list_view_scan_data);
        mListView.setDelButtonClickListener(UnloadCargoArrivalActivity.this);
        mListView.setAdapter(mScannerBaseAdatper);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case Constant.SCAN_KEY_CODE: {
                if (!VehicleInfoDBHelper.checkVehicleInfo(mTvVehicleId
                        .getText().toString())) {
                    Toast.makeText(UnloadCargoArrivalActivity.this,
                            "车辆码信息异常", Toast
                            .LENGTH_SHORT).show();

                    mDeviceVibrator.vibrate(1000);
                    return true;
                } else if (!SalesServiceDBHelper.checkServerInfo
                        (mTvPreviousStation.getText()
                        .toString())) {
                    Toast.makeText(UnloadCargoArrivalActivity.this,
                            "上一站网点信息异常", Toast
                            .LENGTH_SHORT).show();

                    mDeviceVibrator.vibrate(1000);
                    return true;
                } else {
                    LogUtil.trace("mIsScanRunning:" + mIsScanRunning);
                    if (!mIsScanRunning) {
                        // 没有扫码，发出一次扫码广播
                        Intent intent = new Intent();
                        intent.setAction("com.jb.action.F4key");
                        intent.putExtra("F4key", "down");
                        UnloadCargoArrivalActivity.this.sendBroadcast(intent);

                        mIsScanRunning = true;
                    } else {
                        // do nothing
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
        if (TextUtils.isEmpty(barcode)) {
            return;
        }

        if (XcdjDBHelper.isExistCurrentBarcode(barcode)) {
            Toast.makeText(UnloadCargoArrivalActivity.this, "运单号已存在", Toast
                    .LENGTH_SHORT).show();
            mDeviceVibrator.vibrate(1000);

            mIsScanRunning = true;
            triggerForScanner();

            return;
        }

        boolean isInsertSuccess = insertForScanner(barcode);
        LogUtil.trace("isInsertSuccess:" + isInsertSuccess);
        updateUIForScanner(barcode);
        if (isInsertSuccess) {
            increaseOrDecreaseRecords(1);
        }

        triggerForScanner();
        mIsScanRunning = true;
    }

    @Override
    protected void timeout(long timeout) {
        super.timeout(timeout);

        LogUtil.trace("timeout:" + timeout);
        mIsScanRunning = false;
    }

    @Override
    public void clickHappend(int position) {
        LogUtil.trace("position:" + position);

        UnloadArrivalFileContent barcode = (UnloadArrivalFileContent)
                mListData.get(position)
                .getScannerBean();
        // 此处ListView中数据是有ID值的
        if (XcdjDBHelper.isRecordUpload(barcode.getId())) {
            Toast.makeText(UnloadCargoArrivalActivity.this, "当前记录已上传，不能删除",
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        deleteChooseRecord(barcode, position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ensure: {
                uploadListViewDataToServer();

                break;
            }

            case R.id.btn_back: {
                // 返回按键，不上传文件
                LogUtil.trace();
                UnloadCargoArrivalActivity.this.finish();

                LogUtil.trace("All size:" + XcdjDBHelper.findUsableRecords());
                break;
            }
        }
    }

    @Override
    public void syncViewAfterUpload(int updateType) {
        // 刷新UI，更新未上传统计值
        super.syncViewAfterUpload(updateType);

        // F1事件和自动上传事件 触发刷新UI；更新部分仅仅是当前ListView中的记录
        if (mListData != null) {
            for (int index = 0; index < mListData.size(); index++) {
                CommonScannerListViewBean listViewBean = mListData.get(index);

                // origin data
                UnloadArrivalFileContent barcode = (UnloadArrivalFileContent)
                        listViewBean
                        .getScannerBean();
                // 刷新ListView 中的JavaBean，从数据库取，做一个替换操作
                UnloadArrivalFileContent bean = XcdjDBHelper.getNewInRecord
                        (barcode
                        .getShipmentNumber(), barcode.getScanDate());

                listViewBean.setScannerBean(bean);
            }

            mScannerBaseAdatper.notifyDataSetChanged();

            if (!mListView.isStackFromBottom()) {
                mListView.setStackFromBottom(true);
            }
            mListView.setStackFromBottom(false);
        }
    }

    private void prepareDataForView() {
        mVehicleInfo = VehicleInfoDBHelper.getAllVehicleID();
        mVehicleInfoAdapter = new TestTipsAdatper(UnloadCargoArrivalActivity
                .this, mVehicleInfo,
                new FilterListener() {

                    @Override
                    public void getFilterData(List<String> list) {
                        if (list != null) {
                            if (list.size() == 1) {
                                mTvVehicleId.dismissDropDown();
                                mTvVehicleId.setText(list.get(0), false);

                                mUnloadArrivalFileContent.setVehicleID(list
                                        .get(0));

                                Editable spannable = mTvVehicleId.getText();
                                Selection.setSelection(spannable, spannable
                                        .length());
                            } else {
                                // do nothing
                            }
                        } else {
                            // do nothing
                        }
                    }
                });

        mPreviousStationInfo = SalesServiceDBHelper.getAllSalesServiceData();
        mPreviousStationAdapter = new TestTipsAdatper
                (UnloadCargoArrivalActivity.this,
                mPreviousStationInfo, new FilterListener() {

            @Override
            public void getFilterData(List<String> list) {
                if (list != null) {
                    if (list.size() == 1) {
                        String[] arr = list.get(0).toString().split("  ");
                        mTvPreviousStation.dismissDropDown();

                        if (arr != null) {
                            if (arr.length >= 2) {
                                mTvPreviousStation.setText(arr[1], false);
                                mUnloadArrivalFileContent.setPreviousStation
                                        (arr[0]);
                            }
                        } else {
                            mTvPreviousStation.setText(list.get(0).toString()
                                    , false);
                        }

                        Editable spannable = mTvPreviousStation.getText();
                        Selection.setSelection(spannable, spannable.length());
                    } else {
                        // do nothing
                    }
                } else {
                    // do nothing
                }
            }
        });

        mUnloadArrivalFileContent = FileContentHelper
                .getUnloadArrivalFileContent();
        LogUtil.trace("mUnloadArrivalFileContent:" +
                mUnloadArrivalFileContent.toString());

        mListData = new ArrayList<>();
        mScannerBaseAdatper = new CommonScannerBaseAdapter
                (UnloadCargoArrivalActivity.this,
                mListData);
    }

    /**
     * 扫码，录入数据库
     *
     * @param barcode
     */
    private boolean insertForScanner(String barcode) {
        Date scanDate = new Date();

        mUnloadArrivalFileContent.setScanDate(scanDate);
        mUnloadArrivalFileContent.setShipmentNumber(barcode);
        mUnloadArrivalFileContent.setOperateDate(TextStringUtil.getFormatTime
                ());
        // 该结果从 扫码时间 转化得来
        mUnloadArrivalFileContent.setOperateDate(new SimpleDateFormat
                ("yyyyMMdd").format(scanDate));
        return XcdjDBHelper.insertDataToDatabase(mUnloadArrivalFileContent);
    }

    /**
     * 扫码，录入后更新 EditText ListView
     * <p>
     * ListView中数据 和 数据库中的数据绑定，并更新数据
     * <p>
     * 目的：让ZCFajianFileContent bean获取插入数据库的ID值，作为唯一搜索内容
     *
     * @param barcode
     */
    private void updateUIForScanner(String barcode) {
        mEtDeliveryNumber.setText(barcode);

        // 把 instert 的 record 从数据库中取出来，该record内容是更新后的内容
        UnloadArrivalFileContent bean = XcdjDBHelper.getNewInRecord
                (mUnloadArrivalFileContent
                .getShipmentNumber(), mUnloadArrivalFileContent.getScanDate());
        CommonScannerListViewBean mCommonScannerListViewBean = new
                CommonScannerListViewBean();
        mCommonScannerListViewBean.setId(++mScanCount);
        mCommonScannerListViewBean.setScannerBean(bean);

        // 添加到最前面
        mListData.add(0, mCommonScannerListViewBean);
        mScannerBaseAdatper.notifyDataSetChanged();

        if (!mListView.isStackFromBottom()) {
            mListView.setStackFromBottom(true);
        }
        mListView.setStackFromBottom(false);
    }

    /**
     * 增加或减少未上传记录数，记录数存储在SP中
     *
     * @param actionType 0：减少；1：增加
     */
    private void increaseOrDecreaseRecords(int actionType) {
        int unloadRecords = SharedUtil.getInt(UnloadCargoArrivalActivity
                .this, Constant
                .PREFERENCE_NAME_XCDJ);
        LogUtil.trace("unloadRecords:" + unloadRecords);

        switch (actionType) {
            case 0: {
                --unloadRecords;

                break;
            }
            case 1: {
                ++unloadRecords;
                break;
            }
        }

        SharedUtil.putInt(UnloadCargoArrivalActivity.this, Constant
                        .PREFERENCE_NAME_XCDJ,
                unloadRecords);
        setHeaderRightViewText("未上传：" + searchUnloadDataForUpdate(Constant
                .SYNC_UNLOAD_DATA_TYPE_XCDJ));
    }

    /**
     * 通过滑动删除指定记录
     *
     * @param bean
     */
    private void deleteChooseRecord(final UnloadArrivalFileContent bean,
                                    final int position) {
        if (mListData != null && mListData.size() != 0) {
            LogUtil.trace("待删除的内容：" + bean.toString());

            final AlertDialog.Builder normalDialog = new AlertDialog.Builder
                    (UnloadCargoArrivalActivity
                            .this);
            normalDialog.setTitle("提示");
            normalDialog.setCancelable(false);
            normalDialog.setMessage("是否删除：" + bean.getShipmentNumber() + " " +
                    "记录？");
            normalDialog.setPositiveButton("确定", new DialogInterface
                    .OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int barcodeID = bean.getId();
                    if (XcdjDBHelper.isRecordUpload(barcodeID)) {
                        Toast.makeText(UnloadCargoArrivalActivity.this,
                                "当前记录已上传，不能删除", Toast
                                .LENGTH_SHORT).show();
                        return;
                    }

                    // 根据ID值，设置record为不可用
                    XcdjDBHelper.deleteFindedBean(barcodeID);
                    // 刷新UI，ListView
                    updateListViewForDelete(ZhuangcheActivity.DeleteAction
                                    .DELETE_ACTION_CHOOSE,
                            position);

                    increaseOrDecreaseRecords(0);
                }
            });
            normalDialog.setNegativeButton("取消", new DialogInterface
                    .OnClickListener() {

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
    private void updateListViewForDelete(ZhuangcheActivity.DeleteAction
                                                 action, int position) {
        switch (action) {
            case DELETE_ACTION_F2: {
                if (mListData != null) {
                    --mScanCount;

                    mListData.remove(mListData.get(position));
                    mScannerBaseAdatper.notifyDataSetChanged();
                } else {
                    // do nothing
                }
                break;
            }

            case DELETE_ACTION_CHOOSE: {
                if (mListData != null) {
                    mListData.remove(mListData.get(position));

                    for (int index = position - 1; index >= 0; index--) {
                        int id = mListData.get(index).getId();
                        mListData.get(index).setId(--id);
                    }

                    --mScanCount;
                    mScannerBaseAdatper.notifyDataSetChanged();
                } else {
                    // do nothing
                }
                break;
            }
        }

        if (!mListView.isStackFromBottom()) {
            mListView.setStackFromBottom(true);
        }
        mListView.setStackFromBottom(false);
    }

    /**
     * F2按键触发删除最新扫码记录，ListView最上一条记录
     */
    private void deleteLastestRecord() {
        if (mListData == null || mListData.size() == 0) {
            return;
        }

        // ListView中最近录入的record
        final UnloadArrivalFileContent barcode = (UnloadArrivalFileContent)
                mListData.get(0)
                .getScannerBean();
        final int barcodeID = barcode.getId();

        if (XcdjDBHelper.isRecordUpload(barcodeID)) {
            Toast.makeText(UnloadCargoArrivalActivity.this, "当前记录已上传，不能删除",
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (mListData != null && mListData.size() != 0) {
            LogUtil.trace("mListData.size:" + mListData.size() + "; " +
                    "barcode:" + barcode
                    .getShipmentNumber());

            final AlertDialog.Builder normalDialog = new AlertDialog.Builder
                    (UnloadCargoArrivalActivity
                            .this);
            normalDialog.setTitle("提示");
            normalDialog.setCancelable(false);
            normalDialog.setMessage("是否删除：" + barcode.getShipmentNumber() + "" +
                    " 记录？");
            normalDialog.setPositiveButton("确定", new DialogInterface
                    .OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (XcdjDBHelper.isRecordUpload(barcodeID)) {
                        Toast.makeText(UnloadCargoArrivalActivity.this,
                                "当前记录已上传，不能删除", Toast
                                .LENGTH_SHORT).show();
                        return;
                    }

                    XcdjDBHelper.deleteFindedBean(barcodeID);
                    updateListViewForDelete(ZhuangcheActivity.DeleteAction
                            .DELETE_ACTION_F2, 0);

                    increaseOrDecreaseRecords(0);
                }
            });
            normalDialog.setNegativeButton("取消", new DialogInterface
                    .OnClickListener() {

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
     * 将ListView当前录入记录上传服务器，根据ID上传
     */
    private void uploadListViewDataToServer() {
        if (mListData == null) {
            return;
        } else if (mListData != null && mListData.size() == 0) {
            return;
        }

        if (!NetworkUtils.isNetworkConnected(UnloadCargoArrivalActivity
                .this)) {
            Toast.makeText(UnloadCargoArrivalActivity.this, "网络不可用，请检查网络",
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        showLoadinDialog();

        DbManager db = BQDataBaseHelper.getDb();
        List<UnloadArrivalFileContent> list = null;
        if (mListData != null && mListData.size() != 0) {
            ArrayList<IFileContentBean> uploadContent = new ArrayList<>();
            // 根据当前ListView中的数据，上传记录
            for (int index = 0; index < mListData.size(); index++) {
                // 1. 循环找到 item 的 ID
                uploadContent.add(mListData.get(index).getScannerBean());
            }

            new CommonDbHelperToUploadFile<UnloadArrivalFileContent>()
                    .setCallbackListener(new IDbHelperToUploadFileCallback() {

                @Override
                public boolean onSuccess(String s) {
                    // 文件上传存在延时
                    Toast.makeText(UnloadCargoArrivalActivity.this, "文件上传成功",
                            Toast.LENGTH_SHORT)
                            .show();
                    setHeaderRightViewText("未上传：" + searchUnloadDataForUpdate
                            (Constant
                            .SYNC_UNLOAD_DATA_TYPE_XCDJ));
                    return true;
                }

                @Override
                public boolean onError(Throwable throwable, boolean b) {
                    return false;
                }

                @Override
                public boolean onFinish() {
                    closeLoadinDialog();
                    UnloadCargoArrivalActivity.this.finish();

                    return false;
                }
            }).redoUploadRecords(uploadContent);
        } else {
            // do nothing
        }
    }

}
