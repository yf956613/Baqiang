package com.jiebao.baqiang.data.db;

import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.stay.StayHouseFileContent;
import com.jiebao.baqiang.data.stay.StayHouseFileName;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * Created by Administrator on 2018/3/22 0022.
 */

public class LiucangDBHelper {
    private static final String TAG = LiucangDBHelper.class.getSimpleName();

    /**
     * 上传数据库中所有 未上传的 留仓件 记录
     *
     * 不更新SP中统计值
     */
    public static void uploadLiucangUnloadRecords() {
        DbManager db = BQDataBaseHelper.getDb();
        List<StayHouseFileContent> list = null;
        try {
            // FIXME 1. 查询数据库中标识位是“未上传”的记录，且是数据可用
            list = db.selector(StayHouseFileContent.class).where("是否上传", "like", "未上传").and
                    ("是否可用", "=", "可用").findAll();
            if (null != list && list.size() != 0) {
                StayHouseFileName mStayHouseFileName = new StayHouseFileName();
                if (mStayHouseFileName.linkToTXTFile()) {
                    UploadServerFile mUploadServerFile = new UploadServerFile(mStayHouseFileName
                            .getFileInstance());

                    for (int index = 0; index < list.size(); index++) {
                        StayHouseFileContent javaBean = list.get(index);
                        String content = javaBean.getmCurrentValue() + "\r\n";
                        if (mUploadServerFile.writeContentToFile(content, true)) {
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("运单编号", "=", javaBean.getShipmentNumber());
                            db.update(StayHouseFileContent.class, whereBuilder, new KeyValue
                                    ("是否上传", "已上传"));
                        } else {
                            // TODO 写入文件失败
                            LogUtil.trace("写入文件失败");
                        }
                    }

                    mUploadServerFile.uploadFile();
                } else {
                    // TODO 创建文件失败
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
}
