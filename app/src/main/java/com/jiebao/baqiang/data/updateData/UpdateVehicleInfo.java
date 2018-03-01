package com.jiebao.baqiang.data.updateData;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.VehicleInfo;
import com.jiebao.baqiang.data.bean.VehicleInfoList;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * 请求车辆信息表，来自数据视图：e3new.gprs_view_tab_车辆信息表
 * <p>
 * 数据包括："车牌号":"鲁BS8918","车辆识别号":"G0000150"
 * <p>
 * 与之对应的JavaBean是：VehicleInfo和VehicleInfoList
 */

public class UpdateVehicleInfo extends UpdateInterface{
    private static final String TAG = UpdateVehicleInfo.class
            .getSimpleName();
    private static final String DB_NAME = "vehicleinfo";

    private static String mUpdateVehicleInfoUrl = "";
    private volatile static UpdateVehicleInfo mInstance;

    private DataDownloadFinish mDataDownloadFinish;

    public interface DataDownloadFinish {
        void downloadVehicleInfoFinish();
    }

    public void setDataDownloadFinish(DataDownloadFinish dataDownloadFinish) {
        this.mDataDownloadFinish = dataDownloadFinish;
    }

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

        // 记住：要在请求数据是都要带上下述3个字段参数
        RequestParams params = new RequestParams(mUpdateVehicleInfoUrl);

        params.addQueryStringParameter("saleId",salesId);
        params.addQueryStringParameter("userName", userName);
        params.addQueryStringParameter("password", psw);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String vehicleInfo) {
                LogUtil.trace();

                Gson gson = new Gson();
                VehicleInfoList list = gson.fromJson(vehicleInfo,
                        VehicleInfoList.class);
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
        DbManager db = BQDataBaseHelper.getDb();

        if(tableIsExist(DB_NAME)){
            // 如果已建立了表，则不会保存更新数据
            try {
                db.delete(VehicleInfo.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        List<VehicleInfo> vehicleInfos = null;
        vehicleInfos = vehicleInfoList.getVehicleInfo();
        for (int index = 0; index < vehicleInfos.size(); index++) {
            try {
                db.save(new VehicleInfo(vehicleInfos.get(index)
                        .get车牌号(), vehicleInfos.get(index).get车辆识别号()));
            } catch (Exception exception) {
                LogUtil.trace(exception.getMessage());
                exception.printStackTrace();
            }
        }

        LogUtil.trace("Update VehicleInfo Type is over....");
        mDataDownloadFinish.downloadVehicleInfoFinish();

        return true;
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
}
