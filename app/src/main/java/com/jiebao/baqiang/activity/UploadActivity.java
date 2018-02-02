package com.jiebao.baqiang.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.view.JBItemEdit;
import com.jiebao.baqiang.data.bean.VehicleInfo;
import com.jiebao.baqiang.data.bean.SalesService;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by open on 2018/1/22.
 */

public class UploadActivity extends BaseActivity implements View.OnClickListener
{

    private long lastClickTimer = 0;
    private  static  final String TAG="UploadActivity";
    //选择请求码1
    public static final int CHIOCE_REQUESETCODE_1 = 1001;
    public static final int CHIOCE_RESULTCODE_1 = 1002;
    private JBItemEdit jb3;
    private AutoCompleteTextView autoCompleteTextView3;
    private SimpleCursorAdapter adapter;
    List<String > car_num= new ArrayList<>();
    List<String>  before_station =new ArrayList<>();
    Button ok_button,cancel_button;
    @Override
    public void initView() {
        setContent(R.layout.upload_layout);
        initHeaderView();
    }

    public void initHeaderView() {
        setHeaderCenterViewText(getString(R.string.main_check));
    }

    @Override
    public void initData() {
        jb3 =(JBItemEdit)findViewById(R.id.tracking_numb);
        autoCompleteTextView3 =(AutoCompleteTextView)jb3.getRightText();
        ok_button = (Button)findViewById(R.id.ok_button);
        cancel_button = (Button)findViewById(R.id.cancel_button);
        ok_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    protected void onResume(){
        super.onResume();
        new MyAsyncTaskLoader().execute();
    }
    protected void fillCode(String barcode){
        autoCompleteTextView3.setText(barcode);
    }
    @Override
    public void onClick(View v) {
        Log.v(TAG,"onClick1111");
        switch (v.getId()){
            case R.id.ok_button:




        }
    }


    /**
     * 启动新的activity，带数据传递，并返回结果
     *
     * @param clasz
     * @param requestCode
     */
    public void showActivityForResult(Class<?> clasz, Bundle data, int requestCode) {
        if (System.currentTimeMillis() - lastClickTimer < 500) {
            lastClickTimer = System.currentTimeMillis();
            return;
        }
        lastClickTimer = System.currentTimeMillis();
        Intent intent = new Intent(getApplicationContext(), clasz);
        intent.putExtras(data);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 创建静态内部类，继承AsyncTaskLoader,并重写三个方法
     *
     */
    private  class MyAsyncTaskLoader extends AsyncTask<Void,String,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            car_num =(List<String>) BQDataBaseHelper.queryData(VehicleInfo.class,"number");;
            before_station =(List<String>) BQDataBaseHelper.queryData(SalesService.class,"serviceName");;
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

    }

    protected  void  showAdapter(){
        JBItemEdit jb1 = (JBItemEdit)findViewById(R.id.car_code);
        AutoCompleteTextView autoCompleteTextView1 = (AutoCompleteTextView)jb1.getRightText();
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,car_num);
        autoCompleteTextView1.setAdapter(adapter1);
        JBItemEdit jb2 = (JBItemEdit)findViewById(R.id.before_station);
        AutoCompleteTextView autoCompleteTextView2 = (AutoCompleteTextView)jb1.getRightText();
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,before_station);
        autoCompleteTextView2.setAdapter(adapter2);
    }
}
