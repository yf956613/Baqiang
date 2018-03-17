package com.jiebao.baqiang.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
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
import com.jiebao.baqiang.custView.CouldDeleteListView;
import com.jiebao.baqiang.data.arrival.CargoArrivalFileContent;
import com.jiebao.baqiang.data.arrival.CargoArrivalFileName;
import com.jiebao.baqiang.data.bean.FajianListViewBean;
import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.updateData.UpdateInterface;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.scan.ScanHelper;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * 到件需要注意的地方：若管理员设置中，到/发件扫描判断开关为开状态，上一站只显示类型为“网点”的项
 */
public class DaojianActivity extends BaseActivityWithTitleAndNumber
        implements View
        .OnClickListener, CouldDeleteListView.DelButtonClickListener {
    private static final String TAG = DaojianActivity.class.getSimpleName();

    private AutoCompleteTextView mTvPreviousStation;
    private Button mBtnSure, mBtnCancel;
    private CouldDeleteListView mListView;
    private EditText mEtDeliveryNumber;

    // 上一站网点信息
    private List<String> mPreviousStationInfo;
    // 上一站快速提示数据适配器
    private ArrayAdapter<String> mPreviousStationAdapter;

    // 待上传文件名
    private CargoArrivalFileName mCargoArrivalFileName;
    // 待插入数据库内容
    private CargoArrivalFileContent mCargoArrivalFileContent;
    private UploadServerFile mUploadServerFile;

    // 用于更新ListView界面数据，复用发件功能
    private List<FajianListViewBean> mListData;
    private FajianAdatper mFajianAdapter;
    // 此处作为全局扫描次数的记录，用于更新ListView的ID
    private int mScanCount;

    private Vibrator mDeviceVibrator;

    // 总倒计时时间为3秒，每1秒回调一次onTick()
    private CountDownTimer mCountDownTimer = new CountDownTimer(Constant.TIME_SCAN_DELAY, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            LogUtil.trace();
        }

        @Override
        public void onFinish() {
            LogUtil.trace();

            ScanHelper.getInstance().barcodeManager.Barcode_Stop();
            // FIXME
            if (mScanThread != null) {
                mScanThread.mHandler.getLooper().quit();
                mScanThread = null;
            }
        }
    };

    private ScanThread mScanThread = /*new ScanThread()*/null;
    private final int MSG_RETURE_RESULT = 1000;

    class ScanThread extends Thread {
        private Looper looper;
        public Handler mHandler = null;

        @Override
        public void run() {
            super.run();

            // 创建子线程的Looper实例
            Looper.prepare();

            // 取出子线程的Looper实例
            looper = Looper.myLooper();

            // 子线程的Handler实例
            mHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_RETURE_RESULT: {
                            // 再次发一次扫码广播
                            Intent intent = new Intent();
                            intent.setAction("com.jb.action.F4key");
                            intent.putExtra("F4key", "down");
                            DaojianActivity.this.sendBroadcast(intent);

                            mCountDownTimer.start();
                            break;
                        }
                    }
                }
            };

            // 不断循环取出线程
            Looper.loop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mScanThread != null) {
            // FIXME
            mScanThread.mHandler.getLooper().quit();
            mScanThread = null;
        }

        ScanHelper.getInstance().barcodeManager.Barcode_Stop();
    }

    @Override
    public void initView() {
        setContent(R.layout.daojian);
        setHeaderLeftViewText(getString(R.string.main_query));
    }

    @Override
    public void initData() {
        prepareDataForView();

        mDeviceVibrator = (Vibrator) this.getSystemService(this
                .VIBRATOR_SERVICE);

        mTvPreviousStation = DaojianActivity.this.findViewById(R.id
                .tv_before_station);
        mTvPreviousStation.setAdapter(mPreviousStationAdapter);
        // 监听AutoCompleteTextView是否获取焦点
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
                String serverID = mTvPreviousStation.getText().toString();
                LogUtil.d(TAG, "serverID:" + serverID);
                String[] arr = serverID.split("  ");
                // 获取上一站网点编号
                mCargoArrivalFileContent.setPreviousStation(arr[0]);
            }
        });
        // AutoCompleteTextView获取到硬件按键事件
        mTvPreviousStation.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                LogUtil.trace("setOnKeyListener:" + keyCode);

                return false;
            }

        });

        mEtDeliveryNumber = DaojianActivity.this.findViewById(R.id
                .et_shipment_number);

        mBtnSure = DaojianActivity.this.findViewById(R.id.btn_ensure);
        mBtnCancel = DaojianActivity.this.findViewById(R.id.btn_back);
        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mListView = DaojianActivity.this.findViewById(R.id.list_view_scan_data);
        mListView.setAdapter(mFajianAdapter);
        mListView.setDelButtonClickListener(DaojianActivity.this);

        // 初次启动时刷新数据
        reQueryUnUploadDataForListView();
    }

    private void prepareDataForView() {
        // 准备上一站网点数据
        mPreviousStationInfo = resolvePreviousStationData();
        mPreviousStationAdapter = new ArrayAdapter<>(DaojianActivity.this, R
                .layout.list_item,
                mPreviousStationInfo);

        // 组装写入文件数据
        mCargoArrivalFileContent = getCargoArrivalFileContent();
        LogUtil.trace("mUnloadArrivalFileContent:" + mCargoArrivalFileContent
                .toString());

        mListData = new ArrayList<>();
        mFajianAdapter = new FajianAdatper(DaojianActivity.this, mListData);
    }

    /**
     * 解析上一站网点信息
     */
    private List<String> resolvePreviousStationData() {
        LogUtil.trace();

        Boolean isOpen = SharedUtil.getBoolean(DaojianActivity.this, Constant
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
     * 初始化时，先构建一个ShipmentFileContent实体
     *
     * @return
     */
    private CargoArrivalFileContent getCargoArrivalFileContent() {
        // TODO 上一站点编号  模拟
        String previousStation = String.valueOf("59406");

        // 扫描时间
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
        // 是否上传状态
        String status = "未上传";
        // 是否可用
        String isUsed = "可用";

        return new CargoArrivalFileContent(previousStation, scanDate,
                goodsType, shipmentType,
                shipmentNumber, scanEmployeeNumber, operateDate, weight,
                status, isUsed);
    }

    /**
     * 从数据库中找出所有未上传记录
     */
    private void reQueryUnUploadDataForListView() {
        DbManager db = BQDataBaseHelper.getDb();

        try {
            // 查询数据库中标识位“未上传”的记录
            List<CargoArrivalFileContent> data = db.selector
                    (CargoArrivalFileContent.class).where
                    ("是否上传", "like", "未上传").and("是否可用", "=", "可用").findAll();
            if (null != data && data.size() != 0) {
                LogUtil.d(TAG, "未上传记录：" + data.size());

                // 清除数据
                mListData.clear();

                int count = 0;
                for (int index = 0; index < data.size(); index++) {
                    // TODO 共用发件的ListView javaBean
                    FajianListViewBean fajianListViewBean = new
                            FajianListViewBean();
                    // TODO 一旦删除记录，则及时更新ID值
                    fajianListViewBean.setId(++count);
                    fajianListViewBean.setScannerData(data.get(index)
                            .getShipmentNumber());
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case Constant.SCAN_KEY_CODE: {
                // FIXME 执行一次扫码操作，判断前置条件满足？
                if (TextUtils.isEmpty(mTvPreviousStation.getText().toString()
                )) {
                    Toast.makeText(DaojianActivity.this, "前置信息为空", Toast
                            .LENGTH_SHORT).show();

                    mDeviceVibrator.vibrate(1000);
                    return true;
                } else {
                    // 数据库查询
                    String previousStation = mTvPreviousStation.getText()
                            .toString().split("  ")[0];
                    LogUtil.trace("previousStation:" + previousStation);

                    // BUG: 不能转化为Integer值，比如0020 --> 20
                    if (isExistCurrentStation(previousStation)) {
                        // FIXME 判断前置条件？开启一次扫描
                        // FIXME 启动扫描线程，需要考虑多次按下的问题
                        if (mScanThread == null) {
                            mScanThread = new ScanThread();
                            // 线程先运行起来
                            mScanThread.start();
                        }

                        // 发出一次扫码广播
                        Intent intent = new Intent();
                        intent.setAction("com.jb.action.F4key");
                        intent.putExtra("F4key", "down");
                        DaojianActivity.this.sendBroadcast(intent);

                        // 倒计时开始
                        mCountDownTimer.start();
                    } else {
                        Toast.makeText(DaojianActivity.this, "前置信息不符合", Toast
                                .LENGTH_SHORT).show();
                        mDeviceVibrator.vibrate(1000);
                    }
                }

                return true;
            }

            case Constant.F2_KEY_CODE: {
                // 1. 获取最后（最新）扫入的barcode
                if (mListData != null && mListData.size() != 0) {
                    LogUtil.trace("mListData.size:" + mListData.size() + "; " +
                            "barcode:" + mListData.get(mListData.size() - 1)
                            .getScannerData());

                    // 提示是否切换账号
                    final AlertDialog.Builder normalDialog = new AlertDialog
                            .Builder(DaojianActivity.this);
                    normalDialog.setTitle("提示");
                    normalDialog.setCancelable(false);
                    normalDialog.setMessage("是否删除最新记录？");
                    normalDialog.setPositiveButton("确定", new DialogInterface
                            .OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 2. 设置数据库对应记录的“是否可用”状态为：不可用
                            deleteFindedBean(mListData.get(mListData.size() - 1)
                                    .getScannerData());

                            // 3. 重新从数据库中查出所有记录,更新ListView
                            reQueryUnUploadDataForListView();
                        }
                    });
                    normalDialog.setNegativeButton("取消", new DialogInterface
                            .OnClickListener() {

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

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 判断是否存在指定上一站网点
     *
     * @param stationID
     * @return
     */
    private boolean isExistCurrentStation(String stationID) {
        if (tableIsExist("salesservice")) {
            // 存在保存发件数据的表，从该表中查询对应的单号
            DbManager dbManager = BQDataBaseHelper.getDb();
            try {
                // FIXME 查询数据库，是否存在指定网点；有可能在选择提示时，做过滤操作
                List<SalesService> bean = dbManager.selector(SalesService
                        .class).where("网点编号",
                        "like", stationID).limit(1).findAll();
                if (bean != null && bean.size() != 0) {
                    LogUtil.trace("bean:" + bean.size());
                    return true;
                } else {
                    LogUtil.trace("bean is null");
                }
            } catch (DbException e) {
                LogUtil.trace("Exception");
                e.printStackTrace();
            }
        } else {
            LogUtil.trace("salesservice 不存在");
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ensure: {
                // 确定按键，上传文件
                LogUtil.trace();

                DbManager db = BQDataBaseHelper.getDb();
                List<CargoArrivalFileContent> list = null;
                try {
                    // FIXME 1. 查询数据库中标识位是“未上传”的记录，且是数据可用
                    list = db.selector(CargoArrivalFileContent.class).where
                            ("是否上传", "like",
                                    "未上传").and("是否可用", "=", "可用").findAll();
                    if (null != list && list.size() != 0) {
                        // 2. 获取随机文件名
                        mCargoArrivalFileName = new CargoArrivalFileName();
                        if (mCargoArrivalFileName.linkToTXTFile()) {
                            // 3. 链接创建的文件和上传功能
                            mUploadServerFile = new UploadServerFile
                                    (mCargoArrivalFileName
                                            .getFileInstance());
                            for (int index = 0; index < list.size(); index++) {
                                // 4. 创建写入文本的字符串，并写入文本
                                CargoArrivalFileContent javaBean = list.get
                                        (index);
                                String content = javaBean.getmCurrentValue()
                                        + "\r\n";
                                if (mUploadServerFile.writeContentToFile
                                        (content, true)) {
                                    // 不能删除数据，应该是否上传标志位为：已上传
                                    WhereBuilder whereBuilder = WhereBuilder
                                            .b();
                                    whereBuilder.and("运单编号", "=", javaBean
                                            .getShipmentNumber());
                                    // 5. 将当前数据库中对应数据“是否上传”标志置为：已上传
                                    db.update(CargoArrivalFileContent.class,
                                            whereBuilder, new
                                                    KeyValue("是否上传", "已上传"));
                                } else {
                                    // TODO 写入文件失败
                                    LogUtil.trace("写入文件失败");
                                }
                            }

                            // 6. 文件上传服务器
                            mUploadServerFile.uploadFile();
                            DaojianActivity.this.finish();
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
                DaojianActivity.this.finish();

                // 测试阶段删除所有记录
                DbManager db = BQDataBaseHelper.getDb();
                try {
                    List<CargoArrivalFileContent> list = db.findAll
                            (CargoArrivalFileContent.class);
                    if (list != null) {
                        LogUtil.d(TAG, "当前记录：" + list.size());
                        // db.delete(CargoArrivalFileContent.class);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    @Override
    protected void fillCode(String barcode) {
        super.fillCode(barcode);
        LogUtil.trace("barcode:" + barcode);

        if (mScanThread != null) {
            Message msg = mScanThread.mHandler.obtainMessage();
            // 已接收到返回数据
            msg.what = MSG_RETURE_RESULT;
            mScanThread.mHandler.sendMessage(msg);
            // 倒计时结束
            mCountDownTimer.cancel();
        }

        // 1. 查表：判断是否有记录
        if (isExistCurrentBarcode(barcode)) {
            // 若有记录则提示重复；若没有，继续执行
            Toast.makeText(DaojianActivity.this, "运单号已存在", Toast
                    .LENGTH_SHORT).show();
            mDeviceVibrator.vibrate(1000);

            return;
        }

        // 2. 插入到数据库中
        mCargoArrivalFileContent.setScanDate(TextStringUtil
                .getFormatTimeString());
        mCargoArrivalFileContent.setShipmentNumber(barcode);
        mCargoArrivalFileContent.setOperateDate(TextStringUtil.getFormatTime());
        LogUtil.d(TAG, "扫描数据：" + mCargoArrivalFileContent.toString());
        insertDataToDatabase(mCargoArrivalFileContent);

        // 3. 填充运单号
        mEtDeliveryNumber.setText(barcode);

        // 4. 更新ListView的数据
        FajianListViewBean mFajianListViewBean = new FajianListViewBean();
        mFajianListViewBean.setId(++mScanCount);
        mFajianListViewBean.setScannerData(barcode);
        mFajianListViewBean.setStatus("未上传");

        mListData.add(mFajianListViewBean);
        mFajianAdapter.notifyDataSetChanged();
    }

    private static final String DB_NAME = "daojian";

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
                List<CargoArrivalFileContent> bean = dbManager.selector
                        (CargoArrivalFileContent
                                .class).where("运单编号", "like", barcode).and
                        ("是否可用",
                                "like", "可用").findAll();

                if (bean != null && bean.size() != 0) {
                    for (int index = 0; index < bean.size(); index++) {
                        long[] delta = TextStringUtil.getDistanceTimes(bean
                                .get(index)
                                .getScanDate(), TextStringUtil
                                .getFormatTimeString());
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
        long deltaValue = delta[0] * 24 * 60 * 60 + delta[1] * 60 * 60 +
                delta[2] * 60 + delta[3];
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
            String sql = "select count(*) from sqlite_master where type " +
                    "='table' and name "
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
     * 每次扫描后，先将数据存入数据库，需要的数据可根据UnloadArrivalFileContent对应
     * <p>
     * 与之相关的数据库Table为：xcdaojian
     */
    private void insertDataToDatabase(final CargoArrivalFileContent
                                              cargoArrivalFileContent) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                DbManager db = BQDataBaseHelper.getDb();
                try {
                    db.save(cargoArrivalFileContent);
                } catch (DbException e) {
                    LogUtil.trace(e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
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
        LogUtil.trace("barcode:" + barcode);
        DbManager db = BQDataBaseHelper.getDb();
        try {
            // 不能删除数据，应该设置“是否可用”状态为：不可用
            WhereBuilder whereBuilder = WhereBuilder.b();
            whereBuilder.and("运单编号", "=", barcode);
            db.update(CargoArrivalFileContent.class, whereBuilder, new
                    KeyValue("是否可用", "不可用"));
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 查询某条记录
     *
     * @param id
     * @return
     */
    private boolean queryPreviousStation(String id) {
        List<SalesService> mData = null;
        DbManager dbManager = BQDataBaseHelper.getDb();
        try {
            mData = dbManager.selector(SalesService.class).where("网点编号", "=",
                    id).findAll();

            if (mData != null && mData.size() != 0) {
                // 存在记录
                return true;
            }
        } catch (DbException e) {
            LogUtil.trace();
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void syncViewAfterUpload() {
        super.syncViewAfterUpload();

        reQueryUnUploadDataForListView();
    }
}
