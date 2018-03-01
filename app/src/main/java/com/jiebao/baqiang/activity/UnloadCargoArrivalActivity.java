package com.jiebao.baqiang.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.adapter.FajianAdatper;
import com.jiebao.baqiang.custView.CouldDeleteListView;
import com.jiebao.baqiang.data.arrival.UnloadArrivalFileContent;
import com.jiebao.baqiang.data.arrival.UnloadArrivalFileName;
import com.jiebao.baqiang.data.bean.FajianListViewBean;
import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.bean.VehicleInfo;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.updateData.UpdateInterface;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * 卸车到件Activity
 */

public class UnloadCargoArrivalActivity extends BaseActivity implements View
        .OnClickListener {
    private static final String TAG = UnloadCargoArrivalActivity.class
            .getSimpleName();

    private AutoCompleteTextView mTvVehicleId, mTvPreviousStation;
    private Button mBtnSure, mBtnCancel;
    private CouldDeleteListView mListView;
    private EditText mEtDeliveryNumber;

    // 待上传文件名
    private UnloadArrivalFileName mUnloadArrivalFileName;
    // 待插入数据库内容
    private UnloadArrivalFileContent mUnloadArrivalFileContent;
    // 上传文件实体
    private UploadServerFile mUploadServerFile;

    // 用于更新ListView界面数据，复用发件功能
    private List<FajianListViewBean> mListData;
    private FajianAdatper mFajianAdapter;

    // 此处作为全局扫描次数的记录，用于更新ListView的ID
    private int mScanCount;

    // 车牌识别号信息
    private List<String> mVehicleInfo;
    // 车辆信息数据适配器
    private ArrayAdapter<String> mVehicleInfoAdapter;

    // 上一站网点信息
    private List<String> mPreviousStationInfo;
    // 上一站快速提示数据适配器
    private ArrayAdapter<String> mPreviousStationAdapter;


    @Override
    public void initView() {
        setContent(R.layout.activity_unload_shipment_arrival);
        setHeaderCenterViewText(getString(R.string.main_check));
    }

    @Override
    public void initData() {
        prepareDataForView();

        mTvVehicleId = UnloadCargoArrivalActivity.this.findViewById(R.id
                .tv_car_code);
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
                mUnloadArrivalFileContent.setVehicleId(vehicleId);
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
                String serverID = mTvPreviousStation.getText().toString();
                LogUtil.d(TAG, "serverID:" + serverID);
                String[] arr = serverID.split("  ");
                // 更新上一站网点编号
                mUnloadArrivalFileContent.setPreviousStation(arr[0]);
            }
        });

        mBtnSure = UnloadCargoArrivalActivity.this.findViewById(R.id
                .btn_ensure);
        mBtnCancel = UnloadCargoArrivalActivity.this.findViewById(R.id
                .btn_back);
        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mListView = UnloadCargoArrivalActivity.this.findViewById(R.id
                .list_view_scan_data);
        mListView.setAdapter(mFajianAdapter);

        mEtDeliveryNumber = UnloadCargoArrivalActivity.this.findViewById(R.id
                .et_shipment_number);
    }

    private void prepareDataForView() {
        mVehicleInfo = resolveVehicleInfo();
        mVehicleInfoAdapter = new ArrayAdapter<>(UnloadCargoArrivalActivity
                .this, R
                .layout.list_item, mVehicleInfo);

        // 准备上一站网点数据
        mPreviousStationInfo = resolvePreviousStationData();
        mPreviousStationAdapter = new ArrayAdapter<>
                (UnloadCargoArrivalActivity.this, R.layout.list_item,
                        mPreviousStationInfo);

        // 创建待上传文件
        mUnloadArrivalFileName = new UnloadArrivalFileName();
        boolean isAllSuccess = mUnloadArrivalFileName.linkToTXTFile();
        LogUtil.d(TAG, "isAllSuccess:" + isAllSuccess);

        // 组装写入文件数据
        mUnloadArrivalFileContent = getUnloadArrivalFileContent();
        LogUtil.trace("mUnloadArrivalFileContent:" +
                mUnloadArrivalFileContent.toString
                        ());

        // 上传文件实体
        mUploadServerFile = new UploadServerFile(mUnloadArrivalFileName
                .getFileInstance());

        mListData = new ArrayList<>();
        mFajianAdapter = new FajianAdatper(UnloadCargoArrivalActivity.this,
                mListData);
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

    /**
     * 解析上一站网点信息
     */
    private List<String> resolvePreviousStationData() {
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

    /**
     * 初始化时，先构建一个ShipmentFileContent实体
     *
     * @return
     */
    private UnloadArrivalFileContent getUnloadArrivalFileContent() {
        // TODO 上一站点编号  模拟
        String previousStation = String.valueOf("59406");

        // 扫描时间
        String scanDate = TextStringUtil.getFormatTimeString();
        // 物品类型
        String goodsType = "";
        // 快件类型
        String shipmentType = "4";
        // 运单编号
        String shipmentNumber = "";
        // 扫描员工编号
        String scanEmployeeNumber = UpdateInterface.userName;
        // 操作日期
        String operateDate = TextStringUtil.getFormatTime();
        // 重量
        String weight = "0.0";
        // 车辆识别号
        String vehicleID = "";
        // 是否上传状态
        String status = "未上传";

        return new UnloadArrivalFileContent(previousStation, scanDate,
                goodsType, shipmentType, shipmentNumber, scanEmployeeNumber,
                operateDate, weight, vehicleID, status);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ensure: {
                LogUtil.trace();

                DbManager db = BQDataBaseHelper.getDb();
                try {
                    List<UnloadArrivalFileContent> list = db.findAll
                            (UnloadArrivalFileContent.class);
                    if (null != list) {
                        LogUtil.trace("list:" + list.size());
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

                UnloadCargoArrivalActivity.this.finish();
                break;
            }

            case R.id.btn_back: {
                LogUtil.trace();

                mUploadServerFile.uploadFile();
                UnloadCargoArrivalActivity.this.finish();

                break;
            }
        }
    }

    @Override
    protected void fillCode(String barcode) {
        super.fillCode(barcode);

        mEtDeliveryNumber.setText(barcode);

        // 更新ListView的数据
        FajianListViewBean mFajianListViewBean = new FajianListViewBean();
        mFajianListViewBean.setId(++mScanCount);
        mFajianListViewBean.setScannerData(barcode);
        mFajianListViewBean.setStatus("未上传");
        mListData.add(mFajianListViewBean);
        mFajianAdapter.notifyDataSetChanged();

        mUnloadArrivalFileContent.setScanDate(TextStringUtil
                .getFormatTimeString());
        mUnloadArrivalFileContent.setShipmentNumber(barcode);
        mUnloadArrivalFileContent.setOperateDate(TextStringUtil.getFormatTime
                ());
        LogUtil.trace(mUnloadArrivalFileContent.toString());

        // 数据存入数据库
        insertDataToDatabase(mUnloadArrivalFileContent);

        // 根据JavaBean实体组装数据
        String content = mUnloadArrivalFileContent.getmCurrentValue() + "\r\n";
        LogUtil.trace("content:" + content + ";");
        // 数据存入文件
        mUploadServerFile.writeContentToFile(content, true);
    }


    /**
     * 每次扫描后，先将数据存入数据库，需要的数据可根据UnloadArrivalFileContent对应
     * <p>
     * 与之相关的数据库Table为：xcdaojian
     */
    private void insertDataToDatabase(final UnloadArrivalFileContent
                                              unloadArrivalFileContent) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                DbManager db = BQDataBaseHelper.getDb();
                try {
                    db.save(unloadArrivalFileContent);
                } catch (DbException e) {
                    LogUtil.trace(e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
