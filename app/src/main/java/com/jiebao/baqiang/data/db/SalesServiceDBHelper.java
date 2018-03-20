package com.jiebao.baqiang.data.db;

import android.text.TextUtils;

import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class SalesServiceDBHelper {

    /**
     * 获取所有网点信息，包括：网点编号和网点名称
     *
     * @return
     */
    public static List<String> getAllSalesServiceData() {
        LogUtil.trace();

        List<SalesService> mData = null;
        DbManager dbManager = BQDataBaseHelper.getDb();
        try {
            mData = dbManager.findAll(SalesService.class);
        } catch (DbException e) {
            LogUtil.trace();
            e.printStackTrace();
        }

        List<String> mArrayInfo = new ArrayList<>();
        for (int index = 0; index < mData.size(); index++) {
            mArrayInfo.add(mData.get(index).get网点编号() + "  " + mData.get(index).get网点名称());
        }

        return mArrayInfo;
    }

    /**
     * 通过网点名称查询对应的网点编号
     *
     * @param serverName
     * @return
     */
    public static String getServerIdFromName(String serverName) {
        DbManager dbManager = BQDataBaseHelper.getDb();
        try {
            List<SalesService> data = dbManager.selector(SalesService.class).where("网点名称",
                    "like", serverName).limit(1).findAll();

            if (data != null && data.size() != 0) {
                return data.get(0).get网点编号();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 校验网点名称
     *
     * @param serverName
     * @return true：数据正常； false：数据异常
     */
    public static boolean checkServerInfo(String serverName) {
        if (!TextUtils.isEmpty(serverName)) {
            DbManager dbManager = BQDataBaseHelper.getDb();
            try {
                List<SalesService> data = dbManager.selector(SalesService.class).where("网点名称",
                        "like", serverName).limit(1).findAll();
                if (data != null && data.size() != 0) {
                    return true;
                } else {
                    // do nothing
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            return false;
        }

        return false;
    }
}
