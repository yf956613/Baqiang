package com.jiebao.baqiang.data.updateData;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.data.bean.SalesServiceList;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.util.FileUtil;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.List;

/**
 * 请求服务器的营业网点数据，并保持数据库
 */

public class UpdateSalesServiceData extends UpdateInterface {
    private static final String TAG = UpdateSalesServiceData.class
            .getSimpleName();
    private static final String DB_NAME = "salesservice";

    private static String mSalesServiceUrl = "";
    private volatile static UpdateSalesServiceData mInstance;

    private DataDownloadFinish mDataDownloadFinish;

    public interface DataDownloadFinish {
        void downloadSalesServiceFinish();
    }

    private UpdateSalesServiceData() {
    }

    public static UpdateSalesServiceData getInstance() {
        if (mInstance == null) {
            synchronized (UpdateSalesServiceData.class) {
                if (mInstance == null) {
                    mInstance = new UpdateSalesServiceData();
                }
            }
        }

        return mInstance;
    }

    public void setDataDownloadFinish(DataDownloadFinish dataDownloadFinish) {
        this.mDataDownloadFinish = dataDownloadFinish;
    }

    public boolean updateSalesService() {
        mSalesServiceUrl = SharedUtil.getServletAddresFromSP
                (BaqiangApplication.getContext(), NetworkConstant
                        .NEXT_SALES_SERVICE_SERVLET);

        RequestParams params = new RequestParams(mSalesServiceUrl);
        params.addQueryStringParameter("saleId", salesId);
        params.addQueryStringParameter("userName", userName);
        params.addQueryStringParameter("password", psw);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String saleServices) {
                // TODO 创建Gson对象时，指定时间格式
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                SalesServiceList salesServiceList = gson.fromJson(saleServices,
                        SalesServiceList.class);
                storageData(salesServiceList);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.trace(throwable.getMessage() + " " + throwable
                        .getLocalizedMessage());
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
    private boolean storageData(final SalesServiceList salesServiceList) {
        LogUtil.trace();
        DbManager db = BQDataBaseHelper.getDb();

        // 清空已有表数据
        if (tableIsExist(DB_NAME)) {
            // 删除已有Table文件
            try {
                db.delete(SalesService.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        List<SalesService> saleInfo;
        saleInfo = salesServiceList.getSalesServiceList();
        for (int index = 0; index < saleInfo.size(); index++) {
            try {
                SalesService salesService = new SalesService(saleInfo
                        .get(index).get网点编号(), saleInfo.get(index)
                        .get网点名称(), saleInfo.get(index).get所属网点(),
                        saleInfo.get(index).get所属财务中心(), saleInfo.get
                        (index).get启用标识(), saleInfo.get(index)
                        .get允许到付(), saleInfo.get(index).get城市(),
                        saleInfo.get(index).get省份(), saleInfo.get
                        (index).get更新状态(), saleInfo.get(index)
                        .get更新时间(), saleInfo.get(index).get类型(),
                        saleInfo.get(index).get所属提交货中心(), saleInfo
                        .get(index).get县());
                db.save(salesService);
            } catch (Exception exception) {
                LogUtil.trace(exception.getMessage());
                exception.printStackTrace();
            }
        }
        LogUtil.trace("sava data is over...");
        mDataDownloadFinish.downloadSalesServiceFinish();

        return true;
    }


    private String testServiceBackContent() {
        String value = "";
        try {
            LogUtil.trace("path:" + Environment.getExternalStorageDirectory()
                    + "/tmp/tmp"
                    + ".txt");
            value = FileUtil.readSDFile(Environment
                    .getExternalStorageDirectory() +
                    "/tmp/tmp.txt");
        } catch (IOException e) {
            LogUtil.trace("file is not exist...");
            e.printStackTrace();
        }

        return value;
    }

    private SalesServiceList testResolveData(String saleServices) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        SalesServiceList salesServiceList = gson.fromJson(saleServices,
                SalesServiceList.class);
        return salesServiceList;
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
