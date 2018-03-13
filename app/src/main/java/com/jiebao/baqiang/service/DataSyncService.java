package com.jiebao.baqiang.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import com.jiebao.baqiang.data.updateData.UpdateLiuCangType;
import com.jiebao.baqiang.data.updateData.UpdateSalesServiceData;
import com.jiebao.baqiang.data.updateData.UpdateShipmentType;
import com.jiebao.baqiang.data.updateData.UpdateVehicleInfo;
import com.jiebao.baqiang.util.LogUtil;

public class DataSyncService extends Service implements UpdateSalesServiceData
        .DataDownloadFinish, UpdateShipmentType.DataDownloadFinish, UpdateLiuCangType
        .DataDownloadFinish, UpdateVehicleInfo.DataDownloadFinish {

    private DataSyncNotifity mDataSyncNotifity;
    private MyBinder myBinder = new MyBinder();


    public interface DataSyncNotifity {
        void onSyncFinished(Exception e);
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
        mUpdateID = 0;

        DataSyncTask dataSyncTask = new DataSyncTask();
        // 其中的参数用于选择性下载
        dataSyncTask.execute("start_param");

        return super.onStartCommand(intent, flags, startId);
    }

    public void setDataSyncNotifity(DataSyncNotifity dataSyncNotifity) {
        mDataSyncNotifity = dataSyncNotifity;
    }

    class DataSyncTask extends AsyncTask<String, Object, Long> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // TODO MainThread
        }

        @Override
        protected Long doInBackground(String... strings) {
            // TODO WorkerThread
            LogUtil.trace();

            // 更新网点数据
            UpdateSalesServiceData.getInstance().setDataDownloadFinish(DataSyncService.this);
            UpdateSalesServiceData.getInstance().updateSalesService();
            // 更新快件类型正常
            UpdateShipmentType.getInstance().setDataDownloadFinish(DataSyncService.this);
            UpdateShipmentType.getInstance().updateShipmentType();
            // 更新留仓原因正常
            UpdateLiuCangType.getInstance().setDataDownloadFinish(DataSyncService.this);
            UpdateLiuCangType.getInstance().updateLiuCangType();
            // 更新车辆信息正常
            UpdateVehicleInfo.getInstance().setDataDownloadFinish(DataSyncService.this);
            UpdateVehicleInfo.getInstance().updateVehicleInfo();

            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            // TODO MainThread
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            // TODO MainThread
        }

    }

    private static int mUpdateID = 0;

    @Override
    public void downloadSalesServiceFinish() {
        ++mUpdateID;

        LogUtil.trace("mUpdateID:" + mUpdateID);
        if (mUpdateID == 4) {
            // onStartCommand --> onServiceConnected 空指针异常
            mDataSyncNotifity.onSyncFinished(null);
        }
    }

    @Override
    public void downloadShipmentTypeFinish() {
        ++mUpdateID;

        LogUtil.trace("mUpdateID:" + mUpdateID);
        if (mUpdateID == 4) {
            // onStartCommand --> onServiceConnected 空指针异常
            mDataSyncNotifity.onSyncFinished(null);
        }
    }

    @Override
    public void downloadLiuCangTypeFinish() {
        ++mUpdateID;

        LogUtil.trace("mUpdateID:" + mUpdateID);
        if (mUpdateID == 4) {
            // onStartCommand --> onServiceConnected 空指针异常
            mDataSyncNotifity.onSyncFinished(null);
        }
    }

    @Override
    public void downloadVehicleInfoFinish() {
        ++mUpdateID;

        LogUtil.trace("mUpdateID:" + mUpdateID);
        if (mUpdateID == 4) {
            // onStartCommand --> onServiceConnected 空指针异常
            mDataSyncNotifity.onSyncFinished(null);
        }
    }
}
