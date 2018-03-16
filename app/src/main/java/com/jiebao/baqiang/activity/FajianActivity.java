package com.jiebao.baqiang.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.jiebao.baqiang.data.bean.FajianListViewBean;
import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.data.bean.ShipmentType;
import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.dispatch.ShipmentDispatchFileName;
import com.jiebao.baqiang.data.dispatch.ShipmentFileContent;
import com.jiebao.baqiang.data.updateData.UpdateInterface;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FajianActivity extends BaseActivityWithTitleAndNumber implements View
        .OnClickListener, CouldDeleteListView.DelButtonClickListener {
    private static final String TAG = "FajianActivity";

    private AutoCompleteTextView mTvShipmentType;
    private AutoCompleteTextView mTvNextStation;
    private Button mBtnSure, mBtnCancel;
    private CouldDeleteListView mListView;
    // 运单号
    private EditText mEtShipmentNumber;

    private ShipmentDispatchFileName mShipmentDispatchFileName;
    // 插入数据库中的一行数据
    private ShipmentFileContent mShipmentFileContent;
    private UploadServerFile mShipmentUploadFile;

    // 用于更新ListView界面数据
    private List<FajianListViewBean> mListData;
    private FajianAdatper mFajianAdapter;
    // 此处作为全局扫描次数的记录，用于更新ListView的ID
    private int mScanCount;

    // 下一站快速提示数据适配器
    private ArrayAdapter<String> mNextStation;
    private List<String> mNextStationInfo;
    // 快件类型相关
    private List<ShipmentType> mShipmentTypeList;
    private ArrayAdapter<String> mShipmentType;

    private Vibrator mDeviceVibrator;

    @Override
    public void initView() {
        setContent(R.layout.fajian);
        setHeaderLeftViewText(getString(R.string.main_output));
    }

    @Override
    public void initData() {
        prepareDataForView();

        mDeviceVibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);

        mTvNextStation = FajianActivity.this.findViewById(R.id.tv_next_station);
        mTvNextStation.setAdapter(mNextStation);
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
                    LogUtil.trace("mTvNextStation no hasFocus");
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
                // 获取网点编号
                mShipmentFileContent.setNextStation(arr[0]);

                LogUtil.trace(mShipmentFileContent.toString());
            }
        });

        // 快件类型
        mTvShipmentType = FajianActivity.this.findViewById(R.id.tv_shipment_type);
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
                // 一旦选定下一站，则解析快件类型编号，更新ShipmentFileContent实体内容
                String shipmentType = mTvShipmentType.getText().toString();
                LogUtil.d(TAG, "shipmentType:" + shipmentType);
                String[] arr = shipmentType.split("  ");
                // 获取网点编号
                mShipmentFileContent.setShipmentType(arr[0]);
                LogUtil.trace(mShipmentFileContent.toString());
            }
        });

        mEtShipmentNumber = FajianActivity.this.findViewById(R.id.et_shipment_number);

        mBtnSure = FajianActivity.this.findViewById(R.id.btn_ensure);
        mBtnCancel = FajianActivity.this.findViewById(R.id.btn_back);
        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mListView = FajianActivity.this.findViewById(R.id.list_view_scan_data);
        mListView.setAdapter(mFajianAdapter);
        mListView.setDelButtonClickListener(FajianActivity.this);

        // 初次启动时刷新数据
        reQueryUnUploadDataForListView();
    }

    /**
     * 为控件准备数据
     */
    private void prepareDataForView() {
        // 解析下一站网点信息
        mNextStationInfo = resolveNextStationData();
        mNextStation = new ArrayAdapter<>(FajianActivity.this, R.layout.list_item,
                mNextStationInfo);

        // 解析快件类型信息
        resolveShipmentTypeData();


        // 创建写入文件的一行文本实体
        mShipmentFileContent = getShipmentFileContent();
        LogUtil.trace("mShipmentFileContent:" + mShipmentFileContent.toString());

        // 用于显示扫描列表的信息
        mListData = new ArrayList<>();
        mFajianAdapter = new FajianAdatper(FajianActivity.this, mListData);
    }

    /**
     * 初始化时，先构建一个ShipmentFileContent实体
     *
     * @return
     */
    private ShipmentFileContent getShipmentFileContent() {
        // 首次创建ShipmentFileContent实体时，内容为虚构，并不写入文本中
        String nextStation = String.valueOf("59406");

        // 扫描日期
        String scanDate = TextStringUtil.getFormatTimeString();
        // 物品类型
        String goodsType = "";
        // 快件类型
        String shipmentType = String.valueOf("2");
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

        return new ShipmentFileContent(nextStation, scanDate, goodsType, shipmentType,
                shipmentNumber, scanEmployeeNumber, operateDate, weight, status);
    }

    // 从数据库中提取下一站的网点信息
    private List<String> resolveNextStationData() {
        LogUtil.trace();

        Boolean isOpen = SharedUtil.getBoolean(FajianActivity.this, Constant
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
            mArrayInfo.add(mData.get(index).get网点编号() + "  " + mData.get(index).get网点名称());
        }

        return mArrayInfo;
    }

    /**
     * 构造快件类型的填充数据
     */
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
        mShipmentType = new ArrayAdapter<String>(FajianActivity.this, R.layout.list_item,
                mShipmentData);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case Constant.SCAN_KEY_CODE: {
                // FIXME 执行一次扫码操作，判断前置条件满足？
                if (TextUtils.isEmpty(mTvShipmentType.getText().toString())) {
                    Toast.makeText(FajianActivity.this, "前置信息为空", Toast.LENGTH_SHORT).show();

                    mDeviceVibrator.vibrate(1000);
                    return true;
                } else {
                    // 数据库查询
                    String shipmentType = mTvShipmentType.getText().toString().split("  ")[0];
                    LogUtil.trace("shipmentType:" + shipmentType);

                    if (isExistCurrentShipmentType(Integer.parseInt(shipmentType))) {
                        // FIXME 判断前置条件？开启一次扫描
                        Intent intent = new Intent();
                        intent.setAction("com.jb.action.F4key");
                        intent.putExtra("F4key", "down");
                        FajianActivity.this.sendBroadcast(intent);
                    } else {
                        Toast.makeText(FajianActivity.this, "前置信息不符合", Toast.LENGTH_SHORT).show();
                        mDeviceVibrator.vibrate(1000);
                    }
                }

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
        super.fillCode(barcode);

        // 1. 查表：判断是否有记录
        if (isExistCurrentBarcode(barcode)) {
            // 若有记录则提示重复；若没有，继续执行
            Toast.makeText(FajianActivity.this, "运单号已存在", Toast.LENGTH_SHORT).show();
            mDeviceVibrator.vibrate(1000);

            return;
        }

        // 2. 插入到数据库中
        mShipmentFileContent.setScanDate(TextStringUtil.getFormatTimeString());
        mShipmentFileContent.setShipmentNumber(barcode);
        mShipmentFileContent.setOperateDate(TextStringUtil.getFormatTime());
        insertDataToDatabase(mShipmentFileContent);

        // 3. 填充EditText控件
        mEtShipmentNumber.setText(barcode);

        // 4. 更新ListView的数据
        FajianListViewBean mFajianListViewBean = new FajianListViewBean();
        // TODO 一旦删除记录，则及时更新ID值
        mFajianListViewBean.setId(++mScanCount);
        mFajianListViewBean.setScannerData(barcode);
        mFajianListViewBean.setStatus("未上传");

        mListData.add(mFajianListViewBean);
        mFajianAdapter.notifyDataSetChanged();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ensure: {
                DbManager db = BQDataBaseHelper.getDb();
                List<ShipmentFileContent> list = null;
                try {
                    // FIXME 1. 查询数据库中标识位是“未上传”的记录，且是数据可用
                    list = db.selector(ShipmentFileContent.class).where("是否上传", "like", "未上传")
                            .and("是否可用", "=", "可用").findAll();
                    if (null != list && list.size() != 0) {
                        // 2. 获取随机文件名
                        mShipmentDispatchFileName = new ShipmentDispatchFileName();
                        if (mShipmentDispatchFileName.linkToTXTFile()) {
                            // 3. 链接创建的文件和上传功能
                            mShipmentUploadFile = new UploadServerFile(mShipmentDispatchFileName
                                    .getFileInstance());
                            for (int index = 0; index < list.size(); index++) {
                                // 4. 创建写入文本的字符串，并写入文本
                                ShipmentFileContent javaBean = list.get(index);
                                String content = javaBean.getmCurrentValue() + "\r\n";
                                if (mShipmentUploadFile.writeContentToFile(content, true)) {
                                    // 不能删除数据，应该是否上传标志位为：已上传
                                    WhereBuilder whereBuilder = WhereBuilder.b();
                                    whereBuilder.and("运单编号", "=", javaBean.getShipmentNumber());
                                    // 5. 将当前数据库中对应数据“是否上传”标志置为：已上传
                                    db.update(ShipmentFileContent.class, whereBuilder, new
                                            KeyValue("是否上传", "已上传"));
                                } else {
                                    // TODO 写入文件失败
                                    LogUtil.trace("写入文件失败");
                                }
                            }

                            // 6. 文件上传服务器
                            mShipmentUploadFile.uploadFile();
                            FajianActivity.this.finish();
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
                FajianActivity.this.finish();

                // 测试阶段删除所有记录
                DbManager db = BQDataBaseHelper.getDb();
                try {
                    List<ShipmentFileContent> list = db.findAll(ShipmentFileContent.class);
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
     * 判断是否存在指定留仓原因
     *
     * @param reason
     * @return
     */
    private boolean isExistCurrentShipmentType(int reason) {
        if (tableIsExist("shipmenttype")) {
            // 存在保存发件数据的表，从该表中查询对应的单号
            DbManager dbManager = BQDataBaseHelper.getDb();
            try {
                // FIXME 查询数据库，是否包含指定类型编号
                List<ShipmentType> bean = dbManager.selector(ShipmentType.class).where("类型编号",
                        "like", reason).limit(1).findAll();
                if (bean != null && bean.size() != 0) {
                    LogUtil.trace("bean:" + bean.size());
                    return true;
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 查询下一站是否有记录记录
     *
     * @param id
     * @return
     */
    private boolean queryNextStation(String id) {
        List<SalesService> mData = null;
        DbManager dbManager = BQDataBaseHelper.getDb();
        try {
            mData = dbManager.selector(SalesService.class).where("网点编号", "=", id).findAll();

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


    private static final String DB_NAME = "fajian";

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
                List<ShipmentFileContent> bean = dbManager.selector(ShipmentFileContent.class)
                        .where("运单编号", "like", barcode).and("是否可用", "like", "可用").findAll();

                if (bean != null && bean.size() != 0) {
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
    private void insertDataToDatabase(final ShipmentFileContent shipmentFileContent) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                DbManager db = BQDataBaseHelper.getDb();
                try {
                    db.save(shipmentFileContent);
                } catch (DbException e) {
                    LogUtil.trace(e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
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
            db.update(ShipmentFileContent.class, whereBuilder, new KeyValue("是否可用", "不可用"));
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 从数据库中找出所有未上传记录
     */
    private void reQueryUnUploadDataForListView() {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            // 查询数据库中标识位“未上传”的记录
            List<ShipmentFileContent> data = db.selector(ShipmentFileContent.class).where("是否上传",
                    "=", "未上传").and("是否可用", "=", "可用").findAll();
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
}