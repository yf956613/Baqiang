package com.jiebao.baqiang.data.db;

import android.text.TextUtils;

import com.jiebao.baqiang.data.bean.LiucangBean;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class StayHouseReasonDBHelper {

    /**
     * 从数据库中取出留仓原因数据
     *
     * @return
     */
    public static List<String> getReasonFromDB() {
        List<LiucangBean> mData = null;
        DbManager dbManager = BQDataBaseHelper.getDb();
        try {
            mData = dbManager.findAll(LiucangBean.class);
            if (mData != null) {
                LogUtil.trace("List<LiucangBean>::" + mData.size());
            }
        } catch (DbException e) {
            LogUtil.trace();
            e.printStackTrace();
        }

        if (mData != null) {
            Collections.sort(mData, new Comparator<LiucangBean>() {

                @Override
                public int compare(LiucangBean o1, LiucangBean o2) {
                    Integer idFirst = Integer.parseInt(o1.get编号());
                    Integer idSecond = Integer.parseInt(o2.get编号());

                    return idFirst.compareTo(idSecond);
                }
            });

            List<String> reasonData = new ArrayList<>();
            for (int index = 0; index < mData.size(); index++) {
                reasonData.add(mData.get(index).get编号() + "  " + mData.get
                        (index).get名称());
            }

            return reasonData;
        } else {
            return null;
        }

    }

    /**
     * 根据留仓名，获取其对应的留仓编号
     *
     * @param reason
     * @return
     */
    public static String getReasonIdFromName(String reason) {
        DbManager dbManager = BQDataBaseHelper.getDb();
        try {
            List<LiucangBean> data = dbManager.selector(LiucangBean.class)
                    .where("名称", "=",
                            reason).limit(1).findAll();

            if (data != null && data.size() != 0) {
                return data.get(0).get编号();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据留仓编号查询名称
     * @param id
     * @return
     */
    public static String getReasonNameFromId(String id) {
        DbManager dbManager = BQDataBaseHelper.getDb();
        try {
            List<LiucangBean> data = dbManager.selector(LiucangBean.class)
                    .where("编号", "=",
                            id).limit(1).findAll();

            if (data != null && data.size() != 0) {
                return data.get(0).get名称();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 判断是否存在指定留仓原因
     *
     * @param reason
     * @return
     */
    public static boolean checkCurrentReason(String reason) {
        if (!TextUtils.isEmpty(reason)) {
            // 存在保存发件数据的表，从该表中查询对应的单号
            DbManager dbManager = BQDataBaseHelper.getDb();
            try {
                List<LiucangBean> bean = dbManager.selector(LiucangBean
                        .class).where("名称", "=",
                        reason).limit(1).findAll();
                if (bean != null && bean.size() != 0) {
                    LogUtil.trace("bean:" + bean.size());
                    return true;
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

}
