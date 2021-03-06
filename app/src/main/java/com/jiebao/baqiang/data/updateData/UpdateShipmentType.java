package com.jiebao.baqiang.data.updateData;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.ShipmentType;
import com.jiebao.baqiang.data.bean.ShipmentTypeList;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.global.Constant;
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
 * 更新快件类型的数据
 */

public class UpdateShipmentType extends UpdateInterface {
    private static final String TAG = UpdateShipmentType.class.getSimpleName();
    private static final String DB_NAME = "shipmenttype";

    private static String mUpdateShipmentTpyeUrl = "";

    private UpdateShipmentType() {
        infoId = Constant.SHIPMENTTYPEINFO_ID;
    }

    public static UpdateInterface getInstance() {

        if (mInstance == null || ((mInstance != null) && (!(mInstance instanceof UpdateShipmentType)))) {
            synchronized (UpdateShipmentType.class) {
                if (mInstance == null || ((mInstance != null) && (!(mInstance instanceof UpdateShipmentType)))) {
                    mInstance = new UpdateShipmentType();
                }
            }
        }
        return mInstance;
    }

    public void updateData() {
        updateShipmentType();
    }

    public boolean updateShipmentType() {
        mUpdateShipmentTpyeUrl = SharedUtil.getServletAddresFromSP(BaqiangApplication.getContext
                (), NetworkConstant.GOOD_TYPE_SERVLET);

        RequestParams params = new RequestParams(mUpdateShipmentTpyeUrl);
        params.addQueryStringParameter("saleId", UpdateInterface.getSalesId());
        params.addQueryStringParameter("userName", UpdateInterface.getUserName());
        params.addQueryStringParameter("password", UpdateInterface.getPsw());
        params.setConnectTimeout(45 * 1000);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String saleServices) {

                Gson gson = new Gson();
                final ShipmentTypeList list = gson.fromJson(saleServices, ShipmentTypeList.class);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        storageData(list);
                    }
                }).start();

                mDataDownloadStatus.downloadFinish(infoId);

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                // FIXME Login跳转到MainActivity，数据同步失败，提示失败原因，并选择是否再次更新数据
                mDataDownloadStatus.downLoadError(infoId,throwable.getMessage());
                mDataDownloadStatus.updateError(infoId,"download shipment data error !");
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

        mDataDownloadStatus.startDownload(infoId);
        return false;
    }

    private boolean storageData(final ShipmentTypeList shipmentTypeList) {
        LogUtil.trace("+++ save ShipmentType data start +++");

        List<ShipmentType> shipmentTypes;
        shipmentTypes = shipmentTypeList.getGoodTypeInfo();
        if (shipmentTypes == null || shipmentTypes.size() == 0) {
            LogUtil.trace("--- save ShipmentType data failed ---");
            Log.e("ljz", "save ShipmentType data failed");
            mDataDownloadStatus.updateError(infoId,"save ShipmentType data failed");
            return false;
        } else {
            DbManager db = BQDataBaseHelper.getDb();
            if (tableIsExist(DB_NAME)) {
                // FIXME 删除已有Table文件
                try {
                    db.delete(ShipmentType.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

            for (int index = 0; index < shipmentTypes.size(); index++) {
                try {
                    db.save(new ShipmentType(shipmentTypes.get(index).get类型编号(), shipmentTypes
                            .get(index).get类型名称()));
                } catch (Exception exception) {
                    // 反馈出错信息
                    mDataDownloadStatus.updateError(infoId,exception.getLocalizedMessage());
                    exception.printStackTrace();
                }
            }
            // 数据更新正常，状态反馈
            mDataDownloadStatus.updateDataFinish(infoId);
            LogUtil.trace("--- save ShipmentType data over ---");
        }
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
            String sql = "select count(*) from sqlite_master where type " + "='table' and name "
                    + "='" + tableName.trim() + "' ";
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
