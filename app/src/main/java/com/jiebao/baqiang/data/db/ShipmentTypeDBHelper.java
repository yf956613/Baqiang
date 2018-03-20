package com.jiebao.baqiang.data.db;

import android.text.TextUtils;

import com.jiebao.baqiang.data.bean.SalesService;
import com.jiebao.baqiang.data.bean.ShipmentType;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class ShipmentTypeDBHelper {

    public static List<String> getShipmentTypeInfo() {
        List<ShipmentType> mData = null;
        DbManager dbManager = BQDataBaseHelper.getDb();

        try {
            mData = dbManager.findAll(ShipmentType.class);
        } catch (DbException e) {
            LogUtil.trace();
            e.printStackTrace();
        }

        List<String> mShipmentData = new ArrayList<>();
        for (int index = 0; index < mData.size(); index++) {
            // 采用固定格式便于解析快件类型
            mShipmentData.add(mData.get(index).get类型编号() + "  " + mData.get(index).get类型名称());
        }

        Collections.sort(mShipmentData);

        return mShipmentData;
    }

    public static String getShipmentTypeIDFromName(String shipmentName) {
        DbManager dbManager = BQDataBaseHelper.getDb();
        try {
            List<ShipmentType> data = dbManager.selector(ShipmentType.class).where("类型名称",
                    "like", shipmentName).limit(1).findAll();

            if (data != null && data.size() != 0) {
                return data.get(0).get类型编号();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 校验网点名称
     *
     * @param shipmentName
     * @return true：数据正常； false：数据异常
     */
    public static boolean checkShipmentType(String shipmentName) {
        LogUtil.trace("shipmentName:" + shipmentName);
        if (!TextUtils.isEmpty(shipmentName)) {
            DbManager dbManager = BQDataBaseHelper.getDb();
            try {
                List<ShipmentType> data = dbManager.selector(ShipmentType.class).where("类型名称",
                        "like", shipmentName).limit(1).findAll();
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
