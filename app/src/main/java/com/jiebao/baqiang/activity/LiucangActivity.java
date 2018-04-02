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
import com.jiebao.baqiang.data.bean.CommonScannerBaseAdapter;
import com.jiebao.baqiang.data.bean.CommonScannerListViewBean;
import com.jiebao.baqiang.data.bean.FileContentHelper;
import com.jiebao.baqiang.data.db.LiucangDBHelper;
import com.jiebao.baqiang.data.db.StayHouseReasonDBHelper;
import com.jiebao.baqiang.data.stay.StayHouseFileContent;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.scan.ScanHelper;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LiucangActivity extends BaseActivityWithTitleAndNumber
        implements View
        .OnClickListener, CouldDeleteListView.DelButtonClickListener {
    private static final String TAG = "LiucangActivity";

    private AutoCompleteTextView mTvStayHouseReason;
    private EditText mEtShipmentNumber;
    private Button mBtnSure, mBtnCancel;
    private CouldDeleteListView mListView;

    // 留仓原因
    private List<String> mStayHouseReason;
    // 用在View上的Adapter
    private TestTipsAdatper mReasonData;

    // 插入数据库中的一行数据
    private StayHouseFileContent mStayHouseFileContent;

    // 用于更新ListView界面数据
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
        setContent(R.layout.liucang);
        setHeaderLeftViewText(getString(R.string.main_import));

        prepareDataForView();
    }

    @Override
    public void initData() {
        ScanHelper.getInstance().barcodeManager.setScanTime(Constant
                .TIME_SCAN_DELAY);

        mDeviceVibrator = (Vibrator) this.getSystemService(this
                .VIBRATOR_SERVICE);

        mTvStayHouseReason = LiucangActivity.this.findViewById(R.id
                .tv_stay_reason);
        mTvStayHouseReason.setAdapter(mReasonData);
        // 监听EditText是否获取焦点
        mTvStayHouseReason.setOnFocusChangeListener(new View
                .OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 如果当前内容为空，则提示；同时，编辑时自动提示
                    if (TextUtils.isEmpty(mTvStayHouseReason.getText())) {
                        mTvStayHouseReason.showDropDown();
                    }
                } else {
                    LogUtil.trace("mTvNextStation no hasFocus");
                }
            }
        });
        mTvStayHouseReason.setOnItemClickListener(new AdapterView
                .OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int
                    position, long id) {
                String stayHouseReason = mTvStayHouseReason.getText()
                        .toString();
                String reasonID = StayHouseReasonDBHelper.getReasonIdFromName
                        (stayHouseReason);
                if (!TextUtils.isEmpty(reasonID)) {
                    // 更新下一站网点编号
                    mStayHouseFileContent.setStayReason(reasonID);
                }
            }
        });
        mTvStayHouseReason.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DEL: {
                        mTvStayHouseReason.setText("", true);
                    }
                }
                return false;
            }
        });

        mEtShipmentNumber = LiucangActivity.this.findViewById(R.id
                .et_shipment_number);

        mBtnSure = LiucangActivity.this.findViewById(R.id.btn_ensure);
        mBtnCancel = LiucangActivity.this.findViewById(R.id.btn_back);
        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mListView = LiucangActivity.this.findViewById(R.id.list_view_scan_data);
        mListView.setDelButtonClickListener(LiucangActivity.this);
        mListView.setAdapter(mScannerBaseAdatper);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        LogUtil.trace("keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.SCAN_KEY_CODE: {
                if (!StayHouseReasonDBHelper.checkCurrentReason
                        (mTvStayHouseReason.getText()
                                .toString())) {
                    Toast.makeText(LiucangActivity.this, "留仓原因出错", Toast
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
                        LiucangActivity.this.sendBroadcast(intent);
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
        if (TextUtils.isEmpty(barcode)) {
            return;
        } else if (TextStringUtil.isStringFormatCorrect(barcode)) {
            if (LiucangDBHelper.isExistCurrentBarcode(barcode)) {
                Toast.makeText(LiucangActivity.this, "运单号已存在", Toast
                        .LENGTH_SHORT).show();
                mDeviceVibrator.vibrate(1000);

                mIsScanRunning = true;
                triggerForScanner();

                return;
            } else if (!StayHouseReasonDBHelper.checkCurrentReason
                    (mTvStayHouseReason.getText()
                            .toString())) {
                Toast.makeText(LiucangActivity.this, "留仓原因出错", Toast
                        .LENGTH_SHORT).show();
                mDeviceVibrator.vibrate(1000);

                mIsScanRunning = true;
                triggerForScanner();

                return;
            } else {
                boolean isInsertSuccess = insertForScanner(barcode);
                LogUtil.trace("isInsertSuccess:" + isInsertSuccess);
                if (isInsertSuccess) {
                    updateUIForScanner(barcode);
                    increaseOrDecreaseRecords(1);
                    mDeviceVibrator.vibrate(1000);
                } else {
                    // do nothing
                }

                triggerForScanner();
                mIsScanRunning = true;
            }
        } else {
            mIsScanRunning = true;
            triggerForScanner();
        }

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

        StayHouseFileContent barcode = (StayHouseFileContent) mListData.get
                (position)
                .getScannerBean();
        // 此处ListView中数据是有ID值的
        if (LiucangDBHelper.isRecordUpload(barcode.getId())) {
            Toast.makeText(LiucangActivity.this, "当前记录已上传，不能删除", Toast
                    .LENGTH_SHORT).show();
            return;
        }

        deleteChooseRecord(barcode, position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ensure: {
                // uploadListViewDataToServer();
                // 存储手动输入的运单号
                storeManualBarcode(mEtShipmentNumber.getText().toString()
                        .trim());
                break;
            }

            case R.id.btn_back: {
                LiucangActivity.this.finish();
                LogUtil.trace("All size:" + LiucangDBHelper.getAllUsedRecords
                        ());
                break;
            }
        }
    }


    @Override
    public void syncViewAfterUpload(int updateType) {
        super.syncViewAfterUpload(updateType);

        // F1事件和自动上传事件 触发刷新UI；更新部分仅仅是当前ListView中的记录
        if (mListData != null) {
            LogUtil.trace("size:" + mListData.size());

            for (int index = 0; index < mListData.size(); index++) {
                CommonScannerListViewBean listViewBean = mListData.get(index);

                // origin data
                StayHouseFileContent barcode = (StayHouseFileContent)
                        listViewBean.getScannerBean();
                // 刷新ListView 中的JavaBean，从数据库取，做一个替换操作
                StayHouseFileContent bean = LiucangDBHelper.getNewInRecord
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
        mStayHouseReason = StayHouseReasonDBHelper.getReasonFromDB();
        mReasonData = new TestTipsAdatper(LiucangActivity.this,
                mStayHouseReason, new
                FilterListener() {

                    @Override
                    public void getFilterData(List<String> list) {
                        if (list != null) {
                            if (list.size() == 1) {
                                String[] arr = list.get(0).toString().split("" +
                                        "  ");
                                mTvStayHouseReason.dismissDropDown();

                                if (arr != null) {
                                    if (arr.length >= 2) {
                                        mTvStayHouseReason.setText(arr[1],
                                                false);
                                        mStayHouseFileContent.setStayReason
                                                (arr[0]);
                                    }
                                } else {
                                    mTvStayHouseReason.setText(list.get(0)
                                            .toString(), false);
                                }

                                Editable spannable = mTvStayHouseReason
                                        .getText();
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

        mStayHouseFileContent = FileContentHelper.getStayHouseFileContent();

        mListData = new ArrayList<>();
        mScannerBaseAdatper = new CommonScannerBaseAdapter(LiucangActivity
                .this, mListData);
    }

    /**
     * 扫码，录入数据库
     *
     * @param barcode
     */
    private boolean insertForScanner(String barcode) {
        Date scanDate = new Date();

        mStayHouseFileContent.setShipmentType("");
        mStayHouseFileContent.setScanDate(scanDate);
        mStayHouseFileContent.setShipmentNumber(barcode);
        // 该结果从 扫码时间 转化得来
        mStayHouseFileContent.setOperateDate(new SimpleDateFormat("yyyyMMdd")
                .format(scanDate));
        return LiucangDBHelper.insertDataToDatabase(mStayHouseFileContent);
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
        mEtShipmentNumber.setText(barcode);

        // 把 instert 的 record 从数据库中取出来，该record内容是更新后的内容
        StayHouseFileContent bean = LiucangDBHelper.getNewInRecord
                (mStayHouseFileContent
                        .getShipmentNumber(), mStayHouseFileContent
                        .getScanDate());
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
        int unloadRecords = SharedUtil.getInt(LiucangActivity.this, Constant
                .PREFERENCE_NAME_LCJ);
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

        SharedUtil.putInt(LiucangActivity.this, Constant.PREFERENCE_NAME_LCJ,
                unloadRecords);
        setHeaderRightViewText("未上传：" + searchUnloadDataForUpdate(Constant
                .SYNC_UNLOAD_DATA_TYPE_LCJ));
    }

    /**
     * 通过滑动删除指定记录
     *
     * @param bean
     */
    private void deleteChooseRecord(final StayHouseFileContent bean, final
    int position) {
        if (mListData != null && mListData.size() != 0) {
            LogUtil.trace("待删除的内容：" + bean.toString());

            final AlertDialog.Builder normalDialog = new AlertDialog.Builder
                    (LiucangActivity
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
                    if (LiucangDBHelper.isRecordUpload(barcodeID)) {
                        Toast.makeText(LiucangActivity.this, "当前记录已上传，不能删除",
                                Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    // 根据ID值，设置record为不可用
                    LiucangDBHelper.deleteFindedBean(barcodeID);
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
        final StayHouseFileContent barcode = (StayHouseFileContent) mListData
                .get(0)
                .getScannerBean();
        final int barcodeID = barcode.getId();

        if (LiucangDBHelper.isRecordUpload(barcodeID)) {
            Toast.makeText(LiucangActivity.this, "当前记录已上传，不能删除", Toast
                    .LENGTH_SHORT).show();
            return;
        }

        if (mListData != null && mListData.size() != 0) {
            LogUtil.trace("mListData.size:" + mListData.size() + "; " +
                    "barcode:" + barcode
                    .getShipmentNumber());

            final AlertDialog.Builder normalDialog = new AlertDialog.Builder
                    (LiucangActivity
                            .this);
            normalDialog.setTitle("提示");
            normalDialog.setCancelable(false);
            normalDialog.setMessage("是否删除：" + barcode.getShipmentNumber() + "" +
                    " 记录？");
            normalDialog.setPositiveButton("确定", new DialogInterface
                    .OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (LiucangDBHelper.isRecordUpload(barcodeID)) {
                        Toast.makeText(LiucangActivity.this, "当前记录已上传，不能删除",
                                Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    LiucangDBHelper.deleteFindedBean(barcodeID);
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
     * 存入手动输入的运单号
     *
     * @param barcode
     * @return
     */
    private boolean storeManualBarcode(String barcode) {
        if (TextUtils.isEmpty(barcode)) {
            return false;
        } else if (TextStringUtil.isStringFormatCorrect(barcode)) {
            if (LiucangDBHelper.isExistCurrentBarcode(barcode)) {
                Toast.makeText(LiucangActivity.this, "运单号已存在", Toast
                        .LENGTH_SHORT).show();
                mDeviceVibrator.vibrate(1000);

                return false;
            } else if (!StayHouseReasonDBHelper.checkCurrentReason
                    (mTvStayHouseReason.getText()
                            .toString())) {
                Toast.makeText(LiucangActivity.this, "留仓原因出错", Toast
                        .LENGTH_SHORT).show();
                mDeviceVibrator.vibrate(1000);

                return false;
            } else {
                boolean isInsertSuccess = insertForScanner(barcode);
                LogUtil.trace("isInsertSuccess:" + isInsertSuccess);

                if (isInsertSuccess) {
                    updateUIForScanner(barcode);
                    increaseOrDecreaseRecords(1);
                } else {
                    // do nothing
                }
            }
        } else {
            // do nothing
        }


        return true;
    }
}
