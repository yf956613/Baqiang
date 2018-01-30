package com.jiebao.baqiang.data.UpdateData;

import com.google.gson.Gson;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.ShipmentType;
import com.jiebao.baqiang.data.bean.ShipmentTypeList;
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
/*
快件类型
 */

public class UpdateShipmentType {
    private static final String TAG = UpdateShipmentType.class
            .getSimpleName();

    private static String mUpdateShipmentTpyeUrl = "";
    private volatile static UpdateShipmentType mInstance;

    private UpdateShipmentType() {
    }

    public static UpdateShipmentType getInstance() {
        if (mInstance == null) {
            synchronized (UpdateShipmentType.class) {
                if (mInstance == null) {
                    mInstance = new UpdateShipmentType();
                }
            }
        }

        return mInstance;
    }

    public boolean updateShipmentType() {
        mUpdateShipmentTpyeUrl = SharedUtil.getServletAddresFromSP
                (BaqiangApplication
                        .getContext(), NetworkConstant
                        .GOOD_TYPE_SERVLET);

        RequestParams params = new RequestParams(mUpdateShipmentTpyeUrl);

        params.addQueryStringParameter("userName", "jiebao");
        params.addQueryStringParameter("password", "jiebao");

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String saleServices) {
                LogUtil.trace();

                Gson gson = new Gson();
                ShipmentTypeList list = gson.fromJson(saleServices,
                        ShipmentTypeList.class);
                LogUtil.trace("size:" + list.getGoodTypesCnt());
                for (int index = 0; index < list.getGoodTypesCnt(); index++) {
                    LogUtil.d(TAG, "-->" + list.getGoodTypeInfo().get(index)
                            .get类型名称());
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
    private boolean storageData(final ShipmentTypeList shipmentTypeList) {
        LogUtil.trace();

        new Thread(new Runnable() {

            @Override
            public void run() {
                List<ShipmentType> shipmentTypes;
                shipmentTypes = shipmentTypeList.getGoodTypeInfo();

                DbManager db = BQDataBaseHelper.getDb();
                LogUtil.trace("saleInfo.size():" + shipmentTypes.size());

                for (int index = 0; index < shipmentTypes.size(); index++) {
                    try {
                        db.save(new ShipmentType(shipmentTypes.get(index)
                                .get类型编号(), shipmentTypes.get(index).get类型名称
                                ()));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }).start();

        return true;
    }

}
