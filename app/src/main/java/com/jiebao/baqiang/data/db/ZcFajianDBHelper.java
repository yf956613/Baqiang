package com.jiebao.baqiang.data.db;

import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianDispatchFileName;
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
 * 转车发件 数据库
 */

public class ZcFajianDBHelper {
    private static final String TAG = ZcFajianDBHelper.class.getSimpleName();

    /**
     * 获取所有装车发件记录数（可用类型的）
     *
     * @return
     */
    public static int findUsableRecords() {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<ZCFajianFileContent> list = db.selector(ZCFajianFileContent.class).where
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
            // 查询数据库中标识位“未上传”的记录
            List<ZCFajianFileContent> data = db.selector(ZCFajianFileContent.class).where
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
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static List<ZCFajianFileContent> getLimitedTimeRecords(Date beginTime, Date endTime) {
        LogUtil.trace("beginTime:" + beginTime + "; endTime:" + endTime);

        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<ZCFajianFileContent> list = db.selector(ZCFajianFileContent.class).where
                    ("IsUsed", "=", "Used").and("ScanDate", ">=", beginTime)/*.and("ScanDate",
                    "<=", endTime)*/.findAll();
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
     * 获取装车发件未上传记录数
     *
     * @return
     */
    public static int findUnloadRecords() {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            List<ZCFajianFileContent> list = db.selector(ZCFajianFileContent.class).where
                    ("IsUpload", "=", "Unload").findAll();
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
            whereBuilder.and("ShipmentID", "=", barcode);
            db.update(ZCFajianFileContent.class, whereBuilder, new KeyValue("IsUsed", "Unused"));
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
                        .where("ShipmentID", "=", barcode).and("IsUsed", "=", "Used").findAll();
                if (bean != null && bean.size() != 0) {
                    LogUtil.trace("size:" + bean.size());
                    for (int index = 0; index < bean.size(); index++) {
                        long[] delta = TextStringUtil.getDistanceTimes(new SimpleDateFormat
                                ("yyyyMMddHHmmss").format(bean.get(index).getScanDate()),
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
     * 上传数据库中所有 未上传的 装车发件 记录
     * <p>
     * 不更新SP中统计值
     */
    public static void uploadZcfjUnloadRecords() {
        DbManager db = BQDataBaseHelper.getDb();
        List<ZCFajianFileContent> list = null;
        try {
            list = db.selector(ZCFajianFileContent.class).where("IsUpload", "=", "Unload").and
                    ("IsUsed", "=", "Used").findAll();
            if (null != list && list.size() != 0) {
                ZCFajianDispatchFileName mZcFajianDispatchFileName = new ZCFajianDispatchFileName();
                if (mZcFajianDispatchFileName.linkToTXTFile()) {
                    UploadServerFile mZcfajianUploadFile = new UploadServerFile
                            (mZcFajianDispatchFileName.getFileInstance());

                    for (int index = 0; index < list.size(); index++) {
                        ZCFajianFileContent javaBean = list.get(index);
                        String content = javaBean.getmCurrentValue() + "\r\n";
                        if (mZcfajianUploadFile.writeContentToFile(content, true)) {
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("ShipmentID", "=", javaBean.getShipmentNumber());
                            whereBuilder.and("IsUsed", "=", "Used");
                            db.update(ZCFajianFileContent.class, whereBuilder, new KeyValue
                                    ("IsUpload", "Load"));
                        } else {
                            LogUtil.trace("写入文件失败");
                        }
                    }
                    // FIXME 文件是否上传成功
                    mZcfajianUploadFile.uploadFile();
                } else {
                    LogUtil.trace("创建文件失败");
                }
            } else {
                LogUtil.trace("当前数据库没有需要上传数据");
            }
        } catch (DbException e) {
            LogUtil.d(TAG, "崩溃信息:" + e.getLocalizedMessage());
            e.printStackTrace();
        }


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
