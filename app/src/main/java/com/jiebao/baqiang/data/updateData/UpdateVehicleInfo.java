package com.jiebao.baqiang.data.updateData;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.VehicleInfo;
import com.jiebao.baqiang.data.bean.VehicleInfoList;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.global.IDownloadStatus;
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

public class UpdateVehicleInfo extends UpdateInterface {
    private static final String TAG = UpdateVehicleInfo.class.getSimpleName();
    private static final String DB_NAME = "vehicleinfo";

    private static String mUpdateVehicleInfoUrl = "";
    private volatile static UpdateVehicleInfo mInstance;

    private IDownloadStatus mDataDownloadStatus;

    public void setDataDownloadStatus(IDownloadStatus dataDownloadStatus) {
        this.mDataDownloadStatus = dataDownloadStatus;
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
        mUpdateVehicleInfoUrl = SharedUtil.getServletAddresFromSP(BaqiangApplication.getContext()
                , NetworkConstant.VEHICLE_INFO_SERVLET);

        // 记住：要在请求数据是都要带上下述3个字段参数
        RequestParams params = new RequestParams(mUpdateVehicleInfoUrl);
        params.addQueryStringParameter("saleId", salesId);
        params.addQueryStringParameter("userName", userName);
        params.addQueryStringParameter("password", psw);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String vehicleInfo) {
                Gson gson = new Gson();
                final VehicleInfoList list = gson.fromJson(vehicleInfo, VehicleInfoList.class);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        storageData(list);
                    }
                }).start();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                // FIXME Login跳转到MainActivity，数据同步失败，提示失败原因，并选择是否再次更新数据
                mDataDownloadStatus.downLoadError(throwable.getMessage());
            }

            @Override
            public void onCancelled(CancelledException e) {
                LogUtil.trace();
            }

            @Override
            public void onFinished() {
                if (Constant.DEBUG) {
                    // FIXME 是否都执行onFinished()？在哪些情况下执行onError()
                    mDataDownloadStatus.downloadFinish();
                }
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
        LogUtil.trace("+++ save SalesService data start +++");

        List<VehicleInfo> vehicleInfos = vehicleInfoList.getVehicleInfo();
        if (vehicleInfos == null || vehicleInfos.size() == 0) {
            LogUtil.trace("--- save VehicleInfo data over ---");
            return false;
        } else {
            DbManager db = BQDataBaseHelper.getDb();
            if (tableIsExist(DB_NAME)) {
                // FIXME 删除已有Table文件
                try {
                    db.delete(VehicleInfo.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

            for (int index = 0; index < vehicleInfos.size(); index++) {
                try {
                    db.save(new VehicleInfo(vehicleInfos.get(index).get车牌号(), vehicleInfos.get
                            (index).get车辆识别号()));
                } catch (Exception exception) {
                    // 反馈出错信息
                    mDataDownloadStatus.downLoadError(exception.getLocalizedMessage());
                    exception.printStackTrace();
                }
            }

            // 数据更新正常，状态反馈
            mDataDownloadStatus.downloadFinish();
            LogUtil.trace("--- save VehicleInfo data over ---");
        }

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
