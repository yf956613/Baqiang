package com.jiebao.baqiang.data.UpdateData;

import com.google.gson.Gson;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.LiucangBean;
import com.jiebao.baqiang.data.bean.LiucangListInfo;
import com.jiebao.baqiang.data.bean.PaymentType;
import com.jiebao.baqiang.data.bean.PaymentTypeList;
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
 * Created by yaya on 2018/1/26.
 */

public class UpdateLiuCangType {
    private static final String TAG = UpdateLiuCangType.class
            .getSimpleName();

    private static String mUpdateLiuCangTypeUrl = "";
    private volatile static UpdateLiuCangType mInstance;

    private UpdateLiuCangType() {
    }

    public static UpdateLiuCangType getInstance() {
        if (mInstance == null) {
            synchronized (UpdateLiuCangType.class) {
                if (mInstance == null) {
                    mInstance = new UpdateLiuCangType();
                }
            }
        }

        return mInstance;
    }

    public boolean updateLiuCangType() {
        mUpdateLiuCangTypeUrl = SharedUtil.getServletAddresFromSP
                (BaqiangApplication.getContext(), NetworkConstant
                        .LiuCang_TYPE);

        RequestParams params = new RequestParams(mUpdateLiuCangTypeUrl);

        params.addQueryStringParameter("userName", "jiebao");
        params.addQueryStringParameter("password", "jiebao");

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String saleServices) {
                LogUtil.trace();

                Gson gson = new Gson();
                LiucangListInfo list = gson.fromJson(saleServices,
                        LiucangListInfo.class);
                LogUtil.trace("size:" + list.getLiuCangCnt());
                for (int index = 0; index < list.getLiuCangCnt(); index++) {
                    LogUtil.d(TAG, "-->" + list.getLiuCangInfo().get(index).getNUME());
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
    private boolean storageData(final LiucangListInfo liucangListInfo) {
        LogUtil.trace();

        new Thread(new Runnable() {

            @Override
            public void run() {
                List<LiucangBean> liucangBean;
                liucangBean = liucangListInfo.getLiuCangInfo();

               // DbManager db = BQDataBaseHelper.getDb();
                LogUtil.trace("saleInfo.size():" + liucangBean.size());

//                for (int index = 0; index < liucangBean.size(); index++) {
//                    // LogUtil.d(TAG, "index=" + index);
//                    try {
//                        db.save(new PaymentType(liucangBean.get(index)
//                                .get付款方式编号(), paymentTypes.get(index).get付款方式名称()));
//                    } catch (Exception exception) {
//                        LogUtil.trace(exception.getMessage());
//                        exception.printStackTrace();
//                    }
//                }
                BQDataBaseHelper.saveToDB(liucangBean);
            }
        }).start();

        return true;
    }
}
