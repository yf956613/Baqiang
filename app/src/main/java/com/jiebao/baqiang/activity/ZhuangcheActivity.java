package com.jiebao.baqiang.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import com.jiebao.baqiang.data.bean.FajianListViewBean;
import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.data.bean.ShipmentType;
import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.bean.VehicleInfo;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.updateData.UpdateInterface;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianDispatchFileName;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    private ArrayAdapter<String> mVehicleInfoAdapter;

    // 下一站网点信息
    private List<String> mNextStationInfo;
    // 下一站快速提示数据适配器
    private ArrayAdapter<String> mNextStationAdapter;

    // 快件类型相关
    private List<ShipmentType> mShipmentTypeList;
    private ArrayAdapter<String> mShipmentType;

    // 用于更新ListView界面数据
    private List<FajianListViewBean> mListData;
    private FajianAdatper mFajianAdapter;

    // 此处作为全局扫描次数的记录，用于更新ListView的ID
    private int mScanCount;

    //往file 中写文件
    ZCFajianDispatchFileName mZcFajianDispatchFileName;
    ZCFajianFileContent mZcFajianFileContent;
    UploadServerFile mZcfajianUploadFile;

    @Override
    public void initView() {
        setContent(R.layout.zhuangchefajian);
        setHeaderLeftViewText(getString(R.string.main_storge));
    }

    @Override
    public void initData() {
        prepareDataForView();

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
                    "like", "未上传").findAll();
            if (null != data) {
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
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void prepareDataForView() {
        mVehicleInfo = resolveVehicleInfo();
        mVehicleInfoAdapter = new ArrayAdapter<>(ZhuangcheActivity.this, R.layout.list_item,
                mVehicleInfo);

        // 准备上一站网点数据
        mNextStationInfo = resolveNextStationData();
        mNextStationAdapter = new ArrayAdapter<>(ZhuangcheActivity.this, R.layout.list_item,
                mNextStationInfo);

        // 解析快件类型信息
        resolveShipmentTypeData();

        mZcFajianFileContent = getZCFajianFileContent();
        mZcFajianDispatchFileName = new ZCFajianDispatchFileName();
        boolean isAllSuccess = mZcFajianDispatchFileName.linkToTXTFile();
        LogUtil.e(TAG, "isAllSuccess:" + isAllSuccess);
        mZcfajianUploadFile = new UploadServerFile(mZcFajianDispatchFileName.getFileInstance());


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
        mShipmentType = new ArrayAdapter<String>(ZhuangcheActivity.this, R.layout.list_item,
                mShipmentData);
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

        return new ZCFajianFileContent(nextStation, scanDate, goodsType, shipmentType,
                shipmentNumber, scanEmployeeNumber, operateDate, weight, identify, status);
    }

    protected void fillCode(String barcode) {
        LogUtil.d(TAG, "barcode:" + barcode);

        // TODO 判断前置条件是否符合
        if (TextUtils.isEmpty(mTvNextStation.getText().toString()) || TextUtils.isEmpty
                (mTvShipmentType.getText().toString()) || TextUtils.isEmpty(mTvVehicleId.getText
                ().toString())) {
            Toast.makeText(ZhuangcheActivity.this, "前置信息为空", Toast.LENGTH_SHORT).show();

            return;
        }

        // 1. 查表：当前是名为zcfajian的表，判断是否有记录
        if (isExistCurrentBarcode(barcode)) {
            // 若有记录则提示重复；若没有，继续执行
            Toast.makeText(ZhuangcheActivity.this, "运单号已存在", Toast.LENGTH_SHORT).show();
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

        /*// 根据数据看数据，构造上传文件
        String content = mZcFajianFileContent.getmCurrentValue() + "\r\n";
        LogUtil.trace("content:" + content + ";");
        mZcfajianUploadFile.writeContentToFile(content, true);*/
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
                // 查询数据库，是否有记录
                List<ZCFajianFileContent> bean = dbManager.selector(ZCFajianFileContent.class)
                        .where("运单编号", "like", barcode).limit(1).findAll();
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
            String sql = "select count(*) from sqlite_master where type " + "='table' and name " +
                    "='" + tableName.trim() + "' ";
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void onResume() {
        super.onResume();
        // new MyAsyncTaskLoader().execute();
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
                    // 1. 查询数据库中标识位“未上传”的记录
                    list = db.selector(ZCFajianFileContent.class).where("是否上传", "like", "未上传")
                            .findAll();
                    if (null != list) {
                        LogUtil.trace("list:" + list.size());
                        for (int index = 0; index < list.size(); index++) {
                            // 2. 创建写入文本的字符串，并写入文本
                            ZCFajianFileContent javaBean = list.get(index);
                            String content = javaBean.getmCurrentValue() + "\r\n";
                            if (mZcfajianUploadFile.writeContentToFile(content, true)) {
                                // 3. 写入成功，删除记录
                                /*WhereBuilder whereBuilder = WhereBuilder.b();
                                whereBuilder.and("运单编号", "=", javaBean
                                        .getShipmentNumber());
                                db.update(ShipmentFileContent.class,
                                        whereBuilder, new KeyValue("是否上传",
                                                "已上传"));*/
                                WhereBuilder b = WhereBuilder.b();
                                b.and("运单编号", "=", javaBean.getShipmentNumber());
                                db.delete(ZCFajianFileContent.class, b);
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
                mZcfajianUploadFile.uploadFile();
                ZhuangcheActivity.this.finish();

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
     * 创建静态内部类，继承AsyncTaskLoader,并重写三个方法
     */
    /*private  class MyAsyncTaskLoader extends AsyncTask<Void,String,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            vehicleInfo =(List<String>) BQDataBaseHelper.queryData
            (VehicleInfo.class,"number");
            salesService =(List<String>) BQDataBaseHelper.queryData
            (SalesService.class,"serviceName");
            //shipmentTyoe =(List<String>) BQDataBaseHelper.queryData
            (ShipmentType.class,"类型名称");
            resolveShipmentTypeData();
            liucang = (List<String>)BQDataBaseHelper.queryData(LiucangBean
            .class,"名称");
            return null;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            showAdapter();
            // 运行在主线程，更新UI
        }

        protected  void  showAdapter(){
            AutoCompleteTextView autoCompleteTextView1 =
            (AutoCompleteTextView) car_code.getRightText();
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
            (ZhuangcheActivity.this, android.R.layout.simple_list_item_1,
            vehicleInfo);
            autoCompleteTextView1.setAdapter(adapter1);
            AutoCompleteTextView autoCompleteTextView2 =
            (AutoCompleteTextView) mTvNextStation.getRightText();
            ArrayAdapter<String> adapter2 = new ArrayAdapter<>
            (ZhuangcheActivity.this, android.R.layout.simple_list_item_1,
            salesService);
            autoCompleteTextView2.setAdapter(adapter2);
            AutoCompleteTextView autoCompleteTextView3 =
            (AutoCompleteTextView) mTvShipmentType.getRightText();
         //   ArrayAdapter<String> adapter3 = new ArrayAdapter<>
         (ZhuangcheActivity.this, android.R.layout.simple_list_item_1,
         mShipmentData);
            autoCompleteTextView3.setAdapter(mShipmentType);
        }
    }*/

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


}
