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
import com.jiebao.baqiang.data.bean.FajianListViewBean;
import com.jiebao.baqiang.data.bean.LiucangBean;
import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.stay.StayHouseFileContent;
import com.jiebao.baqiang.data.stay.StayHouseFileName;
import com.jiebao.baqiang.data.updateData.UpdateInterface;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

public class LiucangActivity extends BaseActivity implements View
        .OnClickListener {
    private static final String TAG = "LiucangActivity";

    private AutoCompleteTextView mTvStayHouseReason;
    private EditText mEtShipmentNumber;
    private Button mBtnSure, mBtnCancel;
    private CouldDeleteListView mListView;

    // 留仓原因
    private List<LiucangBean> mStayHouseReason;
    // 用在View上的Adapter
    private ArrayAdapter<String> mReasonData;

    // 待上传文件
    private StayHouseFileName mStayHouseFileName;
    // 插入数据库中的一行数据
    private StayHouseFileContent mStayHouseFileContent;
    // 上传文件实体
    private UploadServerFile mUploadServerFile;

    // 用于更新ListView界面数据
    private List<FajianListViewBean> mListData;
    private FajianAdatper mFajianAdapter;
    // 此处作为全局扫描次数的记录，用于更新ListView的ID
    private int mScanCount;

    @Override
    public void initView() {
        setContent(R.layout.liucang);
        setHeaderCenterViewText(getString(R.string.main_import));
    }

    @Override
    public void initData() {
        prepareDataForView();

        mTvStayHouseReason = LiucangActivity.this.findViewById(R.id
                .tv_stay_reason);
        mTvStayHouseReason.setAdapter(mReasonData);
        // 监听EditText是否获取焦点
        mTvStayHouseReason.setOnFocusChangeListener(new View
                .OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 如果当前内容为空，则提示；同时，编辑时自动提示
                    if (TextUtils.isEmpty(mTvStayHouseReason.getText())) {
                        mTvStayHouseReason.showDropDown();
                    }
                } else {
                    LogUtil.trace("mTvNextStation no hasFocus");
                }
            }
        });
        mTvStayHouseReason.setOnItemClickListener(new AdapterView
                .OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int
                    position, long id) {
                // 一旦选定下一站，则解析网点编号，更新ShipmentFileContent实体内容
                String serverID = mTvStayHouseReason.getText().toString();
                LogUtil.d(TAG, "serverID:" + serverID);
                String[] arr = serverID.split("  ");
                // 获取网点编号
                mStayHouseFileContent.setStayReason(arr[0]);
            }
        });

        mBtnSure = LiucangActivity.this.findViewById(R.id.btn_ensure);
        mBtnCancel = LiucangActivity.this.findViewById(R.id.btn_back);
        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mListView = LiucangActivity.this.findViewById(R.id.list_view_scan_data);
        mEtShipmentNumber = LiucangActivity.this.findViewById(R.id
                .et_shipment_number);

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
    }

    private void prepareDataForView() {
        mStayHouseReason = queryStayHouseData();

        List<String> reasonData = new ArrayList<>();
        for (int index = 0; index < mStayHouseReason.size(); index++) {
            reasonData.add(mStayHouseReason.get(index).get编号() + "  " +
                    mStayHouseReason.get(index).get名称());
        }

        mReasonData = new ArrayAdapter<String>
                (LiucangActivity.this, R.layout.list_item, reasonData);
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

    /**
     * 初始化时，先构建一个ShipmentFileContent实体
     *
     * @return
     */
    private StayHouseFileContent getStayHouseFileContent() {
        // 扫描日期
        String scanDate = TextStringUtil.getFormatTimeString();
        // 留仓原因
        String stayHouseReason = "";
        // 快件类型
        String shipmentType = "";
        // 运单编号
        String shipmentNumber = "";
        // 扫描员工编号
        String scanEmployeeNumber = UpdateInterface.userName;
        // 操作日期
        String operateDate = TextStringUtil.getFormatTime();
        // 是否上传状态
        String status = "未上传";

        return new StayHouseFileContent(scanDate, stayHouseReason,
                shipmentType, shipmentNumber, scanEmployeeNumber,
                operateDate, status);
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
        mStayHouseFileContent.setShipmentType("");
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
