package com.jiebao.baqiang.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.jiebao.baqiang.data.UpdateData.UpdateLiuCangType;
import com.jiebao.baqiang.data.UpdateData.UpdateSalesServiceData;
import com.jiebao.baqiang.data.UpdateData.UpdateShipmentType;
import com.jiebao.baqiang.data.UpdateData.UpdateVehicleInfo;

/**
 * Created by open on 2018/1/29.
 */

public class DataSyncService extends Service{

    private DataSyncNotifity mdataSyncNotifity;


    public interface DataSyncNotifity {
        //void onSyncStart(int totalTables);

       // void onDataChange(String tableName, String syncType);

        void onSyncFinished(Exception e);

        //void onSyncinterupted();

        //void onTimeTicker(boolean isRunning);
    }
    @Override
    public void onCreate(){
    }

    public class MyBinder extends Binder{
        public DataSyncService getService(){return DataSyncService.this;}
     }

    private MyBinder myBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public void onStart(){

        UpdateVehicleInfo.getInstance().updateVehicleInfo();
        UpdateSalesServiceData.getInstance().updateSalesService();
        UpdateShipmentType.getInstance().updateShipmentType();
        UpdateLiuCangType.getInstance().updateLiuCangType();
        mdataSyncNotifity.onSyncFinished(null);
    }

    public void setDataSyncNotifity(DataSyncNotifity dataSyncNotifity) {
        mdataSyncNotifity =dataSyncNotifity;
    }
}
