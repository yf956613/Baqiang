package com.jiebao.baqiang.data.db;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.jiebao.baqiang.data.bean.VehicleInfo;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class VehicleInfoDBHelper {
    /**
     * 获取车辆识别号列表
     *
     * @return
     */
    public static List<String> getAllVehicleID() {
        List<VehicleInfo> mData = null;
        DbManager dbManager = BQDataBaseHelper.getDb();

        try {
            mData = dbManager.findAll(VehicleInfo.class);
        } catch (DbException e) {
            LogUtil.trace();
            e.printStackTrace();
        }

        List<String> mArrayInfo = new ArrayList<>();
        for (int index = 0; index < mData.size(); index++) {
            mArrayInfo.add(mData.get(index).get车辆识别号());
        }

        return mArrayInfo;
    }


    /**
     * 校验车辆码信息
     *
     * @param vehicleID
     * @return true：数据正常； false：数据异常
     */
    public static boolean checkVehicleInfo(String vehicleID) {
        if (!TextUtils.isEmpty(vehicleID)) {
            DbManager dbManager = BQDataBaseHelper.getDb();
            try {
                List<VehicleInfo> data = dbManager.selector(VehicleInfo.class).where("车辆识别号",
                        "like", vehicleID).limit(1).findAll();
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
