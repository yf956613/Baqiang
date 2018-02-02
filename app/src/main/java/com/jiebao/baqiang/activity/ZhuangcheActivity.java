package com.jiebao.baqiang.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.adapter.FajianAdatper;
import com.jiebao.baqiang.data.ShipmentDispatch.ShipmentFileContent;
import com.jiebao.baqiang.data.bean.FajianListViewBean;
import com.jiebao.baqiang.data.bean.LiucangBean;
import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.data.bean.ShipmentType;
import com.jiebao.baqiang.data.bean.VehicleInfo;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianDispatchFileName;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCfajianUploadFile;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;
import com.jiebao.baqiang.view.JBItemEdit;

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

public class ZhuangcheActivity  extends BaseActivity implements View.OnClickListener{

    private static final String TAG="ZhuangcheActivity";
    private JBItemEdit next_station,express_type,car_code,tracking_numb;
    List<String> vehicleInfo =new ArrayList<>();
    List<String> salesService =new ArrayList<>();
   // List<String> shipmentTyoe =new ArrayList<>();
    List<String>  liucang = new ArrayList<>();
    Button ok_button,cancel_button;
    private ListView mListView;
    // 用于更新ListView界面数据
    private List<FajianListViewBean> mListData;
    private FajianAdatper mFajianAdapter;
    // 此处作为全局扫描次数的记录，用于更新ListView的ID
    private int mScanCount;

    //往file 中写文件
    ZCFajianDispatchFileName zcFajianDispatchFileName ;
    ZCFajianFileContent zcFajianFileContent;
    ZCfajianUploadFile zCfajianUploadFile;

    // 快件类型相关
    private List<ShipmentType> mShipmentTypeList;
    private ArrayAdapter<String> mShipmentType;
    List<String> mShipmentData = new ArrayList<>();
    private HashMap<String, String> mShipmentDataTmp;

    @Override
    public void initView() {
        setContent(R.layout.zhuangchefajian);
        initHeaderView();
    }

    public void initHeaderView() {
        setHeaderCenterViewText(getString(R.string.main_storge));
    }

    @Override
    public void initData() {
        car_code =(JBItemEdit)findViewById(R.id.car_code);
        next_station =(JBItemEdit)findViewById(R.id.next_station);
        express_type =(JBItemEdit)findViewById(R.id.express_type);
        tracking_numb =(JBItemEdit)findViewById(R.id.tracking_numb);
        ok_button = (Button) findViewById(R.id.ok_button);
        cancel_button = (Button)findViewById(R.id.cancel_button);
        ok_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);
        mListView = (ListView)findViewById(R.id.list_view_scan_data);
        mListData = new ArrayList<>();
        mFajianAdapter = new FajianAdatper(ZhuangcheActivity.this, mListData);
        mListView.setAdapter(mFajianAdapter);


