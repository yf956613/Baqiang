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
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * 到件需要注意的地方：若管理员设置中，到/发件扫描判断开关为开状态，上一站只显示类型为“网点”的项
 */
public class DaojianActivity extends BaseActivity implements View
        .OnClickListener {
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
                LogUtil.trace();

                DbManager db = BQDataBaseHelper.getDb();
                try {
                    List<CargoArrivalFileContent> list = db.findAll
                            (CargoArrivalFileContent.class);
                    if (null != list) {
                        LogUtil.trace("list:" + list.size());
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

                DaojianActivity.this.finish();

                break;
            }

            case R.id.btn_back: {
                LogUtil.trace();
                mUploadServerFile.uploadFile();
                DaojianActivity.this.finish();

                break;
            }
        }
    }

    @Override
    protected void fillCode(String barcode) {
        super.fillCode(barcode);

        // 填充运单号
        mEtDeliveryNumber.setText(barcode);

        // 更新ListView的数据
        FajianListViewBean mFajianListViewBean = new FajianListViewBean();
        mFajianListViewBean.setId(++mScanCount);
        mFajianListViewBean.setScannerData(barcode);
        mFajianListViewBean.setStatus("未上传");
        mListData.add(mFajianListViewBean);
        mFajianAdapter.notifyDataSetChanged();

        mCargoArrivalFileContent.setScanDate(TextStringUtil
                .getFormatTimeString());
        mCargoArrivalFileContent.setShipmentNumber(barcode);
        mCargoArrivalFileContent.setOperateDate(TextStringUtil.getFormatTime
                ());

        insertDataToDatabase(mCargoArrivalFileContent);

        // 根据JavaBean实体组装数据
        String content = mCargoArrivalFileContent.getmCurrentValue() + "\r\n";
        // 数据存入文件
        mUploadServerFile.writeContentToFile(content, true);
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
}
