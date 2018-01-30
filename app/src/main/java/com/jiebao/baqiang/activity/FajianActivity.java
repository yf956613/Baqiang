package com.jiebao.baqiang.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.data.ShipmentDispatch.ShipmentDispatchFileName;
import com.jiebao.baqiang.data.ShipmentDispatch.ShipmentFileContent;
import com.jiebao.baqiang.data.ShipmentDispatch.ShipmentUploadFile;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

/**
 * Created by open on 2018/1/22.
 */

public class FajianActivity extends BaseActivity implements View.OnClickListener{

    Button ok_button,cancel_button;
    private ShipmentDispatchFileName mShipmentDispatchFileName;
    private ShipmentFileContent mShipmentFileContent;
    private ShipmentUploadFile mShipmentUploadFile;
    private static final String TAG="FajianActivity";
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
        ok_button = (Button)findViewById(R.id.ok_button);
        cancel_button = (Button)findViewById(R.id.cancel_button);
        ok_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);
        mShipmentDispatchFileName = new ShipmentDispatchFileName();
        boolean isAllSuccess = mShipmentDispatchFileName.linkToTXTFile();
        LogUtil.d(TAG, "isAllSuccess:" + isAllSuccess);

        mShipmentFileContent = getShipmentFileContent();

        LogUtil.trace(mShipmentFileContent.toString());

        mShipmentUploadFile = new ShipmentUploadFile
                (mShipmentDispatchFileName.getFileInstance());
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.ok_button:
                mShipmentFileContent.scanDateChanged(TextStringUtil
                        .getFormatTimeString());
                LogUtil.d(TAG, mShipmentFileContent.toString());

                mShipmentUploadFile.writeContentToFile(mShipmentFileContent
                        .getmCurrentValue() + "\r\n", true);
                mShipmentUploadFile.uploadFile();
                break;
            case R.id.cancel_button:
                break;

        }

    }
    protected void onCreate(Bundle savedInstanceState){
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

        return new ShipmentFileContent(nextStation, scanDate, goodsType,
                shipmentType, shipmentNumber, scanEmployeeNumber,
                operateDate, weight);
    }
}
