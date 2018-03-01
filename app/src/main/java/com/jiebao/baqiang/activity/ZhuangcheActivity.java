package com.jiebao.baqiang.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.adapter.FajianAdatper;
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
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by open on 2018/1/22.
 */

public class ZhuangcheActivity extends BaseActivity implements View
        .OnClickListener {
    private static final String TAG = "ZhuangcheActivity";

    private AutoCompleteTextView mTvNextStation;
    private AutoCompleteTextView mTvShipmentType;
    private AutoCompleteTextView mTvVehicleId;
    private EditText mEtDeliveryNumber;
    private Button mBtnSure, mBtnCancel;
    private ListView mListView;

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
        setHeaderCenterViewText(getString(R.string.main_storge));
    }

    @Override
    public void initData() {
        prepareDataForView();

        mTvVehicleId = ZhuangcheActivity.this.findViewById(R.id
                .tv_vehicle_code);
        mTvVehicleId.setAdapter(mVehicleInfoAdapter);
        // 监听EditText是否获取焦点
        mTvVehicleId.setOnFocusChangeListener(new View
                .OnFocusChangeListener() {

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
        mTvVehicleId.setOnItemClickListener(new AdapterView
                .OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int
                    position, long id) {
                // 一旦选定上一站，则解析网点编号，更新ShipmentFileContent实体内容
                String vehicleId = mTvVehicleId.getText().toString();
                LogUtil.d(TAG, "serverID:" + vehicleId);
                // 更新车辆识别码
                mZcFajianFileContent.setIdentify(vehicleId);
            }
        });

        mTvNextStation = ZhuangcheActivity.this.findViewById(R.id
                .tv_next_station);
        mTvNextStation.setAdapter(mNextStationAdapter);
        // 监听EditText是否获取焦点
        mTvNextStation.setOnFocusChangeListener(new View
                .OnFocusChangeListener() {

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
        mTvNextStation.setOnItemClickListener(new AdapterView
                .OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int
                    position, long id) {
                // 一旦选定下一站，则解析网点编号，更新ShipmentFileContent实体内容
                String serverID = mTvNextStation.getText().toString();
                LogUtil.d(TAG, "serverID:" + serverID);
                String[] arr = serverID.split("  ");
                // 更新下一站网点编号
                mZcFajianFileContent.setNextStation(arr[0]);
            }
        });

        mTvShipmentType = ZhuangcheActivity.this.findViewById(R.id
                .tv_shipment_type);
        mTvShipmentType.setAdapter(mShipmentType);
        mTvShipmentType.setOnFocusChangeListener(new View
                .OnFocusChangeListener() {

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
        mTvShipmentType.setOnItemClickListener(new AdapterView
                .OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int
                    position, long id) {
                // 一旦选定快件类型，则解析快件类型编号，更新ShipmentFileContent实体内容
                String shipmentType = mTvShipmentType.getText().toString();
                LogUtil.d(TAG, "shipmentType:" + shipmentType);
                String[] arr = shipmentType.split("  ");
                mZcFajianFileContent.setShipmentType(arr[0]);
            }
        });

        mEtDeliveryNumber = ZhuangcheActivity.this.findViewById(R.id
                .et_shipment_number);

        mBtnSure = ZhuangcheActivity.this.findViewById(R.id.btn_ensure);
        mBtnCancel = ZhuangcheActivity.this.findViewById(R.id.btn_back);
        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mListView = ZhuangcheActivity.this.findViewById(R.id
                .list_view_scan_data);
        mListView.setAdapter(mFajianAdapter);
    }

    private void prepareDataForView() {
        mVehicleInfo = resolveVehicleInfo();
        mVehicleInfoAdapter = new ArrayAdapter<>(ZhuangcheActivity.this, R
                .layout.list_item, mVehicleInfo);

        // 准备上一站网点数据
        mNextStationInfo = resolveNextStationData();
        mNextStationAdapter = new ArrayAdapter<>
                (ZhuangcheActivity.this, R.layout.list_item,
                        mNextStationInfo);

        // 解析快件类型信息
        resolveShipmentTypeData();

        mZcFajianFileContent = getZCFajianFileContent();
        mZcFajianDispatchFileName = new ZCFajianDispatchFileName();
        boolean isAllSuccess = mZcFajianDispatchFileName.linkToTXTFile();
        LogUtil.e(TAG, "isAllSuccess:" + isAllSuccess);
        mZcfajianUploadFile = new UploadServerFile(mZcFajianDispatchFileName
                .getFileInstance());


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
            mArrayInfo.add(mData.get(index).get网点编号() + "  " + mData.get
                    (index).get网点名称());
        }

        return mArrayInfo;
    }

    private void resolveShipmentTypeData() {
        mShipmentTypeList = queryShipmentTypeData();

        List<String> mShipmentData = new ArrayList<>();
        for (int index = 0; index < mShipmentTypeList.size(); index++) {
            // 采用固定格式便于解析快件类型
            mShipmentData.add(mShipmentTypeList.get(index).get类型编号() + "  " +
                    mShipmentTypeList.get(index).get类型名称());
        }

        LogUtil.trace("size:" + mShipmentData.size());
        mShipmentType = new ArrayAdapter<String>(ZhuangcheActivity.this, R
                .layout.list_item, mShipmentData);
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

        return new ZCFajianFileContent(nextStation, scanDate, goodsType,
                shipmentType, shipmentNumber, scanEmployeeNumber,
                operateDate, weight, identify, status);
    }

    protected void fillCode(String barcode) {
        mEtDeliveryNumber.setText(barcode);

        // 更新ListView的数据
        FajianListViewBean mFajianListViewBean = new FajianListViewBean();
        mFajianListViewBean.setId(++mScanCount);
        mFajianListViewBean.setScannerData(barcode);
        mFajianListViewBean.setStatus("未上传");
        mListData.add(mFajianListViewBean);
        mFajianAdapter.notifyDataSetChanged();

        //把扫到的内容更行到数据库
        mZcFajianFileContent.setGoodsType("2");
        mZcFajianFileContent.setScanDate(TextStringUtil
                .getFormatTimeString());
        mZcFajianFileContent.setShipmentNumber(barcode);
        mZcFajianFileContent.setOperateDate(TextStringUtil.getFormatTime());
        LogUtil.trace(mZcFajianFileContent.toString());

        insertDataToDatabase(mZcFajianFileContent);

        // 根据数据看数据，构造上传文件
        String content = mZcFajianFileContent.getmCurrentValue() + "\r\n";
        LogUtil.trace("content:" + content + ";");
        mZcfajianUploadFile.writeContentToFile(content, true);
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
    private void insertDataToDatabase(final ZCFajianFileContent
                                              zcFajianFileContent) {
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
            case R.id.btn_back: {
                mZcfajianUploadFile.uploadFile();
                ZhuangcheActivity.this.finish();

                break;
            }

            case R.id.btn_ensure: {
                ZhuangcheActivity.this.finish();
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
}