        zcFajianFileContent=getZCFajianFileContent();
        zcFajianDispatchFileName =new ZCFajianDispatchFileName();
        boolean isAllSuccess = zcFajianDispatchFileName.linkToTXTFile();
        LogUtil.e(TAG, "isAllSuccess:" + isAllSuccess);
        zCfajianUploadFile =new ZCfajianUploadFile(zcFajianDispatchFileName.getFileInstance());
    }

    /**
     * 初始化时，先构建一个ZCFajianFileContent实体
     *
     * @return
     */
    private ZCFajianFileContent getZCFajianFileContent() {
        // TODO 下一站点编号  模拟
        String nextStation = String.valueOf(/*mTvNextStation.getText()
        */"59406");

        // 扫描日期
        String scanDate = TextStringUtil.getFormatTimeString();
        // 物品类型
        String goodsType = "";
        // 快件类型
        String shipmentType = String.valueOf(express_type.getRightText().getText());
        // 运单编号
        String shipmentNumber = "";
        // 扫描员工编号
        String scanEmployeeNumber = "5955513";
        // 操作日期
        String operateDate = TextStringUtil.getFormatTime();
        // 重量
        String weight = "0.0";
        //车辆识别码
        String identify="";
        // 是否上传状态
        String status = "未上传";

        return new ZCFajianFileContent(nextStation, scanDate, goodsType,
                shipmentType, shipmentNumber, scanEmployeeNumber,
                operateDate, weight,identify, status);
    }
    protected void fillCode(String barcode) {
        tracking_numb.getRightText().setText(barcode);
        // 更新ListView的数据
        FajianListViewBean mFajianListViewBean = new FajianListViewBean();
        mFajianListViewBean.setId(++mScanCount);
        mFajianListViewBean.setScannerData(barcode);
        mFajianListViewBean.setStatus("未上传");
        mListData.add(mFajianListViewBean);
        mFajianAdapter.notifyDataSetChanged();

        //把扫到的内容更行到数据库
        zcFajianFileContent.setGoodsType("2");
        zcFajianFileContent.setScanDate(TextStringUtil
                .getFormatTimeString());
        zcFajianFileContent.setShipmentType(resolveShipmentType
                (express_type.getRightText().getText().toString()));
        zcFajianFileContent.setShipmentNumber(barcode);
        zcFajianFileContent.setScanEmployeeNumber("8511801");
        zcFajianFileContent.setOperateDate(TextStringUtil.getFormatTime());
        zcFajianFileContent.setIdentify(car_code.getRightText().getText().toString());
        LogUtil.trace(zcFajianFileContent.toString());

        insertDataToDatabase(zcFajianFileContent);

        // 根据数据看数据，构造上传文件
        String content = zcFajianFileContent.getmCurrentValue() + "\r\n";
        LogUtil.trace("content:" + content + ";");
        zCfajianUploadFile.writeContentToFile(content, true);
    }
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    protected void onResume(){
        super.onResume();
        new MyAsyncTaskLoader().execute();
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
    /**
     * 每次扫描后，先将数据存入数据库，需要的数据可根据ShipmentFileContent对应
     * <p>
     * 与之相关的数据库Table为：fajian
     */
    private void insertDataToDatabase(final ZCFajianFileContent
                                              zcFajianFileContent) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                DbManager db = BQDataBaseHelper.getDb();
                try {
                    db.save(zcFajianFileContent);
                } catch (DbException e) {
                    LogUtil.trace(e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.cancel_button:
                LogUtil.trace();
                zCfajianUploadFile.uploadFile();
                break;

            case R.id.ok_button:
//                  zcFajianFileContent.scanDateChanged(TextStringUtil
//                        .getFormatTimeString());
//                LogUtil.d(TAG, zcFajianFileContent.toString());
//
//                mShipmentUploadFile.writeContentToFile(zcFajianFileContent
//                        .getmCurrentValue() +
//                        "\r\n", true);
//                mShipmentUploadFile.uploadFile();

                break;
        }

    }
    /**
     * 创建静态内部类，继承AsyncTaskLoader,并重写三个方法
     *
     */
    private  class MyAsyncTaskLoader extends AsyncTask<Void,String,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            vehicleInfo =(List<String>) BQDataBaseHelper.queryData(VehicleInfo.class,"number");
            salesService =(List<String>) BQDataBaseHelper.queryData(SalesService.class,"serviceName");
            //shipmentTyoe =(List<String>) BQDataBaseHelper.queryData(ShipmentType.class,"类型名称");
            resolveShipmentTypeData();
            liucang = (List<String>)BQDataBaseHelper.queryData(LiucangBean.class,"名称");
            return null;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            showAdapter();
            // 运行在主线程，更新UI
        }

        protected  void  showAdapter(){
            AutoCompleteTextView autoCompleteTextView1 = (AutoCompleteTextView)car_code.getRightText();
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(ZhuangcheActivity.this, android.R.layout.simple_list_item_1,vehicleInfo);
            autoCompleteTextView1.setAdapter(adapter1);
            AutoCompleteTextView autoCompleteTextView2 = (AutoCompleteTextView)next_station.getRightText();
            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(ZhuangcheActivity.this, android.R.layout.simple_list_item_1,salesService);
            autoCompleteTextView2.setAdapter(adapter2);
            AutoCompleteTextView autoCompleteTextView3 = (AutoCompleteTextView)express_type.getRightText();
         //   ArrayAdapter<String> adapter3 = new ArrayAdapter<>(ZhuangcheActivity.this, android.R.layout.simple_list_item_1,mShipmentData);
            autoCompleteTextView3.setAdapter(mShipmentType);
        }
    }

    private void resolveShipmentTypeData() {
        mShipmentTypeList = queryShipmentTypeData();

        mShipmentDataTmp = new HashMap<>();
        for (int index = 0; index < mShipmentTypeList.size(); index++) {
            mShipmentData.add(mShipmentTypeList.get(index).get类型名称());
            mShipmentDataTmp.put(mShipmentTypeList.get(index).get类型编号(),
                    mShipmentTypeList.get(index).get类型名称());
        }

        LogUtil.trace("size:" + mShipmentData.size());
        mShipmentType = new ArrayAdapter<String>
                (ZhuangcheActivity.this, android.R.layout.simple_list_item_1,
                        mShipmentData);
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
