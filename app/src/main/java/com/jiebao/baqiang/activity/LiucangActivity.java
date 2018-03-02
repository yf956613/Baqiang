package com.jiebao.baqiang.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

public class LiucangActivity extends BaseActivity implements View
        .OnClickListener, CouldDeleteListView.DelButtonClickListener {
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
        mListView.setAdapter(mFajianAdapter);
        mListView.setDelButtonClickListener(LiucangActivity.this);
        mEtShipmentNumber = LiucangActivity.this.findViewById(R.id
                .et_shipment_number);

        // 初次启动时刷新数据
        reQueryUnUploadDataForListView();
    }

    /**
     * 从数据库中找出所有未上传记录
     */
    private void reQueryUnUploadDataForListView() {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            // 查询数据库中标识位“未上传”的记录
            List<StayHouseFileContent> data = db.selector
                    (StayHouseFileContent.class).where("是否上传",
                    "like", "未上传").findAll();
            LogUtil.d(TAG, "未上传记录：" + data.size());

            // 清除数据
            mListData.clear();

            int count = 0;
            for (int index = 0; index < data.size(); index++) {
                FajianListViewBean fajianListViewBean = new
                        FajianListViewBean();
                // TODO 一旦删除记录，则及时更新ID值
                fajianListViewBean.setId(++count);
                fajianListViewBean.setScannerData(data.get(index)
                        .getShipmentNumber());
                fajianListViewBean.setStatus("未上传");
                mListData.add(fajianListViewBean);
            }

            mFajianAdapter.notifyDataSetChanged();
            // 更新全局ID
            mScanCount = count;
        } catch (DbException e) {
            e.printStackTrace();
        }
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

        mStayHouseFileName = new StayHouseFileName();
        boolean isAllSuccess = mStayHouseFileName.linkToTXTFile();
        LogUtil.d(TAG, "isAllSccess:" + isAllSuccess);

        mStayHouseFileContent = getStayHouseFileContent();
        LogUtil.trace("mStayHouseFileContent:" + mStayHouseFileContent.toString
                ());
        mUploadServerFile = new UploadServerFile
                (mStayHouseFileName.getFileInstance());

        mListData = new ArrayList<>();
        mFajianAdapter = new FajianAdatper(LiucangActivity.this, mListData);
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
                // 确定按键，上传文件
                LogUtil.trace();

                DbManager db = BQDataBaseHelper.getDb();
                List<StayHouseFileContent> list = null;
                try {
                    // 1. 查询数据库中标识位“未上传”的记录
                    list = db.selector(StayHouseFileContent.class).where("是否上传",
                            "like", "未上传").findAll();
                    if (null != list) {
                        LogUtil.trace("list:" + list.size());
                        for (int index = 0; index < list.size(); index++) {
                            // 2. 创建写入文本的字符串，并写入文本
                            StayHouseFileContent javaBean = list.get(index);
                            String content = javaBean.getmCurrentValue() +
                                    "\r\n";
                            if (mUploadServerFile.writeContentToFile(content,
                                    true)) {
                                // 3. 写入成功，删除记录
                                /*WhereBuilder whereBuilder = WhereBuilder.b();
                                whereBuilder.and("运单编号", "=", javaBean
                                        .getShipmentNumber());
                                db.update(ShipmentFileContent.class,
                                        whereBuilder, new KeyValue("是否上传",
                                                "已上传"));*/
                                WhereBuilder b = WhereBuilder.b();
                                b.and("运单编号", "=", javaBean.getShipmentNumber
                                        ());
                                db.delete(StayHouseFileContent.class, b);
                            } else {
                                // 写入文件失败，跳过
                            }
                        }
                    }
                } catch (DbException e) {
                    LogUtil.d(TAG, "崩溃信息:" + e.getLocalizedMessage());
                    e.printStackTrace();
                }

                // 4. 文件上传服务器
                mUploadServerFile.uploadFile();
                LiucangActivity.this.finish();

                break;
            }

            case R.id.btn_back: {
                // 返回按键，不上传文件
                LogUtil.trace();
                LiucangActivity.this.finish();

                // 测试阶段删除所有记录
                DbManager db = BQDataBaseHelper.getDb();
                try {
                    List<StayHouseFileContent> list = db.findAll
                            (StayHouseFileContent.class);
                    if (list != null) {
                        LogUtil.d(TAG, "当前记录：" + list.size());
                        // db.delete(ShipmentFileContent.class);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    @Override
    protected void fillCode(String barcode) {
        super.fillCode(barcode);
        LogUtil.trace("barcode:" + barcode);

        // TODO 判断前置条件是否符合
        if (TextUtils.isEmpty(mTvStayHouseReason.getText().toString()) ||
                TextUtils.isEmpty(mTvStayHouseReason.getText().toString())) {
            Toast.makeText(LiucangActivity.this, "前置信息为空", Toast.LENGTH_SHORT)
                    .show();

            return;
        }

        // 1. 查表：当前是名为fajian的表，判断是否有记录
        if (isExistCurrentBarcode(barcode)) {
            // 若有记录则提示重复；若没有，继续执行
            Toast.makeText(LiucangActivity.this, "运单号已存在", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // 2. 插入到数据库中
        mStayHouseFileContent.setScanDate(TextStringUtil
                .getFormatTimeString());
        mStayHouseFileContent.setShipmentType("");
        mStayHouseFileContent.setShipmentNumber(barcode);
        mStayHouseFileContent.setOperateDate(TextStringUtil.getFormatTime());
        insertDataToDatabase(mStayHouseFileContent);

        // 3. 填充EditText控件
        mEtShipmentNumber.setText(barcode);

        // 4. 更新ListView的数据
        FajianListViewBean mFajianListViewBean = new FajianListViewBean();
        mFajianListViewBean.setId(++mScanCount);
        mFajianListViewBean.setScannerData(barcode);
        mFajianListViewBean.setStatus("未上传");

        mListData.add(mFajianListViewBean);
        mFajianAdapter.notifyDataSetChanged();

        /*// 根据数据看数据，构造上传文件
        String content = mStayHouseFileContent.getmCurrentValue() + "\r\n";
        LogUtil.trace("content:" + content + ";");
        mUploadServerFile.writeContentToFile(content, true);*/
    }

    private static final String DB_NAME = "liucangjian";

    /**
     * 判断数据库中是否有当前运单记录
     * @param barcode
     * @return
     */
    private boolean isExistCurrentBarcode(String barcode) {
        if (tableIsExist(DB_NAME)) {
            // 存在保存发件数据的表，从该表中查询对应的单号
            DbManager dbManager = BQDataBaseHelper.getDb();
            try {
                // 查询数据库，是否有记录
                List<StayHouseFileContent> bean = dbManager.selector
                        (StayHouseFileContent.class).where("运单编号",
                        "like", barcode).limit(1).findAll();
                LogUtil.trace("bean:" + bean.size());

                if (bean != null && bean.size() != 0) {
                    return true;
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 查询数据库文件中是否有车辆信息表
     *
     * @return false：没有该数据表；true：存在该数据表
     */
    public boolean tableIsExist(String tableName) {
        LogUtil.trace("tableName:" + tableName);
        boolean result = false;

        if (tableName == null) {
            return false;
        }

        DbManager dbManager = BQDataBaseHelper.getDb();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbManager.getDatabase();
            // 查询内置sqlite_master表，判断是否创建了对应表
            String sql = "select count(*) from sqlite_master where type " +
                    "='table' and name ='" +
                    tableName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
        } catch (Exception e) {
            LogUtil.trace(e.getMessage());
            // TODO: handle exception
        }

        return result;
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

    @Override
    public void clickHappend(int position) {
        // 删除按键
        LogUtil.trace("position:" + position);

        // 1. 找到当前position的运单号
        LogUtil.d(TAG, "待删除的内容:" + mListData.get(position).getScannerData());

        // 2. 删除数据库中对应的记录
        deleteFindedBean(mListData.get(position).getScannerData());

        // 3. 重新从数据库中查出所有记录,更新ListView
        reQueryUnUploadDataForListView();
    }

    /**
     * 点击删除按键后，删除对应的数据项
     *
     * @param barcode
     */
    private void deleteFindedBean(final String barcode) {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            db.delete(StayHouseFileContent.class, WhereBuilder.b("运单编号",
                    "like", barcode));
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }
    }
}
