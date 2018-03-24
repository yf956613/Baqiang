package com.jiebao.baqiang.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2018/3/24 0024.
 */

public class NetworkUtils {

    /**
     * 判断网络是否连接
     *
     * @param cxt
     * @return
     */
    public static boolean isNetworkConnected(Context cxt) {
        ConnectivityManager connMgr = (ConnectivityManager) cxt.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * 判断网络连接是否是WIFI形式
     *
     * @param mContext
     * @return
     */
    public static boolean isWifiNetwork(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
}
