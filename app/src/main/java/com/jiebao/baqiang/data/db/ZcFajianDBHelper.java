package com.jiebao.baqiang.data.db;

import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class ZcFajianDBHelper {

    /**
     * 根据运单编号删除对应项
     *
     * @param barcode
     */
    public static void deleteFindedBean(final String barcode) {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            db.delete(ZCFajianFileContent.class, WhereBuilder.b("运单编号", "like", barcode));
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }
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
