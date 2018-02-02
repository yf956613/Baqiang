package com.jiebao.baqiang.activity;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.adapter.FajianAdatper;
import com.jiebao.baqiang.custView.CouldDeleteListView;
import com.jiebao.baqiang.data.arrival.UnloadArrivalFileContent;
import com.jiebao.baqiang.data.arrival.UnloadArrivalFileName;
import com.jiebao.baqiang.data.bean.FajianListViewBean;
import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
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
    private UploadServerFile mUploadServerFile;

    // 用于更新ListView界面数据，复用发件功能
    private List<FajianListViewBean> mListData;
    private FajianAdatper mFajianAdapter;
    // 此处作为全局扫描次数的记录，用于更新ListView的ID
    private int mScanCount;

    // TODO 测试阶段将 车辆码和上一站写死，只测试扫描和文件上传功能

    @Override
    public void initView() {
        setContent(R.layout.activity_unload_shipment_arrival);
        initHeaderView();
    }

    @Override
    public void initData() {
        mTvVehicleId = UnloadCargoArrivalActivity.this.findViewById(R.id
                .tv_car_code);
        mTvPreviousStation = UnloadCargoArrivalActivity.this.findViewById(R
                .id.tv_before_station);
        mBtnSure = UnloadCargoArrivalActivity.this.findViewById(R.id
                .btn_ensure);
        mBtnCancel = UnloadCargoArrivalActivity.this.findViewById(R.id
                .btn_back);
        mListView = UnloadCargoArrivalActivity.this.findViewById(R.id
                .list_view_scan_data);
        mEtDeliveryNumber = UnloadCargoArrivalActivity.this.findViewById(R.id
                .et_shipment_number);

        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        // 创建待上传文件
        mUnloadArrivalFileName = new UnloadArrivalFileName();
        boolean isAllSuccess = mUnloadArrivalFileName.linkToTXTFile();
        LogUtil.e(TAG, "isAllSuccess:" + isAllSuccess);

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
        mListView.setAdapter(mFajianAdapter);
    }

    public void initHeaderView() {
        setHeaderCenterViewText(getString(R.string.main_check));
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
                break;
            }

            case R.id.btn_back: {
                LogUtil.trace();

                mUploadServerFile.uploadFile();

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

        mUnloadArrivalFileContent.setGoodsType("2");
        mUnloadArrivalFileContent.setScanDate(TextStringUtil
                .getFormatTimeString());
        mUnloadArrivalFileContent.setShipmentType("9");
        mUnloadArrivalFileContent.setShipmentNumber(barcode);
        mUnloadArrivalFileContent.setScanEmployeeNumber("8511801");
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
        String shipmentType = String.valueOf("9");
        // 运单编号
        String shipmentNumber = "";
        // 扫描员工编号
        String scanEmployeeNumber = "5955513";
        // 操作日期
        String operateDate = TextStringUtil.getFormatTime();
        // 重量
        String weight = "0.0";
        // 车辆识别号
        String vehicleID = "G0000150";
        // 是否上传状态
        String status = "未上传";

        return new UnloadArrivalFileContent(previousStation, scanDate,
                goodsType,
                shipmentType, shipmentNumber, scanEmployeeNumber,
                operateDate, weight, vehicleID, status);
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
