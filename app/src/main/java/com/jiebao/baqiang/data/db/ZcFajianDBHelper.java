package com.jiebao.baqiang.data.db;

import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.BQTimeUtil;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 装车发件 数据库操作
 * 1. 获取所有记录数（可用类型的）
 * 2. 获取所有可用记录
 * 3. 获取指定时间范围内可用记录
 * 4. 获取未上传记录数
 * 5. 统计指定时间范围内的 总可用 记录数
 * 6. 统计指定时间范围内的 未上传 记录数
 * 7. 根据运单编号，设置数据为不可用
 * 8. 记录存入数据库
 * 9. 判断是否存在当前运单记录
 * 10. 判断指定记录是否已经上传
 */

public class ZcFajianDBHelper {
    private static final String TAG = ZcFajianDBHelper.class.getSimpleName();

    /**
     * 获取新加入的记录内容，根据运单号和扫码时间，可以唯一确定一条记录
     *
     * @param barcode：运单号
     * @param scanTime：扫描时间
     * @return
     */
    public static ZCFajianFileContent getNewInRecord(String barcode, Date
            scanTime) {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<ZCFajianFileContent> list = db.selector(ZCFajianFileContent
                    .class).where
                    ("ShipmentID", "=", barcode).and("ScanDate", "=",
                    scanTime).findAll();
            if (list != null && list.size() == 1) {
                return list.get(0);
            } else {
                LogUtil.trace("存在多条记录，该获取唯一记录的方法有错误");
            }
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取所有装车发件记录数（可用类型的）
     * <p>
     * 1. 数据必须是可用的
     *
     * @return
     */
    public static int findUsableRecords() {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<ZCFajianFileContent> list = db.selector(ZCFajianFileContent
                    .class).where
                    ("IsUsed", "=", "Used").findAll();
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
     * 获取所有可用记录
     *
     * @return
     */
    public static List<ZCFajianFileContent> getUsableRecords() {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<ZCFajianFileContent> data = db.selector(ZCFajianFileContent
                    .class).where
                    ("IsUsed", "=", "Used").findAll();
            if (null != data && data.size() != 0) {
                return data;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取 指定时间范围内 可用 记录
     * <p>
     * 1. 可用数据；
     * 2. 满足时间限制；
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static List<ZCFajianFileContent> getLimitedTimeRecords(long beginTime, long endTime) {
        LogUtil.trace("beginTime:" + beginTime + "; endTime:" + endTime);

        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<ZCFajianFileContent> list = db.selector(ZCFajianFileContent
                    .class).where
                    ("IsUsed", "=", "Used").and("ScanDate", ">=", new Date
                    (beginTime)).and
                    ("ScanDate", "<=", new Date(endTime)).findAll();
            if (list != null) {
                return list;
            }
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 统计指定时间范围内的 总可用 记录数
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static int findTimeLimitedUsableRecords(long beginTime, long
            endTime) {
        LogUtil.trace("beginTime:" + beginTime + "; endTime:" + endTime);

        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<ZCFajianFileContent> list = db.selector(ZCFajianFileContent
                    .class).where("IsUsed", "=", "Used").and("ScanDate",
                    ">=", new Date(beginTime)).and("ScanDate", "<=", new Date
                    (endTime)).findAll();
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
     * 统计 指定时间内 已上传总数
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static int findTimeLimitedUploadRecords(long beginTime, long
            endTime) {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<ZCFajianFileContent> list = db.selector(ZCFajianFileContent
                    .class).where("IsUsed", "=", "Used").and("IsUpload", "=",
                    "Load").and("ScanDate", ">=", new Date(beginTime)).and
                    ("ScanDate", "<=", new Date(endTime)).findAll();
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
     * 获取装车发件未上传记录数
     * <p>
     * 1. 可用的；
     * 2. 未上传的；
     *
     * @return
     */
    public static int findUnloadRecords() {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<ZCFajianFileContent> list = db.selector(ZCFajianFileContent
                    .class).where
                    ("IsUsed", "=", "Used").and("IsUpload", "=", "Unload")
                    .findAll();
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
     * 统计指定时间范围内的 未上传 记录数
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static int findTimeLimitedUnloadRecords(long beginTime, long
            endTime) {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<ZCFajianFileContent> list = db.selector(ZCFajianFileContent
                    .class).where
                    ("IsUsed", "=", "Used").and("IsUpload", "=", "Unload")
                    .and("ScanDate", ">=",
                            new Date(beginTime)).and("ScanDate", "<=", new Date
                            (endTime)).findAll();
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
     * 根据运单编号，设置数据为不可用。
     * <p>
     * 1. 运单编号匹配；
     * 2. 未上传 数据；
     *
     * @param barcode
     */
    public static boolean deleteFindedBean(final String barcode) {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            WhereBuilder whereBuilder = WhereBuilder.b();
            // FIXME 3小之外，没有上传，相同的barcode设置为Unused？
            whereBuilder.and("ShipmentID", "=", barcode);
            whereBuilder.and("IsUpload", "=", "Unload");
            db.update(ZCFajianFileContent.class, whereBuilder, new KeyValue
                    ("IsUsed", "Unused"));

            return true;
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 根据运单ID，设置数据为不可用。
     * <p>
     * 1. 运单编号匹配；
     * 2. 未上传 数据；
     *
     * @param barcodeID
     */
    public static boolean deleteFindedBean(final int barcodeID) {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            WhereBuilder whereBuilder = WhereBuilder.b();
            whereBuilder.and("id", "=", barcodeID);
            db.update(ZCFajianFileContent.class, whereBuilder, new KeyValue
                    ("IsUsed", "Unused"));

            return true;
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 每次扫描后，先将数据存入数据库，需要的数据可根据ZCFajianFileContent对应
     * <p>
     * 1. 存入数据库；
     * 2. 生成了 ID；
     * 3. 生成了 是否可用、是否上传的状态；
     */
    public static boolean insertDataToDatabase(final ZCFajianFileContent
                                                       zcFajianFileContent) {
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
     * <p>
     * 1. 匹配运单编号；
     * 2. 数据可用；
     * 3. （数据可用的情况下）3小时内不能再次录入；
     *
     * @param barcode
     * @return
     */
    public static boolean isExistCurrentBarcode(String barcode) {
        if (BQDataBaseHelper.tableIsExist(Constant.DB_TABLE_NAME_LOAD_SEND)) {
            DbManager dbManager = BQDataBaseHelper.getDb();
            try {
                List<ZCFajianFileContent> bean = dbManager.selector
                        (ZCFajianFileContent.class)
                        .where("ShipmentID", "=", barcode).and("IsUsed", "=",
                                "Used").findAll();
                if (bean != null && bean.size() != 0) {
                    LogUtil.trace("size:" + bean.size());
                    for (int index = 0; index < bean.size(); index++) {
                        long[] delta = TextStringUtil.getDistanceTimes(new
                                        SimpleDateFormat
                                        ("yyyyMMddHHmmss").format(bean.get
                                        (index).getScanDate()),
                                TextStringUtil.getFormatTimeString());
                        if (BQTimeUtil.isTimeOutOfRange(delta)) {
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

    /**
     * 搜索指定记录是否已经上传
     * <p>
     * 1. 运单号匹配；
     * 2. 可用；
     *
     * @param barcode
     * @return
     */
    public static boolean isRecordUpload(String barcode) {
        DbManager dbManager = BQDataBaseHelper.getDb();

        try {
            List<ZCFajianFileContent> list = dbManager.selector
                    (ZCFajianFileContent.class).where
                    ("IsUsed", "=", "Used").and("ShipmentID", "=", barcode)
                    .findAll();
            if (list != null && list.size() != 0) {
                LogUtil.trace("search size:" + list.size());
                if ("Unload".equals(list.get(0).getStatus())) {
                    return false;
                } else if ("Load".equals(list.get(0).getStatus())) {
                    return true;
                } else {
                    // do nothing
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据 ID 搜索指定记录是否已经上传
     * <p>
     * 1. ID 匹配
     * 2. 可用；
     *
     * @param recordID
     * @return
     */
    public static boolean isRecordUpload(int recordID) {
        DbManager dbManager = BQDataBaseHelper.getDb();

        try {
            List<ZCFajianFileContent> list = dbManager.selector
                    (ZCFajianFileContent.class).where
                    ("id", "=", recordID).findAll();
            if (list != null && list.size() != 0) {
                LogUtil.trace("search size:" + list.size());

                if ("Unload".equals(list.get(0).getStatus())) {
                    return false;
                } else if ("Load".equals(list.get(0).getStatus())) {
                    return true;
                } else {
                    // do nothing
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*public void reQueryUnUploadDataForListView() {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            // 查询数据库中标识位“未上传”的记录
            List<ZCFajianFileContent> data = db.selector(ZCFajianFileContent
            .class).where("是否上传",
                    "like", "未上传").and("是否可用", "=", "可用").findAll();
            if (null != data && data.size() != 0) {
                LogUtil.d(TAG, "未上传记录：" + data.size());

                // 清除数据
                mListData.clear();

                int count = 0;
                for (int index = 0; index < data.size(); index++) {
                    ScannerListViewBean fajianListViewBean = new
                    ScannerListViewBean();
                    // 一旦删除记录，则及时更新ID值
                    fajianListViewBean.setId(++count);
                    fajianListViewBean.setScannerData(data.get(index)
                    .getShipmentNumber());
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
