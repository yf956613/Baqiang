package com.jiebao.baqiang.data.UpdateData;

import com.google.gson.Gson;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.VehicleInfo;
import com.jiebao.baqiang.data.bean.VehicleInfoList;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.global.Constant;
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

public class UpdateVehicleInfo extends UpdateInterface{
    private static final String TAG = UpdateVehicleInfo.class
            .getSimpleName();

    private static String mUpdateVehicleInfoUrl = "";
    private volatile static UpdateVehicleInfo mInstance;

    private UpdateVehicleInfo() {
    }

    public static UpdateVehicleInfo getInstance() {
        if (mInstance == null) {
            synchronized (UpdateVehicleInfo.class) {
                if (mInstance == null) {
                    mInstance = new UpdateVehicleInfo();
                }
            }
        }

        return mInstance;
    }

    public boolean updateVehicleInfo() {
        mUpdateVehicleInfoUrl = SharedUtil.getServletAddresFromSP
                (BaqiangApplication.getContext(), NetworkConstant
                        .VEHICLE_INFO_SERVLET);

        RequestParams params = new RequestParams(mUpdateVehicleInfoUrl);

        params.addQueryStringParameter("saleId",salesId);
        params.addQueryStringParameter("userName", userName);
        params.addQueryStringParameter("password", psw);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String saleServices) {
                LogUtil.trace();

                Gson gson = new Gson();
                VehicleInfoList list = gson.fromJson(saleServices,
                        VehicleInfoList.class);
                LogUtil.trace("size:" + list.getVehicleInfoCnt());
                for (int index = 0; index < list.getVehicleInfoCnt(); index++) {
                    LogUtil.d(TAG, "-->" + list.getVehicleInfo().get(index)
                            .get车牌号());
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
    private boolean storageData(final VehicleInfoList vehicleInfoList) {
        LogUtil.trace();

        new Thread(new Runnable() {

            @Override
            public void run() {
                List<VehicleInfo> vehicleInfos;
                vehicleInfos = vehicleInfoList.getVehicleInfo();

                DbManager db = BQDataBaseHelper.getDb();
                LogUtil.trace("saleInfo.size():" + vehicleInfos.size());

                for (int index = 0; index < vehicleInfos.size(); index++) {
                    // LogUtil.d(TAG, "index=" + index);
                    try {
                        db.save(new VehicleInfo(vehicleInfos.get(index)
                                .get车牌号(), vehicleInfos.get(index).get车辆识别号()));
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
