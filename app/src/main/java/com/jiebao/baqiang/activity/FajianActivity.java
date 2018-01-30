package com.jiebao.baqiang.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.data.ShipmentDispatch.ShipmentDispatchFileName;
import com.jiebao.baqiang.data.ShipmentDispatch.ShipmentFileContent;
import com.jiebao.baqiang.data.ShipmentDispatch.ShipmentUploadFile;
import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

/**
 * Created by open on 2018/1/22.
 */

public class FajianActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "FajianActivity";

    private Button mBtnSure, mBtnCancel;
    private ShipmentDispatchFileName mShipmentDispatchFileName;
    private ShipmentFileContent mShipmentFileContent;
    private ShipmentUploadFile mShipmentUploadFile;

    @Override
    public void initView() {
        setContent(R.layout.fajian);
        initHeaderView();
    }

    public void initHeaderView() {
        setHeaderCenterViewText(getString(R.string.main_output));
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity, "android.permission" +
                    ".WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initData() {
        verifyStoragePermissions(FajianActivity.this);

        mBtnSure = (Button) findViewById(R.id.ok_button);
        mBtnCancel = (Button) findViewById(R.id.cancel_button);
        mBtnSure.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mShipmentDispatchFileName = new ShipmentDispatchFileName();
        boolean isAllSuccess = mShipmentDispatchFileName.linkToTXTFile();
        LogUtil.e(TAG, "isAllSuccess:" + isAllSuccess);

        mShipmentFileContent = getShipmentFileContent();
        LogUtil.trace(mShipmentFileContent.toString());
        mShipmentUploadFile = new ShipmentUploadFile(mShipmentDispatchFileName.getFileInstance());


        DbManager.DaoConfig mDaoConfig = BQDataBaseHelper.getDaoConfig();
        DbManager dbManager = x.getDb(mDaoConfig);
        try {
            SalesService childInfo = dbManager.findFirst(SalesService.class);
            LogUtil.trace("childInfo::" + childInfo.get网点名称());
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok_button:
                mShipmentFileContent.scanDateChanged(TextStringUtil.getFormatTimeString());
                LogUtil.d(TAG, mShipmentFileContent.toString());

                mShipmentUploadFile.writeContentToFile(mShipmentFileContent.getmCurrentValue() +
                        "\r\n", true);
                mShipmentUploadFile.uploadFile();
                break;
            case R.id.cancel_button:
                break;

        }

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * 初始化时，先构建一个ShipmentFileContent实体
     *
     * @return
     */
    private ShipmentFileContent getShipmentFileContent() {
        LogUtil.trace();

        String nextStation = "259200";
        String scanDate = TextStringUtil.getFormatTimeString();
        String goodsType = "";
        String shipmentType = "2";
        String shipmentNumber = "880273772877";
        String scanEmployeeNumber = "5955513";
        String operateDate = TextStringUtil.getFormatTime();
        String weight = "";

        return new ShipmentFileContent(nextStation, scanDate, goodsType, shipmentType,
                shipmentNumber, scanEmployeeNumber, operateDate, weight);
    }
}
