package com.jiebao.baqiang.data.bean;

import com.jiebao.baqiang.data.arrival.UnloadArrivalFileContent;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianDispatchFileName;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.List;

/**
 * 数据库 到 待上传文件的 功能封装类
 * <p>
 * 1. Activity --> CommonDbHelperToUploadFile --> CommonUploadFile
 * 2. CommonUploadFile 执行文件上传，并将结果反馈给 CommonDbHelperToUploadFile
 * 3. CommonDbHelperToUploadFile主要的工作是：封装数据库操作，让Activity不包含数据库操作
 * 4. CommonDbHelperToUploadFile将网络操作结果反馈给Activity
 * 5. 支持泛型
 */

public class CommonDbHelperToUploadFile<T> {
    private static final String TAG = CommonDbHelperToUploadFile.class.getSimpleName();

    private IDbHelperToUploadFileCallback mCallbackListener;

    public CommonDbHelperToUploadFile() {
    }

    public CommonDbHelperToUploadFile setCallbackListener(IDbHelperToUploadFileCallback
                                                                  callbackListener) {
        this.mCallbackListener = callbackListener;
        return this;
    }

    /**
     * 上传单条记录
     *
     * @param bean
     */
    public void uploadSingleRecord(T bean) {
        CommonUploadFile uploadFile = null;

        if (bean instanceof ZCFajianFileContent) {
            uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.ZCFJ_TYPE);
            final ZCFajianFileContent value = (ZCFajianFileContent) bean;
            String content = value.getmCurrentValue() + "\r\n";
            uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                @Override
                public boolean uploadSuccess(String s) {
                    DbManager db = BQDataBaseHelper.getDb();
                    WhereBuilder whereBuilder = WhereBuilder.b();
                    whereBuilder.and("id", "=", value.getId());
                    LogUtil.trace("上传单条记录 ID：" + value.getId());

                    try {
                        int result = db.update(ZCFajianFileContent.class, whereBuilder, new
                                KeyValue("IsUpload", "Load"));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    mCallbackListener.onSuccess(s);

                    return true;
                }

                @Override
                public boolean uploadError(Throwable throwable, boolean b) {
                    mCallbackListener.onError(throwable, b);

                    return false;
                }

                @Override
                public boolean uploadCancel(Callback.CancelledException e) {
                    return false;
                }

                @Override
                public boolean uploadFinish() {
                    mCallbackListener.onFinish();

                    return false;
                }
            });

