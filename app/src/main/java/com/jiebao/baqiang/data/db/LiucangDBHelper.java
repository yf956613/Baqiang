package com.jiebao.baqiang.data.db;

import com.jiebao.baqiang.data.bean.FileContentHelper;
import com.jiebao.baqiang.data.stay.StayHouseFileContent;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.BQTimeUtil;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 留仓件扫码 数据库
 */

public class LiucangDBHelper {
    private static final String TAG = LiucangDBHelper.class.getSimpleName();

    /**
     * 删除指定时间的记录
     *
     * @param date
     */
    public static void deleteSpecialTimeRecords(Date date) {
        DbManager dbManager = BQDataBaseHelper.getDb();
        try {
            if (dbManager != null) {
                // 删除指定时间之前，且是上传或无用数据
                List<StayHouseFileContent> stayHouseFileContents = dbManager
                        .selector
                        (StayHouseFileContent.class).where("ScanDate", "<=",
                                date).and
                        ("IsUpload", "=", "Load").or("ScanDate", "<=", date)
                        .and("IsUsed", "=",
                        "Unused").findAll();
                if (stayHouseFileContents != null && stayHouseFileContents
                        .size() != 0) {
                    for (int index = 0; index < stayHouseFileContents.size();
                         index++) {
                        dbManager.delete(StayHouseFileContent.class,
                                WhereBuilder.b("id", "=",
                                stayHouseFileContents.get(index).getId()));
                    }
                }
            }
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }
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
            List<StayHouseFileContent> list = db.selector
                    (StayHouseFileContent.class).where
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

    public static int getAllUsedRecords() {
        // 返回按键，不上传文件
        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<StayHouseFileContent> list = db.findAll(StayHouseFileContent
                    .class);
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
            List<StayHouseFileContent> list = dbManager.selector
                    (StayHouseFileContent.class)
                    .where("id", "=", recordID).findAll();
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
     * 获取新加入的记录内容，根据运单号和扫码时间，可以唯一确定一条记录
     *
     * @param barcode：运单号
     * @param scanTime：扫描时间
     * @return
     */
    public static StayHouseFileContent getNewInRecord(String barcode, Date
            scanTime) {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<StayHouseFileContent> list = db.selector
                    (StayHouseFileContent.class).where
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
            db.update(StayHouseFileContent.class, whereBuilder, new KeyValue
                    ("IsUsed", "Unused"));

            return true;
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 每次扫描后，先将数据存入数据库
     * <p>
     * 1. 存入数据库；
     * 2. 生成了 ID；
     * 3. 生成了 是否可用、是否上传的状态；
     */
    public static boolean insertDataToDatabase(final StayHouseFileContent
                                                       stayHouseFileContent) {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            db.save(stayHouseFileContent);
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
        if (BQDataBaseHelper.tableIsExist(Constant.DB_TABLE_NAME_STAY_HOUSE)) {
            DbManager dbManager = BQDataBaseHelper.getDb();
            try {
                List<StayHouseFileContent> bean = dbManager.selector
                        (StayHouseFileContent.class)
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
     * 获取 指定时间范围内 可用 记录
     * <p>
     * 1. 可用数据；
     * 2. 满足时间限制；
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static List<StayHouseFileContent> getLimitedTimeRecords(long beginTime, long endTime) {
        LogUtil.trace("beginTime:" + beginTime + "; endTime:" + endTime);

        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<StayHouseFileContent> list = db.selector
                    (StayHouseFileContent.class).where
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
            List<StayHouseFileContent> list = db.selector
                    (StayHouseFileContent.class).where
                    ("IsUsed", "=", "Used").and("ScanDate", ">=", new Date
                    (beginTime)).and
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
            List<StayHouseFileContent> list = db.selector
                    (StayHouseFileContent.class).where
                    ("IsUsed", "=", "Used").and("IsUpload", "=", "Load").and
                    ("ScanDate", ">=",
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
            List<StayHouseFileContent> list = db.selector
                    (StayHouseFileContent.class).where
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
     * 增加指定数目的记录，仅供测试用
     *
     * @return
     */
    public static boolean addSpecialNumberRecords() {
        DbManager db = BQDataBaseHelper.getDb();

        List<StayHouseFileContent> list = new ArrayList<>();
        Date scanDate = new Date();
        StayHouseFileContent value = FileContentHelper
                .getStayHouseFileContent();
        value.setScanDate(scanDate);
        value.setShipmentNumber("0000000000");
        value.setShipmentType("");
        value.setStayReason("10");
        value.setOperateDate(new SimpleDateFormat("yyyyMMdd").format(scanDate));

        for (int index = 0; index < Constant.TEST_ADD_RECORDS_NUMBER; index++) {
            list.add(value);
        }
        try {
            db.save(list);
            return true;
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 将所有记录的 是否上传 状态，设置为未上传 仅供测试
     *
     * @return
     */
    public static boolean reversalAllRecords() {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            db.update(StayHouseFileContent.class, WhereBuilder.b("IsUpload",
                    "=", "Load"), new KeyValue("IsUpload", "Unload"));
            return true;
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
