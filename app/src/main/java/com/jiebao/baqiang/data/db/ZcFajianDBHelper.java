package com.jiebao.baqiang.data.db;

import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;
import com.jiebao.baqiang.util.TimeUtil;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class ZcFajianDBHelper {

    public static int findAllBean() {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<ZCFajianFileContent> list = db.findAll(ZCFajianFileContent.class);
            if (list != null) {
                return list.size();
            }
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 根据运单编号，设置数据为不可用
     *
     * @param barcode
     */
    public static boolean deleteFindedBean(final String barcode) {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            WhereBuilder whereBuilder = WhereBuilder.b();
            whereBuilder.and("运单编号", "=", barcode);
            db.update(ZCFajianFileContent.class, whereBuilder, new KeyValue
                    ("是否可用", "不可用"));
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 每次扫描后，先将数据存入数据库，需要的数据可根据ZCFajianFileContent对应
     */
    public static boolean insertDataToDatabase(final ZCFajianFileContent zcFajianFileContent) {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            db.save(zcFajianFileContent);
            return true;
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 判断数据库中是否有当前运单记录
     *
     * @param barcode
     * @return
     */
    public static boolean isExistCurrentBarcode(String barcode) {
        if (BQDataBaseHelper.tableIsExist(Constant.DB_TABLE_NAME_LOAD_SEND)) {
            DbManager dbManager = BQDataBaseHelper.getDb();
            try {
                List<ZCFajianFileContent> bean = dbManager.selector(ZCFajianFileContent.class)
                        .where("运单编号", "like", barcode).and("是否可用", "like", "可用").findAll();
                if (bean != null && bean.size() != 0) {
                    LogUtil.trace("size:" + bean.size());
                    for (int index = 0; index < bean.size(); index++) {
                        long[] delta = TextStringUtil.getDistanceTimes(bean.get(index)
                                .getScanDate(), TextStringUtil.getFormatTimeString());
                        if (TimeUtil.isTimeOutOfRange(delta)) {
                            // 超出指定时间，存入数据库 --> return false
                            LogUtil.trace("超出指定时间");

                            continue;
                        } else {
                            // 不需存入数据库 --> return true
                            LogUtil.trace("在指定时间之内");
                            return true;
                        }
                    }
                } else {
                    // do nothing
                }
            } catch (DbException e) {
                LogUtil.trace("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return false;
    }

    /*public void reQueryUnUploadDataForListView() {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            // 查询数据库中标识位“未上传”的记录
            List<ZCFajianFileContent> data = db.selector(ZCFajianFileContent.class).where("是否上传",
                    "like", "未上传").and("是否可用", "=", "可用").findAll();
            if (null != data && data.size() != 0) {
                LogUtil.d(TAG, "未上传记录：" + data.size());

                // 清除数据
                mListData.clear();

                int count = 0;
                for (int index = 0; index < data.size(); index++) {
                    ScannerListViewBean fajianListViewBean = new ScannerListViewBean();
                    // 一旦删除记录，则及时更新ID值
                    fajianListViewBean.setId(++count);
                    fajianListViewBean.setScannerData(data.get(index).getShipmentNumber());
                    fajianListViewBean.setStatus("未上传");
                    mListData.add(fajianListViewBean);
                }

                mScannerBaseAdatper.notifyDataSetChanged();
                // 更新全局ID
                mScanCount = count;
            } else {
                // 清除数据
                mListData.clear();
                mScannerBaseAdatper.notifyDataSetChanged();
                // 更新全局ID
                mScanCount = 0;
                LogUtil.trace("未上传 && 可用，过滤后无数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }*/
}