            uploadFile.writeContentToFile(content, true);
            uploadFile.uploadFile();
        } else if (bean instanceof UnloadArrivalFileContent) {
            uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.XCDJ_TYPE);
            final UnloadArrivalFileContent value = (UnloadArrivalFileContent) bean;
            String content = value.getmCurrentValue() + "\r\n";
            uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                @Override
                public boolean uploadSuccess(String s) {
                    DbManager db = BQDataBaseHelper.getDb();
                    WhereBuilder whereBuilder = WhereBuilder.b();
                    whereBuilder.and("id", "=", value.getId());
                    LogUtil.trace("上传单条记录 ID：" + value.getId());

                    try {
                        int result = db.update(UnloadArrivalFileContent.class, whereBuilder, new
                                KeyValue("IsUpload", "Load"));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    mCallbackListener.onSuccess(s);

                    return true;
                }

                @Override
                public boolean uploadError(Throwable throwable, boolean b) {
                    mCallbackListener.onError(throwable, b);

                    return false;
                }

                @Override
                public boolean uploadCancel(Callback.CancelledException e) {
                    return false;
                }

                @Override
                public boolean uploadFinish() {
                    mCallbackListener.onFinish();

                    return false;
                }
            });

            uploadFile.writeContentToFile(content, true);
            uploadFile.uploadFile();
        } else {
            // do other things
        }
    }

    /**
     * 上传 单一功能项 的所有记录
     * <p>
     * 1. 根据上述集合中record 的 ID值匹配记录；
     * 2. 文件上传成功后，更新 是否上传 标志位
     *
     * @param records：对应功能项的JavaBean集合
     */
    public void redoUploadRecords(final List<T> records) {
        CommonUploadFile uploadFile = null;
        if (records != null && records.size() != 0) {
            if (records.get(0) instanceof ZCFajianFileContent) {
                // ZCFajianFileContent类型
                uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.ZCFJ_TYPE);

                for (int index = 0; index < records.size(); index++) {
                    ZCFajianFileContent record = (ZCFajianFileContent) records.get(index);
                    String content = record.getmCurrentValue() + "\r\n";
                    // 循环写入文件
                    uploadFile.writeContentToFile(content, true);
                }

                uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                    @Override
                    public boolean uploadSuccess(String s) {
                        DbManager db = BQDataBaseHelper.getDb();

                        for (int index = 0; index < records.size(); index++) {
                            ZCFajianFileContent bean = (ZCFajianFileContent) records.get(index);
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            /*whereBuilder.and("ShipmentID", "=", value.getShipmentNumber());
                            whereBuilder.and("IsUsed", "=", "Used");
                            whereBuilder.and("IsUpload", "=", "Unload");*/
                            whereBuilder.and("id", "=", bean.getId());
                            LogUtil.trace("上传所有记录 ID：" + bean.getId());
                            try {
                                db.update(ZCFajianFileContent.class, whereBuilder, new KeyValue
                                        ("IsUpload", "Load"));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }

                        mCallbackListener.onSuccess(s);
                        return true;
                    }

                    @Override
                    public boolean uploadError(Throwable throwable, boolean b) {
                        mCallbackListener.onError(throwable, b);

                        return false;
                    }

                    @Override
                    public boolean uploadCancel(Callback.CancelledException e) {
                        return false;
                    }

                    @Override
                    public boolean uploadFinish() {
                        mCallbackListener.onFinish();

                        return false;
                    }
                });

                uploadFile.uploadFile();
            } else if (records.get(0) instanceof UnloadArrivalFileContent) {
                LogUtil.trace("<111111111111111>");
                // UnloadArrivalFileContent类型
                uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.XCDJ_TYPE);

                for (int index = 0; index < records.size(); index++) {
                    UnloadArrivalFileContent record = (UnloadArrivalFileContent) records.get(index);
                    String content = record.getmCurrentValue() + "\r\n";
                    // 循环写入文件
                    uploadFile.writeContentToFile(content, true);
                }

                uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                    @Override
                    public boolean uploadSuccess(String s) {
                        DbManager db = BQDataBaseHelper.getDb();

                        for (int index = 0; index < records.size(); index++) {
                            UnloadArrivalFileContent bean = (UnloadArrivalFileContent) records
                                    .get(index);
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("id", "=", bean.getId());
                            LogUtil.trace("上传所有记录 ID：" + bean.getId());
                            try {
                                db.update(UnloadArrivalFileContent.class, whereBuilder, new
                                        KeyValue("IsUpload", "Load"));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }

                        mCallbackListener.onSuccess(s);
                        return true;
                    }

                    @Override
                    public boolean uploadError(Throwable throwable, boolean b) {
                        mCallbackListener.onError(throwable, b);

                        return false;
                    }

                    @Override
                    public boolean uploadCancel(Callback.CancelledException e) {
                        return false;
                    }

                    @Override
                    public boolean uploadFinish() {
                        mCallbackListener.onFinish();

                        return false;
                    }
                });

                uploadFile.uploadFile();
            } else {
                // 其他类型
            }
        } else {
            // do nothing
        }
    }

    // 顺序上传状态表
    private HashMap mUploadStatus = null;

    /**
     * 自动上传、F1上传 全局未上传文件功能
     */
    public void uploadUnloadRecords() {
        mUploadStatus = new HashMap<String, Boolean>();

        CommonUploadFile uploadFile = null;
        DbManager db = BQDataBaseHelper.getDb();

        try {
            final List<ZCFajianFileContent> zcfjListData = db.selector(ZCFajianFileContent.class)
                    .where("IsUpload", "=", "Unload").and("IsUsed", "=", "Used").findAll();
            final List<UnloadArrivalFileContent> xcdjListData = db.selector
                    (UnloadArrivalFileContent.class).where("IsUpload", "=", "Unload").and
                    ("IsUsed", "=", "Used").findAll();
            if (null != zcfjListData && zcfjListData.size() != 0) {
                // 上传 ZCFajianFileContent
                uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.ZCFJ_TYPE);
                for (int index = 0; index < zcfjListData.size(); index++) {
                    ZCFajianFileContent record = zcfjListData.get(index);
                    String content = record.getmCurrentValue() + "\r\n";
                    // 循环写入文件
                    uploadFile.writeContentToFile(content, true);
                }

                uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                    @Override
                    public boolean uploadSuccess(String s) {
                        DbManager db = BQDataBaseHelper.getDb();

                        for (int index = 0; index < zcfjListData.size(); index++) {
                            ZCFajianFileContent bean = (ZCFajianFileContent) zcfjListData.get
                                    (index);
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("id", "=", bean.getId());
                            LogUtil.trace("上传所有记录 ID：" + bean.getId());
                            try {
                                db.update(ZCFajianFileContent.class, whereBuilder, new KeyValue
                                        ("IsUpload", "Load"));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }

                        // mCallbackListener.onSuccess(s);
                        mUploadStatus.put("zcfj", true);
                        return true;
                    }

                    @Override
                    public boolean uploadError(Throwable throwable, boolean b) {
                        // mCallbackListener.onError(throwable, b);

                        mUploadStatus.put("zcfj", false);
                        return false;
                    }

                    @Override
                    public boolean uploadCancel(Callback.CancelledException e) {
                        return false;
                    }

                    @Override
                    public boolean uploadFinish() {
                        // mCallbackListener.onFinish();

                        return false;
                    }
                });
                uploadFile.uploadFile();
            } else {
                // do nothing
                LogUtil.trace("装车发件没有数据可上传");

                mUploadStatus.put("zcfj", true);
            }

            if (xcdjListData != null && xcdjListData.size() != 0) {
                LogUtil.trace("<11111111111111>:size:" + xcdjListData.size());
                // 上传 UnloadArrivalFileContent
                uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.XCDJ_TYPE);
                for (int index = 0; index < xcdjListData.size(); index++) {
                    UnloadArrivalFileContent record = xcdjListData.get(index);
                    String content = record.getmCurrentValue() + "\r\n";
                    // 循环写入文件
                    uploadFile.writeContentToFile(content, true);
                }

                uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                    @Override
                    public boolean uploadSuccess(String s) {
                        LogUtil.trace("<2222222222222>");
                        DbManager db = BQDataBaseHelper.getDb();

                        for (int index = 0; index < xcdjListData.size(); index++) {
                            UnloadArrivalFileContent bean = (UnloadArrivalFileContent)
                                    xcdjListData.get(index);
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("id", "=", bean.getId());
                            LogUtil.trace("上传所有记录 ID：" + bean.getId());
                            try {
                                db.update(UnloadArrivalFileContent.class, whereBuilder, new
                                        KeyValue("IsUpload", "Load"));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }

                        mCallbackListener.onSuccess(s);
                        mUploadStatus.put("xcdj", true);
                        return true;
                    }

                    @Override
                    public boolean uploadError(Throwable throwable, boolean b) {
                        // mCallbackListener.onError(throwable, b);
                        mUploadStatus.put("xcdj", false);
                        return false;
                    }

                    @Override
                    public boolean uploadCancel(Callback.CancelledException e) {
                        return false;
                    }

                    @Override
                    public boolean uploadFinish() {
                        // mCallbackListener.onFinish();

                        return false;
                    }
                });

                uploadFile.uploadFile();
            } else {
                // do nothing
                LogUtil.trace("卸车到件没有数据可上传");
                mUploadStatus.put("xcdj", true);
            }
        } catch (DbException e) {
            LogUtil.d(TAG, "崩溃信息:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
