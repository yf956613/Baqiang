package com.jiebao.baqiang.util;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2018/2/24 0024.
 */

public class VersionUpdateUtil {

    /**
     * 获取当前程序的版本号
     *
     * @return
     * @throws Exception
     */
    public static int getVersionCode(Context cxt) throws Exception {
        PackageManager packageManager = cxt.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(cxt.getPackageName(), 0);

        return packInfo.versionCode;
    }

}
