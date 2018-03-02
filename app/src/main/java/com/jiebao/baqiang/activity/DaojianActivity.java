package com.jiebao.baqiang.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
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
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * 到件需要注意的地方：若管理员设置中，到/发件扫描判断开关为开状态，上一站只显示类型为“网点”的项
 */
public class DaojianActivity extends BaseActivity implements View
        .OnClickListener, CouldDeleteListView.DelButtonClickListener {
    private static final String TAG = UnloadCargoArrivalActivity.class
            .getSimpleName();

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

    @Override
    public void initView() {
        setContent(R.layout.daojian);
        setHeaderCenterViewText(getString(R.string.main_query));
    }

    @Override
    public void initData() {
        prepareDataForView();

        mTvPreviousStation = DaojianActivity.this.findViewById(R
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
                String serverID = mTvPreviousStation.getText().toString();
                LogUtil.d(TAG, "serverID:" + serverID);
                String[] arr = serverID.split("  ");
                // 获取上一站网点编号
                mCargoArrivalFileContent.setPreviousStation(arr[0]);
            }
        });

        mEtDeliveryNumber = DaojianActivity.this.findViewById(R.id
                .et_shipment_number);

        mBtnSure = DaojianActivity.this.findViewById(R.id
                .btn_ensure);
        mBtnCancel = DaojianActivity.this.findViewById(R.id
                .btn_back);
        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mListView = DaojianActivity.this.findViewById(R.id
                .list_view_scan_data);
        mListView.setAdapter(mFajianAdapter);
        mListView.setDelButtonClickListener(DaojianActivity.this);

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
            List<CargoArrivalFileContent> data = db.selector
                    (CargoArrivalFileContent.class).where("是否上传",
                    "like", "未上传").findAll();
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
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void prepareDataForView() {
        // 准备上一站网点数据
        mPreviousStationInfo = resolvePreviousStationData();
        mPreviousStationAdapter = new ArrayAdapter<>(DaojianActivity.this, R
                .layout
                .list_item, mPreviousStationInfo);

        // 创建待上传文件
        mCargoArrivalFileName = new CargoArrivalFileName();
        boolean isAllSuccess = mCargoArrivalFileName.linkToTXTFile();
        LogUtil.d(TAG, "isAllSuccess:" + isAllSuccess);

        // 组装写入文件数据
        mCargoArrivalFileContent = getCargoArrivalFileContent();
        LogUtil.trace("mUnloadArrivalFileContent:" +
                mCargoArrivalFileContent.toString());

        // 上传文件的实体
        mUploadServerFile = new UploadServerFile(mCargoArrivalFileName
                .getFileInstance());

        mListData = new ArrayList<>();
        mFajianAdapter = new FajianAdatper(DaojianActivity.this,
                mListData);
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

        return new CargoArrivalFileContent(previousStation, scanDate,
                goodsType, shipmentType, shipmentNumber, scanEmployeeNumber,
                operateDate, weight, status);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ensure: {
                // 确定按键，上传文件
                LogUtil.trace();

                DbManager db = BQDataBaseHelper.getDb();
                List<CargoArrivalFileContent> list = null;
                try {
                    // 1. 查询数据库中标识位“未上传”的记录
                    list = db.selector(CargoArrivalFileContent.class).where("是否上传",
                            "like", "未上传").findAll();
                    if (null != list) {
                        LogUtil.trace("list:" + list.size());
                        for (int index = 0; index < list.size(); index++) {
                            // 2. 创建写入文本的字符串，并写入文本
                            CargoArrivalFileContent javaBean = list.get(index);
                            String content = javaBean.getmCurrentValue() +
                                    "\r\n";
                            if (mUploadServerFile.writeContentToFile(content,
                                    true)) {
                                // 3. 写入成功，删除记录
                                /*WhereBuilder whereBuilder = WhereBuilder.b();
                                whereBuilder.and("运单编号", "=", javaBean
                                        .getShipmentNumber());
                                db.update(ShipmentFileContent.class,
                                        whereBuilder, new KeyValue("是否上传",
                                                "已上传"));*/
                                WhereBuilder b = WhereBuilder.b();
                                b.and("运单编号", "=", javaBean.getShipmentNumber
                                        ());
                                db.delete(CargoArrivalFileContent.class, b);
                            } else {
                                // 写入文件失败，跳过
                            }
                        }
                    }
                } catch (DbException e) {
                    LogUtil.d(TAG, "崩溃信息:" + e.getLocalizedMessage());
                    e.printStackTrace();
                }

                // 4. 文件上传服务器
                mUploadServerFile.uploadFile();
                DaojianActivity.this.finish();

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

        // TODO 判断前置条件是否符合
        if (TextUtils.isEmpty(mTvPreviousStation.getText().toString())) {
            Toast.makeText(DaojianActivity.this, "前置信息为空", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // 1. 查表：当前是名为daojian的表，判断是否有记录
        if (isExistCurrentBarcode(barcode)) {
            // 若有记录则提示重复；若没有，继续执行
            Toast.makeText(DaojianActivity.this, "运单号已存在", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // 2. 插入到数据库中
        mCargoArrivalFileContent.setScanDate(TextStringUtil
                .getFormatTimeString());
        mCargoArrivalFileContent.setShipmentNumber(barcode);
        mCargoArrivalFileContent.setOperateDate(TextStringUtil.getFormatTime());
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

        /*// 根据JavaBean实体组装数据
        String content = mCargoArrivalFileContent.getmCurrentValue() + "\r\n";
        // 数据存入文件
        mUploadServerFile.writeContentToFile(content, true);*/
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
                // 查询数据库，是否有记录
                List<CargoArrivalFileContent> bean = dbManager.selector
                        (CargoArrivalFileContent.class).where("运单编号",
                        "like", barcode).limit(1).findAll();
                LogUtil.trace("bean:" + bean.size());

                if (bean != null && bean.size() != 0) {
                    return true;
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
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
                    "='table' and name ='" +
                    tableName.trim() + "' ";
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
        DbManager db = BQDataBaseHelper.getDb();
        try {
            db.delete(CargoArrivalFileContent.class, WhereBuilder.b("运单编号",
                    "like", barcode));
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }
    }
}
