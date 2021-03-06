package com.jiebao.baqiang.data.updateData;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.LiucangBean;
import com.jiebao.baqiang.data.bean.LiucangListInfo;
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
 * 留仓原因
 */

public class UpdateLiuCangType extends UpdateInterface {
    private static final String TAG = UpdateLiuCangType.class.getSimpleName();
    private static final String DB_NAME = "liucang";

    private static String mUpdateLiuCangTypeUrl = "";

    public void setDataDownloadStatus(IDownloadStatus dataDownloadFinish) {
        this.mDataDownloadStatus = dataDownloadFinish;
    }

    private UpdateLiuCangType() {
        infoId = Constant.LIUCANGTYPEINFO_ID;
    }

    public static UpdateInterface getInstance() {
        if (mInstance == null || ((mInstance != null) && (!(mInstance instanceof UpdateLiuCangType)))) {
            synchronized (UpdateSalesServiceData.class) {
                if (mInstance == null || ((mInstance != null) && (!(mInstance instanceof UpdateLiuCangType)))) {
                    mInstance = new UpdateLiuCangType();
                }
            }
        }
        return mInstance;
    }

    public void updateData() {
        updateLiuCangType();
    }

    public boolean updateLiuCangType() {
        mUpdateLiuCangTypeUrl = SharedUtil.getServletAddresFromSP(BaqiangApplication.getContext()
                , NetworkConstant.LiuCang_TYPE);

        RequestParams params = new RequestParams(mUpdateLiuCangTypeUrl);
        params.addQueryStringParameter("saleId", UpdateInterface.getSalesId());
        params.addQueryStringParameter("userName", UpdateInterface.getUserName());
        params.addQueryStringParameter("password", UpdateInterface.getPsw());
        params.setConnectTimeout(45 * 1000);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String liucang) {

                Gson gson = new Gson();
                final LiucangListInfo list = gson.fromJson(liucang, LiucangListInfo.class);

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
                mDataDownloadStatus.updateError(infoId,"download liucang data error !");
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

    /**
     * 保存数据到数据库
     *
     * @return
     */
    private boolean storageData(final LiucangListInfo liucangListInfo) {
        LogUtil.trace("+++ save LiucangReason data start +++");

        List<LiucangBean> liucangBean = liucangListInfo.getLiuCangInfo();
        if (liucangBean == null || liucangBean.size() == 0) {
            LogUtil.trace("--- save LiucangBean data failed ---");
            Log.e("ljz", "save LiucangBean data failed");
            mDataDownloadStatus.updateError(infoId,"save LiucangBean data failed");
            return false;
        } else {
            DbManager db = BQDataBaseHelper.getDb();
            if (tableIsExist(DB_NAME)) {
                // FIXME 删除已有Table文件
                try {
                    db.delete(LiucangBean.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

            for (int index = 0; index < liucangBean.size(); index++) {
                try {
                    LiucangBean reason = new LiucangBean(liucangBean.get(index).get编号(),
                            liucangBean.get(index).get名称(), liucangBean.get(index).get备注());
                    db.save(reason);
                } catch (Exception exception) {
                    // 反馈出错信息
                    mDataDownloadStatus.updateError(infoId,exception.getLocalizedMessage());
                    exception.printStackTrace();
                }
            }
            mDataDownloadStatus.updateDataFinish(infoId);
            LogUtil.trace("--- save LiucangBean data over ---");
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
