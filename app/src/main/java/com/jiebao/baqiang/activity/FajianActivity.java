package com.jiebao.baqiang.activity;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

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
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FajianActivity extends BaseActivity implements View
        .OnClickListener {
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
    private HashMap<String, String> mShipmentDataTmp;

    @Override
    public void initView() {
        setContent(R.layout.fajian);
        setHeaderCenterViewText(getString(R.string.main_output));
    }

    @Override
    public void initData() {
        prepareDataForView();

        mTvNextStation = FajianActivity.this.findViewById(R.id.tv_next_station);
        mTvNextStation.setAdapter(mNextStation);
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
                    LogUtil.trace("mTvNextStation no hasFocus");
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
                // 获取网点编号
                mShipmentFileContent.setNextStation(arr[0]);

                LogUtil.trace(mShipmentFileContent.toString());
            }
        });

        // 快件类型
        mTvShipmentType = FajianActivity.this.findViewById(R.id
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
                // 一旦选定下一站，则解析快件类型编号，更新ShipmentFileContent实体内容
                String shipmentType = mTvShipmentType.getText().toString();
                LogUtil.d(TAG, "shipmentType:" + shipmentType);
                String[] arr = shipmentType.split("  ");
                // 获取网点编号
                mShipmentFileContent.setShipmentType(arr[0]);
                LogUtil.trace(mShipmentFileContent.toString());
            }
        });

        mBtnSure = FajianActivity.this.findViewById(R.id.btn_ensure);
        mBtnCancel = FajianActivity.this.findViewById(R.id.btn_back);
        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mListView = FajianActivity.this.findViewById(R.id.list_view_scan_data);
        mListView.setAdapter(mFajianAdapter);

        mEtShipmentNumber = FajianActivity.this.findViewById(R.id
                .et_shipment_number);
    }

    /**
     * 为控件准备数据
     */
    private void prepareDataForView() {
        // 解析下一站网点信息
        mNextStationInfo = resolveNextStationData();
        mNextStation = new ArrayAdapter<>(FajianActivity.this, R.layout
                .list_item, mNextStationInfo);

        // 解析快件类型信息
        resolveShipmentTypeData();

        // 创建待上传的文件，指定文件名并创建文件
        mShipmentDispatchFileName = new ShipmentDispatchFileName();
        boolean isAllSuccess = mShipmentDispatchFileName.linkToTXTFile();
        LogUtil.d(TAG, "isAllSuccess:" + isAllSuccess);

        // 创建写入文件的一行文本实体
        mShipmentFileContent = getShipmentFileContent();
        LogUtil.trace("mShipmentFileContent:" + mShipmentFileContent.toString
                ());

        // 创建可上传文本的JavaBean实体
        mShipmentUploadFile = new UploadServerFile
                (mShipmentDispatchFileName.getFileInstance());

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

        return new ShipmentFileContent(nextStation, scanDate, goodsType,
                shipmentType, shipmentNumber, scanEmployeeNumber,
                operateDate, weight, status);
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
            mArrayInfo.add(mData.get(index).get网点编号() + "  " + mData.get
                    (index).get网点名称());
        }

        return mArrayInfo;
    }

    /**
     * 构造快件类型的填充数据
     */
    private void resolveShipmentTypeData() {
        mShipmentTypeList = queryShipmentTypeData();

        mShipmentDataTmp = new HashMap<>();
        List<String> mShipmentData = new ArrayList<>();
        for (int index = 0; index < mShipmentTypeList.size(); index++) {
            // 采用固定格式便于解析快件类型
            mShipmentData.add(mShipmentTypeList.get(index).get类型编号() + "  " +
                    mShipmentTypeList.get(index).get类型名称());
            mShipmentDataTmp.put(mShipmentTypeList.get(index).get类型编号(),
                    mShipmentTypeList.get(index).get类型名称());
        }

        LogUtil.trace("size:" + mShipmentData.size());
        mShipmentType = new ArrayAdapter<String>(FajianActivity.this, R
                .layout.list_item, mShipmentData);
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
        LogUtil.trace("keyCode:" + keyCode);

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void fillCode(String barcode) {
        super.fillCode(barcode);
        // 执行一次扫描操作
        LogUtil.trace("barcode:" + barcode);

        // 填充运单号控件
        mEtShipmentNumber.setText(barcode);

        // 更新ListView的数据
        FajianListViewBean mFajianListViewBean = new FajianListViewBean();
        mFajianListViewBean.setId(++mScanCount);
        mFajianListViewBean.setScannerData(barcode);
        mFajianListViewBean.setStatus("未上传");
        // 刷新ListView数据
        mListData.add(mFajianListViewBean);
        mFajianAdapter.notifyDataSetChanged();

        mShipmentFileContent.setScanDate(TextStringUtil
                .getFormatTimeString());
        mShipmentFileContent.setShipmentNumber(barcode);
        mShipmentFileContent.setOperateDate(TextStringUtil.getFormatTime());

        // 扫描数据插入数据库
        insertDataToDatabase(mShipmentFileContent);

        // 创建写入文本的字符串，并写入文本
        String content = mShipmentFileContent.getmCurrentValue() + "\r\n";
        mShipmentUploadFile.writeContentToFile(content, true);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ensure: {
                LogUtil.trace();

                DbManager db = BQDataBaseHelper.getDb();
                try {
                    List<ShipmentFileContent> list = db.findAll
                            (ShipmentFileContent.class);
                    if (null != list) {
                        LogUtil.trace("list:" + list.size());
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

                FajianActivity.this.finish();

                break;
            }

            case R.id.btn_back: {
                LogUtil.trace();
                mShipmentUploadFile.uploadFile();
                FajianActivity.this.finish();

                break;
            }
        }
    }

    /**
     * 每次扫描后，先将数据存入数据库，需要的数据可根据ShipmentFileContent对应
     * <p>
     * 与之相关的数据库Table为：fajian
     */
    private void insertDataToDatabase(final ShipmentFileContent
                                              shipmentFileContent) {
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
     * 根据快件类型，查询对应的快件类型编号
     *
     * @param typeString
     * @return
     */
    private String resolveShipmentType(String typeString) {
        LogUtil.trace("typeString:" + typeString);

        String shipmentTypeID = "";
        Iterator iterator = mShipmentDataTmp.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            if (!TextUtils.isEmpty(typeString)) {
                if (value.equals(typeString)) {
                    shipmentTypeID = key;
                }
            }
        }

        return shipmentTypeID;
    }

}