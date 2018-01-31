package com.jiebao.baqiang.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.adapter.FajianAdatper;
import com.jiebao.baqiang.custView.CouldDeleteListView;
import com.jiebao.baqiang.data.ShipmentDispatch.ShipmentDispatchFileName;
import com.jiebao.baqiang.data.ShipmentDispatch.ShipmentFileContent;
import com.jiebao.baqiang.data.ShipmentDispatch.ShipmentUploadFile;
import com.jiebao.baqiang.data.bean.FajianListViewBean;
import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class FajianActivity extends BaseActivity implements View
        .OnClickListener {
    private static final String TAG = "FajianActivity";

    private TextView mTvNextStation, mTvShipmentType;
    private Button mBtnSure, mBtnCancel;
    private CouldDeleteListView mListView;
    private EditText mEtShipmentNumber;

    private ShipmentDispatchFileName mShipmentDispatchFileName;
    private ShipmentFileContent mShipmentFileContent;
    private ShipmentUploadFile mShipmentUploadFile;

    private List<FajianListViewBean> mListData;

    // 每次扫描时，创建ShipmentFileContent对象，其中包括扫描时间
    private List<ShipmentFileContent> mData = new ArrayList<>();
    private FajianAdatper mFajianAdapter;

    // 此处作为全局扫描次数的记录，用于更新ListView的ID
    private int mScanCount;

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

        /*mShipmentFileContent = getShipmentFileContent();
        mShipmentUploadFile = new ShipmentUploadFile
                (mShipmentDispatchFileName.getFileInstance());*/


        DbManager.DaoConfig mDaoConfig = BQDataBaseHelper.getDaoConfig();
        DbManager dbManager = x.getDb(mDaoConfig);
        try {
            List<SalesService> list = dbManager.findAll(SalesService.class);
            for (int index = 0; index < list.size(); index++) {
                LogUtil.trace("childInfo::" + list.get(index).get网点名称());
            }
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }

        mListData = new ArrayList<>();
        mFajianAdapter = new FajianAdatper(FajianActivity.this, mListData);
        mListView.setAdapter(mFajianAdapter);
    }

    @Override
    protected void fillCode(String barcode) {
        super.fillCode(barcode);
        LogUtil.trace("barcode:" + barcode);
        mEtShipmentNumber.setText(barcode);

        // 构造 待写入数据库的 数据
        ShipmentFileContent shipmentFileContent = getShipmentFileContent
                (barcode);
        mData.add(shipmentFileContent);

        // 更新ListView的数据
        FajianListViewBean mFajianListViewBean = new FajianListViewBean();
        mFajianListViewBean.setId(++mScanCount);
        mFajianListViewBean.setScannerData(barcode);
        mFajianListViewBean.setStatus("未上传");
        mListData.add(mFajianListViewBean);
        mFajianAdapter.notifyDataSetChanged();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok_button: {
                /*mShipmentFileContent.scanDateChanged(TextStringUtil
                        .getFormatTimeString());
                LogUtil.d(TAG, mShipmentFileContent.toString());

                mShipmentUploadFile.writeContentToFile(mShipmentFileContent
                        .getmCurrentValue() +
                        "\r\n", true);
                mShipmentUploadFile.uploadFile();*/

                insertDataToDatabase();
                break;
            }

            case R.id.cancel_button:
                break;
        }

    }

    /**
     * 初始化时，先构建一个ShipmentFileContent实体
     *
     * @return
     */
    private ShipmentFileContent getShipmentFileContent(String scanData) {
        // 下一站
        String nextStation = String.valueOf(mTvNextStation.getText());
        // 扫描日期
        String scanDate = TextStringUtil.getFormatTimeString();
        // 物品类型
        String goodsType = "";
        // 快件类型
        String shipmentType = String.valueOf(mTvShipmentType.getText());
        // 运单编号
        String shipmentNumber = scanData;
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
    private void insertDataToDatabase() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                DbManager db = BQDataBaseHelper.getDb();
                try {
                    for (int index = 0; index < mData.size(); index++) {
                        db.save(mData.get(index));
                    }
                } catch (DbException e) {
                    LogUtil.trace(e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
