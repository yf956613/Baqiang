package com.jiebao.baqiang.data.updateData;

import com.google.gson.Gson;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.PaymentType;
import com.jiebao.baqiang.data.bean.PaymentTypeList;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * Created by yaya on 2018/1/26.
 */

public class UpdatePaymentType extends UpdateInterface {
    private static final String TAG = UpdatePaymentType.class
            .getSimpleName();

    private static String mUpdatePaymentTypeUrl = "";
    private volatile static UpdatePaymentType mInstance;

    private UpdatePaymentType() {
    }

    public static UpdatePaymentType getInstance() {
        if (mInstance == null) {
            synchronized (UpdatePaymentType.class) {
                if (mInstance == null) {
                    mInstance = new UpdatePaymentType();
                }
            }
        }

        return mInstance;
    }

    public boolean updateVehicleInfo() {
        mUpdatePaymentTypeUrl = SharedUtil.getServletAddresFromSP
                (BaqiangApplication.getContext(), NetworkConstant
                        .PAYMENT_TYPE_SERVLET);

        RequestParams params = new RequestParams(mUpdatePaymentTypeUrl);

        params.addQueryStringParameter("saleId",salesId);
        params.addQueryStringParameter("userName", userName);
        params.addQueryStringParameter("password", psw);
        params.setConnectTimeout(45 * 1000);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String saleServices) {
                LogUtil.trace();

                Gson gson = new Gson();
                PaymentTypeList list = gson.fromJson(saleServices,
                        PaymentTypeList.class);
                LogUtil.trace("size:" + list.getPayWaysCnt());
                for (int index = 0; index < list.getPayWaysCnt(); index++) {
                    LogUtil.d(TAG, "-->" + list.getPayWayInfo().get(index)
                            .get付款方式名称());
                }

                storageData(list);
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
    private boolean storageData(final PaymentTypeList paymentTypeList) {
        LogUtil.trace();

        new Thread(new Runnable() {

            @Override
            public void run() {
                List<PaymentType> paymentTypes;
                paymentTypes = paymentTypeList.getPayWayInfo();

                DbManager db = BQDataBaseHelper.getDb();
                LogUtil.trace("saleInfo.size():" + paymentTypes.size());

                for (int index = 0; index < paymentTypes.size(); index++) {
                    // LogUtil.d(TAG, "index=" + index);
                    try {
                        db.save(new PaymentType(paymentTypes.get(index)
                                .get付款方式编号(), paymentTypes.get(index).get付款方式名称()));
                    } catch (Exception exception) {
                        LogUtil.trace(exception.getMessage());
                        exception.printStackTrace();
                    }
                }
            }
        }).start();

        return true;
    }
}
