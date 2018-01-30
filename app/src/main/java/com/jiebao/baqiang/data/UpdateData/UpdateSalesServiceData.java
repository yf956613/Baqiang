package com.jiebao.baqiang.data.UpdateData;

import com.google.gson.Gson;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.data.bean.SalesServiceList;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * 请求服务器的营业网点数据，并保持数据库
 */

public class UpdateSalesServiceData {
    private static final String TAG = UpdateSalesServiceData.class
            .getSimpleName();

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
        mSalesServiceUrl = SharedUtil.getServletAddresFromSP
                (BaqiangApplication.getContext(), NetworkConstant
                        .NEXT_SALES_SERVICE_SERVLET);
        LogUtil.d(TAG, "Server salesService url: " + mSalesServiceUrl);

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
        });

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
                        db.save(new SalesService(saleInfo.get(index).get网点编号
                                (), saleInfo.get(index).get网点名称()));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }).start();

        return true;
    }


}
