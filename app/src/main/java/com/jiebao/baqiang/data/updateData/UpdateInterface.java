package com.jiebao.baqiang.data.updateData;

import android.text.TextUtils;

import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.global.IDownloadStatus;
import com.jiebao.baqiang.util.SharedUtil;

/**
 * 所有全局用到网点信息、用户编号和密码
 */

public class UpdateInterface {

    public volatile static UpdateInterface mInstance;

    public IDownloadStatus mDataDownloadStatus;

    public int infoId = 0;

    public int getInfoID() {
        return infoId;
    }

    /*// 网点编号
    public static String salesId = SharedUtil.getString(BaqiangApplication
            .getContext(), Constant.PREFERENCE_KEY_SALE_SERVICE);
    // 用户名：网点编号和用户编号组成
    public static String userName = salesId + SharedUtil.getString
            (BaqiangApplication.getContext(), Constant
            .PREFERENCE_KEY_USERNAME);*/
    /*public static String psw = SharedUtil.getString(BaqiangApplication
            .getContext(), Constant.PREFERENCE_KEY_PSW);*/

    public void setDataDownloadStatus(IDownloadStatus dataDownloadStatus) {
        this.mDataDownloadStatus = dataDownloadStatus;
    }

    public static UpdateInterface getInstance() {
        return mInstance;
    }

    public void updateData() {
    }

    public static String getPsw() {
        String psw = SharedUtil.getString(BaqiangApplication
                .getContext(), Constant.PREFERENCE_KEY_PSW);

        if (!TextUtils.isEmpty(psw)) {
            return psw;
        } else {
            return null;
        }
    }

    public static String getUserName() {
        // 用户名：网点编号和用户编号组成
        String salesId = SharedUtil.getString(BaqiangApplication
                .getContext(), Constant.PREFERENCE_KEY_SALE_SERVICE);
        String userName = salesId + SharedUtil.getString
                (BaqiangApplication.getContext(), Constant
                        .PREFERENCE_KEY_USERNAME);
        if (!TextUtils.isEmpty(userName)) {
            return userName;
        } else {
            return null;
        }
    }

    public static String getSalesId() {
        String salesId = SharedUtil.getString(BaqiangApplication
                .getContext(), Constant.PREFERENCE_KEY_SALE_SERVICE);

        if (!TextUtils.isEmpty(salesId)) {
            return salesId;
        } else {
            return null;
        }
    }

}
