package com.jiebao.baqiang.data.UpdateData;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import com.google.gson.Gson;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.data.bean.SalesServiceList;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.util.FileUtil;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.DbManager;

import java.io.IOException;
import java.util.List;

/**
 * 请求服务器的营业网点数据，并保持数据库
 */

public class UpdateSalesServiceData {
    private static final String TAG = UpdateSalesServiceData.class.getSimpleName();

    private static String mSalesServiceUrl = "";
    private volatile static UpdateSalesServiceData mInstance;

    private UpdateSalesServiceData() {
    }

    public static UpdateSalesServiceData getInstance() {
        if (mInstance == null) {
            synchronized (UpdateSalesServiceData.class) {
                if (mInstance == null) {
                    mInstance = new UpdateSalesServiceData();
                }
            }
        }

        return mInstance;
    }

    /**
     * 请求后台数据
     *
     * @return
     */
    public boolean updateSalesService() {
        /*mSalesServiceUrl = SharedUtil.getServletAddresFromSP
                (BaqiangApplication.getContext(), NetworkConstant
                        .NEXT_SALES_SERVICE_SERVLET);
        LogUtil.e(TAG, "Server salesService url: " + mSalesServiceUrl);

        RequestParams params = new RequestParams(mSalesServiceUrl);
        params.addQueryStringParameter("userName", "jiebao");
        params.addQueryStringParameter("password", "jiebao");

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String saleServices) {
                LogUtil.trace();

                Gson gson = new Gson();
                SalesServiceList salesServiceList = gson.fromJson(saleServices,
                        SalesServiceList.class);
                LogUtil.trace("size:" + salesServiceList.getCount());

                storageData(salesServiceList);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.trace(throwable.getMessage());

            }

            @Override
            public void onCancelled(CancelledException e) {
                LogUtil.trace();
            }

            @Override
            public void onFinished() {
                LogUtil.trace();
            }
        });*/

        storageData(testResolveData(testServiceBackContent()));

        return false;
    }

    /**
     * 保存数据到数据库
     *
     * @return
     */
    private boolean storageData(final SalesServiceList salesServiceList) {
        LogUtil.trace();

        new Thread(new Runnable() {

            @Override
            public void run() {
                List<SalesService> saleInfo;
                saleInfo = salesServiceList.getSalesServiceList();

                DbManager db = BQDataBaseHelper.getDb();
                LogUtil.trace("saleInfo.size():" + saleInfo.size());

                for (int index = 0; index < saleInfo.size(); index++) {
                    try {
                        SalesService salesService = new SalesService(saleInfo.get(index).get网点编号
                                (), saleInfo.get(index).get网点名称(), saleInfo.get(index).get所属网点(),
                                saleInfo.get(index).get所属财务中心(), saleInfo.get(index).get启用标识(),
                                saleInfo.get(index).get允许到付(), saleInfo.get(index).get城市(),
                                saleInfo.get(index).get省份(), saleInfo.get(index).get更新状态(),
                                saleInfo.get(index).get更新时间(), saleInfo.get(index).get类型(),
                                saleInfo.get(index).get所属提交货中心(), saleInfo.get(index).get县());
                        LogUtil.e(TAG, salesService.toString());

                        db.save(salesService);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }).start();

        return true;
    }


    private String testServiceBackContent() {
        String value = "";
        try {
            LogUtil.trace("path:" + Environment.getExternalStorageDirectory() + "/tmp/salesInfo"
                    + ".txt");
            value = FileUtil.readSDFile(Environment.getExternalStorageDirectory() +
                    "/tmp/salesInfo.txt");

            LogUtil.trace("vaue:" + value);
        } catch (IOException e) {
            LogUtil.trace("file is not exist...");
            e.printStackTrace();
        }

        return value;
    }

    private SalesServiceList testResolveData(String saleServices) {
        Gson gson = new Gson();
        SalesServiceList salesServiceList = gson.fromJson(saleServices, SalesServiceList.class);
        LogUtil.trace("size:" + salesServiceList.getCount());
        return salesServiceList;
    }

}
