package com.jiebao.baqiang.data.UpdateData;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
 * 更新快件类型的数据
 */
/*
快件类型
 */

public class UpdateShipmentType extends UpdateInterface{
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
        params.addQueryStringParameter("saleId",salesId);
        params.addQueryStringParameter("userName", userName);
        params.addQueryStringParameter("password", psw);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String saleServices) {
                LogUtil.trace();

                Gson gson = new Gson();
                ShipmentTypeList list = gson.fromJson(saleServices,
                        ShipmentTypeList.class);
                /*LogUtil.trace("size:" + list.getGoodTypesCnt());
                for (int index = 0; index < list.getGoodTypesCnt(); index++) {
                    LogUtil.d(TAG, "-->" + list.getGoodTypeInfo().get(index)
                            .get类型名称());
                }*/

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

    private boolean storageData(final ShipmentTypeList shipmentTypeList) {
        LogUtil.trace();

        if (tableIsExist("shipmenttype")) {
            LogUtil.trace("not to update shipment type....");
            // 如果已建立了表，则不会保存更新数据
            return false;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                List<ShipmentType> shipmentTypes;
                shipmentTypes = shipmentTypeList.getGoodTypeInfo();

                DbManager db = BQDataBaseHelper.getDb();
                for (int index = 0; index < shipmentTypes.size(); index++) {
                    try {
                        db.save(new ShipmentType(shipmentTypes.get(index)
                                .get类型编号(), shipmentTypes.get(index).get类型名称
                                ()));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

                LogUtil.trace("Update Shipment Type is over....");
            }
        }).start();

        return true;
    }

    /**
     * 查询数据库文件中是否有快件类型表
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
