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
import com.jiebao.baqiang.data.bean.LiucangBean;
import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.stay.StayHouseFileContent;
import com.jiebao.baqiang.data.stay.StayHouseFileName;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by open on 2018/1/22.
 */

public class LiucangActivity extends BaseActivity implements View
        .OnClickListener {
    private static final String TAG = "LiucangActivity";

    private AutoCompleteTextView mTvStayHouseReason;
    private Button mBtnSure, mBtnCancel;
    private CouldDeleteListView mListView;
    private EditText mEtShipmentNumber;

    private StayHouseFileName mStayHouseFileName;
    // 插入数据库中的一行数据
    private StayHouseFileContent mStayHouseFileContent;
    private UploadServerFile mUploadServerFile;

    // 用于更新ListView界面数据
    private List<FajianListViewBean> mListData;
    private FajianAdatper mFajianAdapter;
    // 此处作为全局扫描次数的记录，用于更新ListView的ID
    private int mScanCount;

    // 留仓原因
    private List<LiucangBean> mStayHouseReason;
    private HashMap<String, String> mStayHouseTmp;
    // 用在View上的Adapter
    private ArrayAdapter<String> mReasonData;

    @Override
    public void initView() {
        setContent(R.layout.liucang);
        initHeaderView();
    }

    public void initHeaderView() {
        setHeaderCenterViewText(getString(R.string.main_import));
    }

    @Override
    public void initData() {
        resolveStayReasonData();

        mTvStayHouseReason = LiucangActivity.this.findViewById(R.id
                .tv_stay_reason);
        mBtnSure = LiucangActivity.this.findViewById(R.id.btn_ensure);
        mBtnCancel = LiucangActivity.this.findViewById(R.id.btn_back);
        mListView = LiucangActivity.this.findViewById(R.id.list_view_scan_data);
        mEtShipmentNumber = LiucangActivity.this.findViewById(R.id
                .et_shipment_number);

        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mStayHouseFileName = new StayHouseFileName();
        boolean isAllSuccess = mStayHouseFileName.linkToTXTFile();
        LogUtil.e(TAG, "isAllSuccess:" + isAllSuccess);

        mStayHouseFileContent = getStayHouseFileContent();
        LogUtil.trace("mStayHouseFileContent:" + mStayHouseFileContent.toString
                ());
        mUploadServerFile = new UploadServerFile
                (mStayHouseFileName.getFileInstance());

        mListData = new ArrayList<>();
        mFajianAdapter = new FajianAdatper(LiucangActivity.this, mListData);
        mListView.setAdapter(mFajianAdapter);


        mTvStayHouseReason.setAdapter(mReasonData);
    }

    /**
     * 初始化时，先构建一个ShipmentFileContent实体
     *
     * @return
     */
    private StayHouseFileContent getStayHouseFileContent() {
        // 扫描日期
        String scanDate = TextStringUtil.getFormatTimeString();
        // 留仓原因
        String stayHouseReason = String.valueOf(mTvStayHouseReason.getText());
        // 快件类型
        String shipmentType = String.valueOf("2");
        // 运单编号
        String shipmentNumber = "";
        // 扫描员工编号
        String scanEmployeeNumber = "5955513";
        // 操作日期
        String operateDate = TextStringUtil.getFormatTime();
        // 是否上传状态
        String status = "未上传";

        return new StayHouseFileContent(scanDate, stayHouseReason,
                shipmentType, shipmentNumber, scanEmployeeNumber,
                operateDate, status);
    }

    /**
     * 根据快件类型，查询对应的快件类型编号
     *
     * @param typeString
     * @return
     */
    private String resolveReason(String typeString) {
        LogUtil.trace("typeString:" + typeString);

        String shipmentTypeID = "";

        Iterator iterator = mStayHouseTmp.entrySet().iterator();
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

    /**
     * 构造留仓原因的填充数据
     */
    private void resolveStayReasonData() {
        LogUtil.trace();

        mStayHouseReason = queryStayHouseData();

        mStayHouseTmp = new HashMap<>();
        List<String> reasonData = new ArrayList<>();
        for (int index = 0; index < mStayHouseReason.size(); index++) {
            reasonData.add(mStayHouseReason.get(index).get名称());
            mStayHouseTmp.put(mStayHouseReason.get(index).get编号(),
                    mStayHouseReason.get(index).get名称());
        }
        LogUtil.trace("size:" + reasonData.size()+"-->"+ reasonData);

        mReasonData = new ArrayAdapter<String>
                (LiucangActivity.this, android.R.layout.simple_list_item_1,
                        reasonData);
    }

    /**
     * 从数据库中取出留仓原因数据
     *
     * @return
     */
    private List<LiucangBean> queryStayHouseData() {
        List<LiucangBean> mData = null;
        DbManager dbManager = BQDataBaseHelper.getDb();
        try {
            mData = dbManager.findAll(LiucangBean.class);
            if (mData != null) {
                LogUtil.trace("List<LiucangBean>::" + mData.size());
            }
        } catch (DbException e) {
            LogUtil.trace();
            e.printStackTrace();
        }
        return mData;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ensure: {
                LogUtil.trace();

                DbManager db = BQDataBaseHelper.getDb();
                try {
                    List<StayHouseFileContent> list = db.findAll
                            (StayHouseFileContent.class);
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
        LogUtil.trace("barcode:" + barcode);
        mEtShipmentNumber.setText(barcode);

        // 更新ListView的数据
        FajianListViewBean mFajianListViewBean = new FajianListViewBean();
        mFajianListViewBean.setId(++mScanCount);
        mFajianListViewBean.setScannerData(barcode);
        mFajianListViewBean.setStatus("未上传");
        mListData.add(mFajianListViewBean);
        mFajianAdapter.notifyDataSetChanged();

        mStayHouseFileContent.setScanDate(TextStringUtil
                .getFormatTimeString());
        mStayHouseFileContent.setStayReason(resolveReason(mTvStayHouseReason
                .getText().toString()));
        mStayHouseFileContent.setShipmentType("2");
        mStayHouseFileContent.setShipmentNumber(barcode);
        mStayHouseFileContent.setScanEmployeeNumber("8511801");
        mStayHouseFileContent.setOperateDate(TextStringUtil.getFormatTime());

        LogUtil.trace(mStayHouseFileContent.toString());

        insertDataToDatabase(mStayHouseFileContent);

        // 根据数据看数据，构造上传文件
        String content = mStayHouseFileContent.getmCurrentValue() + "\r\n";
        LogUtil.trace("content:" + content + ";");
        mUploadServerFile.writeContentToFile(content, true);
    }

    /**
     * 每次扫描后，先将数据存入数据库，需要的数据可根据ShipmentFileContent对应
     * <p>
     * 与之相关的数据库Table为：fajian
     */
    private void insertDataToDatabase(final StayHouseFileContent
                                              stayHouseFileContent) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                DbManager db = BQDataBaseHelper.getDb();
                try {
                    db.save(stayHouseFileContent);
                } catch (DbException e) {
                    LogUtil.trace(e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
