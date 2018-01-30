package com.jiebao.baqiang.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.jiebao.baqiang.data.UpdateData.UpdateSalesServiceData;
import com.jiebao.baqiang.util.LogUtil;

/**
 * Created by open on 2018/1/29.
 */

public class DataSyncService extends Service {

    private DataSyncNotifity mDataSyncNotifity;
    private MyBinder myBinder = new MyBinder();

    public interface DataSyncNotifity {
        //void onSyncStart(int totalTables);

        // void onDataChange(String tableName, String syncType);

        void onSyncFinished(Exception e);

        //void onSyncinterupted();

        //void onTimeTicker(boolean isRunning);
    }

    @Override
    public void onCreate() {
        LogUtil.trace();
    }

    public class MyBinder extends Binder {
        public DataSyncService getService() {
            return DataSyncService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.trace();

        //UpdateVehicleInfo.getInstance().updateVehicleInfo();
        //UpdateSalesServiceData.getInstance().updateSalesService();
        //UpdateShipmentType.getInstance().updateShipmentType();
        //UpdateLiuCangType.getInstance().updateLiuCangType();
        UpdateSalesServiceData.getInstance().updateSalesService();

        // TODO onStartCommand --> onServiceConnected 空指针异常
        // mDataSyncNotifity.onSyncFinished(null);

        return super.onStartCommand(intent, flags, startId);
    }

    public void setDataSyncNotifity(DataSyncNotifity dataSyncNotifity) {
        mDataSyncNotifity = dataSyncNotifity;
    }
}
