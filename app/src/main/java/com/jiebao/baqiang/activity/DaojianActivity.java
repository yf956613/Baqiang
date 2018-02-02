package com.jiebao.baqiang.activity;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.adapter.FajianAdatper;
import com.jiebao.baqiang.custView.CouldDeleteListView;
import com.jiebao.baqiang.data.arrival.CargoArrivalFileContent;
import com.jiebao.baqiang.data.arrival.CargoArrivalFileName;
import com.jiebao.baqiang.data.bean.FajianListViewBean;
import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

public class DaojianActivity extends BaseActivity implements View
        .OnClickListener {

    private static final String TAG = UnloadCargoArrivalActivity.class
            .getSimpleName();

    private AutoCompleteTextView mTvPreviousStation;
    private Button mBtnSure, mBtnCancel;
    private CouldDeleteListView mListView;
    private EditText mEtDeliveryNumber;

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
        initHeaderView();
    }

    public void initHeaderView() {
        setHeaderCenterViewText(getString(R.string.main_query));
    }

    @Override
    public void initData() {
        mTvPreviousStation = DaojianActivity.this.findViewById(R
                .id.tv_before_station);
        mBtnSure = DaojianActivity.this.findViewById(R.id
                .btn_ensure);
        mBtnCancel = DaojianActivity.this.findViewById(R.id
                .btn_back);
        mListView = DaojianActivity.this.findViewById(R.id
                .list_view_scan_data);
        mEtDeliveryNumber = DaojianActivity.this.findViewById(R.id
                .et_shipment_number);

        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        // 创建待上传文件
        mCargoArrivalFileName = new CargoArrivalFileName();
        boolean isAllSuccess = mCargoArrivalFileName.linkToTXTFile();
        LogUtil.e(TAG, "isAllSuccess:" + isAllSuccess);

        // 组装写入文件数据
        mCargoArrivalFileContent = getCargoArrivalFileContent();
        LogUtil.trace("mUnloadArrivalFileContent:" +
                mCargoArrivalFileContent.toString());
        // 上传文件实体
        mUploadServerFile = new UploadServerFile(mCargoArrivalFileName
                .getFileInstance());

        mListData = new ArrayList<>();
        mFajianAdapter = new FajianAdatper(DaojianActivity.this,
                mListData);
        mListView.setAdapter(mFajianAdapter);
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
                break;
            }

            case R.id.btn_back: {
                LogUtil.trace();

                mUploadServerFile.uploadFile();

                break;
            }
        }
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
        String shipmentType = String.valueOf("9");
        // 运单编号
        String shipmentNumber = "";
        // 扫描员工编号
        String scanEmployeeNumber = "5955513";
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

        mCargoArrivalFileContent.setGoodsType("2");
        mCargoArrivalFileContent.setScanDate(TextStringUtil
                .getFormatTimeString());
        mCargoArrivalFileContent.setShipmentType("9");
        mCargoArrivalFileContent.setShipmentNumber(barcode);
        mCargoArrivalFileContent.setScanEmployeeNumber("8511801");
        mCargoArrivalFileContent.setOperateDate(TextStringUtil.getFormatTime
                ());
        LogUtil.trace(mCargoArrivalFileContent.toString());

        // 数据存入数据库
        insertDataToDatabase(mCargoArrivalFileContent);

        // 根据JavaBean实体组装数据
        String content = mCargoArrivalFileContent.getmCurrentValue() + "\r\n";
        LogUtil.trace("content:" + content + ";");
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
