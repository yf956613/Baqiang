package com.jiebao.baqiang.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.adapter.FajianAdatper;
import com.jiebao.baqiang.custView.CouldDeleteListView;
import com.jiebao.baqiang.data.bean.FajianListViewBean;
import com.jiebao.baqiang.data.bean.ShipmentType;
import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.dispatch.ShipmentDispatchFileName;
import com.jiebao.baqiang.data.dispatch.ShipmentFileContent;
import com.jiebao.baqiang.util.LogUtil;
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

    private AutoCompleteTextView mTvNextStation, mTvShipmentType;
    private Button mBtnSure, mBtnCancel;
    private CouldDeleteListView mListView;
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

    // 快件类型相关
    private List<ShipmentType> mShipmentTypeList;
    private ArrayAdapter<String> mShipmentType;
    private HashMap<String, String> mShipmentDataTmp;

    @Override
    public void initView() {
        setContent(R.layout.fajian);
        initHeaderView();
    }

    public void initHeaderView() {
        setHeaderCenterViewText(getString(R.string.main_output));
    }

    @Override
    public void initData() {
        mTvNextStation = FajianActivity.this.findViewById(R.id.tv_next_station);
        mTvShipmentType = FajianActivity.this.findViewById(R.id
                .tv_shipment_type);
        mBtnSure = FajianActivity.this.findViewById(R.id.btn_ensure);
        mBtnCancel = FajianActivity.this.findViewById(R.id.btn_back);
        mListView = FajianActivity.this.findViewById(R.id.list_view_scan_data);
        mEtShipmentNumber = FajianActivity.this.findViewById(R.id
                .et_shipment_number);

        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mShipmentDispatchFileName = new ShipmentDispatchFileName();
        boolean isAllSuccess = mShipmentDispatchFileName.linkToTXTFile();
        LogUtil.e(TAG, "isAllSuccess:" + isAllSuccess);

        mShipmentFileContent = getShipmentFileContent();
        LogUtil.trace("mShipmentFileContent:" + mShipmentFileContent.toString
                ());
        mShipmentUploadFile = new UploadServerFile
                (mShipmentDispatchFileName.getFileInstance());


        /*DbManager.DaoConfig mDaoConfig = BQDataBaseHelper.getDaoConfig();
        DbManager dbManager = x.getDb(mDaoConfig);
        try {
            List<SalesService> list = dbManager.findAll(SalesService.class);
            if(list!=null) {
                for (int index = 0; index < list.size(); index++) {
                    LogUtil.trace("childInfo::" + list.get(index).get网点名称());
                }
            }
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }*/

        mListData = new ArrayList<>();
        mFajianAdapter = new FajianAdatper(FajianActivity.this, mListData);
        mListView.setAdapter(mFajianAdapter);

        resolveShipmentTypeData();
        mTvShipmentType.setAdapter(mShipmentType);
    }


    /**
     * 构造快件类型的填充数据
     */
    private void resolveShipmentTypeData() {
        mShipmentTypeList = queryShipmentTypeData();

        mShipmentDataTmp = new HashMap<>();
        List<String> mShipmentData = new ArrayList<>();
        for (int index = 0; index < mShipmentTypeList.size(); index++) {
            mShipmentData.add(mShipmentTypeList.get(index).get类型名称());
            mShipmentDataTmp.put(mShipmentTypeList.get(index).get类型编号(),
                    mShipmentTypeList.get(index).get类型名称());
        }

        LogUtil.trace("size:" + mShipmentData.size());
        mShipmentType = new ArrayAdapter<String>
                (FajianActivity.this, android.R.layout.simple_list_item_1,
                        mShipmentData);
    }

    @Override
    protected void fillCode(String barcode) {
        super.fillCode(barcode);
        LogUtil.trace("barcode:" + barcode);
        mEtShipmentNumber.setText(barcode);

        // 更新ListView的数据
        FajianListViewBean mFajianListViewBean = new FajianListViewBean();
        mFajianListViewBean.setId(++mScanCount);
        mFajianListViewBean.setScannerData(barcode);
        mFajianListViewBean.setStatus("未上传");
        mListData.add(mFajianListViewBean);
        mFajianAdapter.notifyDataSetChanged();

        mShipmentFileContent.setGoodsType("2");
        mShipmentFileContent.setScanDate(TextStringUtil
                .getFormatTimeString());
        mShipmentFileContent.setShipmentType(resolveShipmentType
                (mTvShipmentType.getText().toString()));
        mShipmentFileContent.setShipmentNumber(barcode);
        mShipmentFileContent.setScanEmployeeNumber("8511801");
        mShipmentFileContent.setOperateDate(TextStringUtil.getFormatTime());

        LogUtil.trace(mShipmentFileContent.toString());

        insertDataToDatabase(mShipmentFileContent);

        // 根据数据看数据，构造上传文件
        String content = mShipmentFileContent.getmCurrentValue() + "\r\n";
        LogUtil.trace("content:" + content + ";");
        mShipmentUploadFile.writeContentToFile(content, true);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ensure: {
                LogUtil.trace();

                DbManager db = BQDataBaseHelper.getDb();
                try {
                    List<ShipmentFileContent> list = db.findAll
                            (ShipmentFileContent.class);
                    if(null != list){
                        LogUtil.trace("list:" + list.size());
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            }

            case R.id.btn_back: {
                LogUtil.trace();
                mShipmentUploadFile.uploadFile();

                break;
            }
        }
    }

    /**
     * 初始化时，先构建一个ShipmentFileContent实体
     *
     * @return
     */
    private ShipmentFileContent getShipmentFileContent() {
        // TODO 下一站点编号  模拟
        String nextStation = String.valueOf(/*mTvNextStation.getText()
        */"59406");

        // 扫描日期
        String scanDate = TextStringUtil.getFormatTimeString();
        // 物品类型
        String goodsType = "";
        // 快件类型
        String shipmentType = String.valueOf(mTvShipmentType.getText());
        // 运单编号fill
        String shipmentNumber = "";
        // 扫描员工编号
        String scanEmployeeNumber = "5955513";
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
