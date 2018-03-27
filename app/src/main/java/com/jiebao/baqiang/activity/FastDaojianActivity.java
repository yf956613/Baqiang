package com.jiebao.baqiang.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.custView.CouldDeleteListView;
import com.jiebao.baqiang.data.arrival.CargoArrivalFileContent;
import com.jiebao.baqiang.data.bean.CommonDbHelperToUploadFile;
import com.jiebao.baqiang.data.bean.CommonScannerBaseAdapter;
import com.jiebao.baqiang.data.bean.CommonScannerListViewBean;
import com.jiebao.baqiang.data.bean.FileContentHelper;
import com.jiebao.baqiang.data.bean.IDbHelperToUploadFileCallback;
import com.jiebao.baqiang.data.bean.IFileContentBean;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.db.DaojianDBHelper;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.scan.ScanHelper;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.NetworkUtils;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.DbManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 中心站点，快速扫描录单
 */

public class FastDaojianActivity extends BaseActivityWithTitleAndNumber
        implements View.OnClickListener, CouldDeleteListView
        .DelButtonClickListener {
    private static final String TAG = FastDaojianActivity.class.getSimpleName();

    private CouldDeleteListView mListView;
    private EditText mEtDeliveryNumber;

    private CargoArrivalFileContent mCargoArrivalFileContent;
    private List<CommonScannerListViewBean> mListData;
    private CommonScannerBaseAdapter mScannerBaseAdatper;
    private Button mBtnSure, mBtnCancel;

    private int mScanCount;
    private Vibrator mDeviceVibrator;
    private boolean mIsScanRunning = false;

    @Override
    public void initView() {
        setContent(R.layout.activity_fast_daojian);
        setHeaderLeftViewText("快速到件");

        prepareDataForView();
    }

    @Override
    public void initData() {
        ScanHelper.getInstance().barcodeManager.setScanTime(Constant
                .TIME_SCAN_DELAY);

        mDeviceVibrator = (Vibrator) this.getSystemService(this
                .VIBRATOR_SERVICE);

        mEtDeliveryNumber = FastDaojianActivity.this.findViewById(R.id
                .et_shipment_number);

        mBtnSure = FastDaojianActivity.this.findViewById(R.id.btn_ensure);
        mBtnCancel = FastDaojianActivity.this.findViewById(R.id.btn_back);
        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mListView = FastDaojianActivity.this.findViewById(R.id
                .list_view_scan_data);
        mListView.setDelButtonClickListener(FastDaojianActivity.this);
        mListView.setAdapter(mScannerBaseAdatper);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case Constant.SCAN_KEY_CODE: {
                LogUtil.trace("mIsScanRunning:" + mIsScanRunning);

                if (!mIsScanRunning) {
                    // 没有扫码，发出一次扫码广播
                    Intent intent = new Intent();
                    intent.setAction("com.jb.action.F4key");
                    intent.putExtra("F4key", "down");
                    FastDaojianActivity.this.sendBroadcast(intent);
                    LogUtil.trace("3: mIsScanRunning=" + mIsScanRunning);
                    mIsScanRunning = true;
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

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void fillCode(String barcode) {
        LogUtil.d(TAG, "barcode:" + barcode);
        if (TextUtils.isEmpty(barcode)) {
            return;
        }

        if (DaojianDBHelper.isExistCurrentBarcode(barcode)) {
            Toast.makeText(FastDaojianActivity.this, "运单号已存在", Toast
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ensure: {
                uploadListViewDataToServer();
                break;
            }

            case R.id.btn_back: {
                FastDaojianActivity.this.finish();
                LogUtil.trace("All size:" + DaojianDBHelper.findUsableRecords
                        ());

                break;
            }
        }
    }

    @Override
    public void clickHappend(int position) {
        LogUtil.trace("position:" + position);

        CargoArrivalFileContent barcode = (CargoArrivalFileContent) mListData
                .get(position)
                .getScannerBean();
        // 此处ListView中数据是有ID值的
        if (DaojianDBHelper.isRecordUpload(barcode.getId())) {
            Toast.makeText(FastDaojianActivity.this, "当前记录已上传，不能删除", Toast
                    .LENGTH_SHORT).show();
            return;
        }

        deleteChooseRecord(barcode, position);
    }

    @Override
    public void syncViewAfterUpload(int updateType) {
        super.syncViewAfterUpload(updateType);

        // F1事件和自动上传事件 触发刷新UI；更新部分仅仅是当前ListView中的记录
        if (mListData != null) {
            for (int index = 0; index < mListData.size(); index++) {
                CommonScannerListViewBean listViewBean = mListData.get(index);

                // origin data
                CargoArrivalFileContent barcode = (CargoArrivalFileContent)
                        listViewBean.getScannerBean();
                // 刷新ListView 中的JavaBean，从数据库取，做一个替换操作
                CargoArrivalFileContent bean = DaojianDBHelper.getNewInRecord
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
        mCargoArrivalFileContent = FileContentHelper
                .getCargoArrivalFileContent();

        mListData = new ArrayList<>();
        mScannerBaseAdatper = new CommonScannerBaseAdapter(FastDaojianActivity
                .this, mListData);
    }

    /**
     * 扫码，录入数据库
     *
     * @param barcode
     */
    private boolean insertForScanner(String barcode) {
        Date scanDate = new Date();

        // 快速到件，设置上一站编码为""
        mCargoArrivalFileContent.setPreviousStation("");
        mCargoArrivalFileContent.setScanDate(scanDate);
        mCargoArrivalFileContent.setShipmentNumber(barcode);
        // 该结果从 扫码时间 转化得来
        mCargoArrivalFileContent.setOperateDate(new SimpleDateFormat
                ("yyyMMdd").format(scanDate));
        return DaojianDBHelper.insertDataToDatabase(mCargoArrivalFileContent);
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

        // 把 instert 的 record 从数据库中取出来，该record内容是更新（插入到数据库）后的内容
        CargoArrivalFileContent bean = DaojianDBHelper.getNewInRecord
                (mCargoArrivalFileContent
                        .getShipmentNumber(), mCargoArrivalFileContent
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
        int unloadRecords = SharedUtil.getInt(FastDaojianActivity.this, Constant
                .PREFERENCE_NAME_DJ);
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

        SharedUtil.putInt(FastDaojianActivity.this, Constant.PREFERENCE_NAME_DJ,
                unloadRecords);
        setHeaderRightViewText("未上传：" + searchUnloadDataForUpdate(Constant
                .SYNC_UNLOAD_DATA_TYPE_DJ));
    }

    /**
     * 通过滑动删除指定记录
     *
     * @param bean
     */
    private void deleteChooseRecord(final CargoArrivalFileContent bean, final
    int position) {
        if (mListData != null && mListData.size() != 0) {
            LogUtil.trace("待删除的内容：" + bean.toString());

            final AlertDialog.Builder normalDialog = new AlertDialog.Builder
                    (FastDaojianActivity
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
                    if (DaojianDBHelper.isRecordUpload(barcodeID)) {
                        Toast.makeText(FastDaojianActivity.this, "当前记录已上传，不能删除",
                                Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    // 根据ID值，设置record为不可用
                    DaojianDBHelper.deleteFindedBean(barcodeID);
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
        final CargoArrivalFileContent barcode = (CargoArrivalFileContent)
                mListData.get(0)
                        .getScannerBean();
        final int barcodeID = barcode.getId();

        if (DaojianDBHelper.isRecordUpload(barcodeID)) {
            Toast.makeText(FastDaojianActivity.this, "当前记录已上传，不能删除", Toast
                    .LENGTH_SHORT).show();
            return;
        }

        if (mListData != null && mListData.size() != 0) {
            LogUtil.trace("mListData.size:" + mListData.size() + "; " +
                    "barcode:" + barcode
                    .getShipmentNumber());

            final AlertDialog.Builder normalDialog = new AlertDialog.Builder
                    (FastDaojianActivity
                            .this);
            normalDialog.setTitle("提示");
            normalDialog.setCancelable(false);
            normalDialog.setMessage("是否删除：" + barcode.getShipmentNumber() + "" +
                    " 记录？");
            normalDialog.setPositiveButton("确定", new DialogInterface
                    .OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (DaojianDBHelper.isRecordUpload(barcodeID)) {
                        Toast.makeText(FastDaojianActivity.this, "当前记录已上传，不能删除",
                                Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    DaojianDBHelper.deleteFindedBean(barcodeID);
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

        if (!NetworkUtils.isNetworkConnected(FastDaojianActivity
                .this)) {
            Toast.makeText(FastDaojianActivity.this, "网络不可用，请检查网络", Toast
                    .LENGTH_SHORT).show();
            return;
        }

        showLoadinDialog();

        DbManager db = BQDataBaseHelper.getDb();
        List<CargoArrivalFileContent> list = null;
        if (mListData != null && mListData.size() != 0) {
            ArrayList<IFileContentBean> uploadContent = new ArrayList<>();
            // 根据当前ListView中的数据，上传记录
            for (int index = 0; index < mListData.size(); index++) {
                // 1. 循环找到 item 的 ID
                uploadContent.add(mListData.get(index).getScannerBean());
            }

            new CommonDbHelperToUploadFile<CargoArrivalFileContent>()
                    .setCallbackListener(new IDbHelperToUploadFileCallback() {

                        @Override
                        public boolean onSuccess(String s) {
                            // 文件上传存在延时
                            Toast.makeText(FastDaojianActivity.this,
                                    "文件上传成功", Toast
                                            .LENGTH_SHORT).show();
                            setHeaderRightViewText("未上传：" +
                                    searchUnloadDataForUpdate
                                            (Constant
                                                    .SYNC_UNLOAD_DATA_TYPE_DJ));
                            return true;
                        }

                        @Override
                        public boolean onError(Throwable throwable, boolean b) {
                            return false;
                        }

                        @Override
                        public boolean onFinish() {
                            closeLoadinDialog();
                            FastDaojianActivity.this.finish();

                            return false;
                        }
                    }).redoUploadRecords(uploadContent);
        } else {
            // do nothing
        }
    }
}
