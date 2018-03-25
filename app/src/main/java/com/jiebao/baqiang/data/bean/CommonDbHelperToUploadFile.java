package com.jiebao.baqiang.data.bean;

import com.jiebao.baqiang.data.arrival.CargoArrivalFileContent;
import com.jiebao.baqiang.data.arrival.UnloadArrivalFileContent;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.dispatch.ShipmentFileContent;
import com.jiebao.baqiang.data.stay.StayHouseFileContent;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianDispatchFileName;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        } else if (bean instanceof CargoArrivalFileContent) {
            uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.DJ_TYPE);
            final CargoArrivalFileContent value = (CargoArrivalFileContent) bean;
            String content = value.getmCurrentValue() + "\r\n";
            uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                @Override
                public boolean uploadSuccess(String s) {
                    DbManager db = BQDataBaseHelper.getDb();
                    WhereBuilder whereBuilder = WhereBuilder.b();
                    whereBuilder.and("id", "=", value.getId());
                    LogUtil.trace("上传单条记录 ID：" + value.getId());

                    try {
                        int result = db.update(CargoArrivalFileContent.class, whereBuilder, new
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
        } else if (bean instanceof ShipmentFileContent) {
            uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.FJ_TYPE);
            final ShipmentFileContent value = (ShipmentFileContent) bean;
            String content = value.getmCurrentValue() + "\r\n";
            uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                @Override
                public boolean uploadSuccess(String s) {
                    DbManager db = BQDataBaseHelper.getDb();
                    WhereBuilder whereBuilder = WhereBuilder.b();
                    whereBuilder.and("id", "=", value.getId());
                    LogUtil.trace("上传单条记录 ID：" + value.getId());

                    try {
                        int result = db.update(ShipmentFileContent.class, whereBuilder, new
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
        } else if (bean instanceof StayHouseFileContent) {
            uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.LC_TYPE);
            final StayHouseFileContent value = (StayHouseFileContent) bean;
            String content = value.getmCurrentValue() + "\r\n";
            uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                @Override
                public boolean uploadSuccess(String s) {
                    DbManager db = BQDataBaseHelper.getDb();
                    WhereBuilder whereBuilder = WhereBuilder.b();
                    whereBuilder.and("id", "=", value.getId());
                    LogUtil.trace("上传单条记录 ID：" + value.getId());

                    try {
                        int result = db.update(StayHouseFileContent.class, whereBuilder, new
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
            } else if (records.get(0) instanceof CargoArrivalFileContent) {
                // CargoArrivalFileContent类型
                uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.DJ_TYPE);

                for (int index = 0; index < records.size(); index++) {
                    CargoArrivalFileContent record = (CargoArrivalFileContent) records.get(index);
                    String content = record.getmCurrentValue() + "\r\n";
                    // 循环写入文件
                    uploadFile.writeContentToFile(content, true);
                }

                uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                    @Override
                    public boolean uploadSuccess(String s) {
                        DbManager db = BQDataBaseHelper.getDb();

                        for (int index = 0; index < records.size(); index++) {
                            CargoArrivalFileContent bean = (CargoArrivalFileContent) records.get
                                    (index);
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("id", "=", bean.getId());
                            LogUtil.trace("上传所有记录 ID：" + bean.getId());
                            try {
                                db.update(CargoArrivalFileContent.class, whereBuilder, new
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
            } else if (records.get(0) instanceof ShipmentFileContent) {
                // ShipmentFileContent类型
                uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.FJ_TYPE);

                for (int index = 0; index < records.size(); index++) {
                    ShipmentFileContent record = (ShipmentFileContent) records.get(index);
                    String content = record.getmCurrentValue() + "\r\n";
                    // 循环写入文件
                    uploadFile.writeContentToFile(content, true);
                }

                uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                    @Override
                    public boolean uploadSuccess(String s) {
                        DbManager db = BQDataBaseHelper.getDb();

                        for (int index = 0; index < records.size(); index++) {
                            ShipmentFileContent bean = (ShipmentFileContent) records.get(index);
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("id", "=", bean.getId());
                            LogUtil.trace("上传所有记录 ID：" + bean.getId());
                            try {
                                db.update(ShipmentFileContent.class, whereBuilder, new KeyValue
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
            } else if (records.get(0) instanceof StayHouseFileContent) {
                // StayHouseFileContent类型
                uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.FJ_TYPE);

                for (int index = 0; index < records.size(); index++) {
                    StayHouseFileContent record = (StayHouseFileContent) records.get(index);
                    String content = record.getmCurrentValue() + "\r\n";
                    // 循环写入文件
                    uploadFile.writeContentToFile(content, true);
                }

                uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                    @Override
                    public boolean uploadSuccess(String s) {
                        DbManager db = BQDataBaseHelper.getDb();

                        for (int index = 0; index < records.size(); index++) {
                            StayHouseFileContent bean = (StayHouseFileContent) records.get(index);
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("id", "=", bean.getId());
                            LogUtil.trace("上传所有记录 ID：" + bean.getId());
                            try {
                                db.update(StayHouseFileContent.class, whereBuilder, new KeyValue
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
            }
        } else {
            // do nothing
        }
    }

    // 顺序上传状态表
    private volatile HashMap mUploadStatus = null;
    private int mUploadFileNumber = 0;

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
            final List<CargoArrivalFileContent> djListData = db.selector(CargoArrivalFileContent
                    .class).where("IsUpload", "=", "Unload").and("IsUsed", "=", "Used").findAll();
            final List<ShipmentFileContent> fjListData = db.selector(ShipmentFileContent.class)
                    .where("IsUpload", "=", "Unload").and("IsUsed", "=", "Used").findAll();
            final List<StayHouseFileContent> lcjListData = db.selector(StayHouseFileContent
                    .class).where("IsUpload", "=", "Unload").and("IsUsed", "=", "Used").findAll();

            // 需要上传哪些文件？
            if (null != zcfjListData && zcfjListData.size() != 0) {
                mUploadFileNumber++;
            }

            if (null != xcdjListData && xcdjListData.size() != 0) {
                mUploadFileNumber++;
            }

            if (null != djListData && djListData.size() != 0) {
                mUploadFileNumber++;
            }

            if (null != fjListData && fjListData.size() != 0) {
                mUploadFileNumber++;
            }

            if (null != lcjListData && lcjListData.size() != 0) {
                mUploadFileNumber++;
            }

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
                        mUploadStatus.put("zcfj", true);
                        if (mUploadStatus.size() >= mUploadFileNumber) {
                            mCallbackListener.onSuccess(isAllRecordsUploadSuccess());
                        }
                        return true;
                    }

                    @Override
                    public boolean uploadError(Throwable throwable, boolean b) {
                        mCallbackListener.onError(throwable, b);
                        mUploadStatus.put("zcfj", false);
                        return false;
                    }

                    @Override
                    public boolean uploadCancel(Callback.CancelledException e) {
                        return false;
                    }

                    @Override
                    public boolean uploadFinish() {
                        if (mUploadStatus.size() >= mUploadFileNumber) {
                            mCallbackListener.onFinish();
                        }

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

                        mUploadStatus.put("xcdj", true);
                        if (mUploadStatus.size() >= mUploadFileNumber) {
                            mCallbackListener.onSuccess(isAllRecordsUploadSuccess());
                        }
                        return true;
                    }

                    @Override
                    public boolean uploadError(Throwable throwable, boolean b) {
                        mCallbackListener.onError(throwable, b);
                        mUploadStatus.put("xcdj", false);
                        return false;
                    }

                    @Override
                    public boolean uploadCancel(Callback.CancelledException e) {
                        return false;
                    }

                    @Override
                    public boolean uploadFinish() {
                        if (mUploadStatus.size() >= mUploadFileNumber) {
                            mCallbackListener.onFinish();
                        }

                        return false;
                    }
                });

                uploadFile.uploadFile();
            } else {
                // do nothing
                LogUtil.trace("卸车到件没有数据可上传");
                mUploadStatus.put("xcdj", true);
            }

            if (null != djListData && djListData.size() != 0) {
                // 上传 CargoArrivalFileContent
                uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.DJ_TYPE);
                for (int index = 0; index < djListData.size(); index++) {
                    CargoArrivalFileContent record = djListData.get(index);
                    String content = record.getmCurrentValue() + "\r\n";
                    // 循环写入文件
                    uploadFile.writeContentToFile(content, true);
                }

                uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                    @Override
                    public boolean uploadSuccess(String s) {
                        DbManager db = BQDataBaseHelper.getDb();

                        for (int index = 0; index < djListData.size(); index++) {
                            CargoArrivalFileContent bean = (CargoArrivalFileContent) djListData
                                    .get(index);
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("id", "=", bean.getId());
                            LogUtil.trace("上传所有记录 ID：" + bean.getId());
                            try {
                                db.update(CargoArrivalFileContent.class, whereBuilder, new
                                        KeyValue("IsUpload", "Load"));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }

                        mUploadStatus.put("dj", true);
                        if (mUploadStatus.size() >= mUploadFileNumber) {
                            mCallbackListener.onSuccess(isAllRecordsUploadSuccess());
                        }
                        return true;
                    }

                    @Override
                    public boolean uploadError(Throwable throwable, boolean b) {
                        mCallbackListener.onError(throwable, b);
                        mUploadStatus.put("dj", false);
                        return false;
                    }

                    @Override
                    public boolean uploadCancel(Callback.CancelledException e) {
                        return false;
                    }

                    @Override
                    public boolean uploadFinish() {
                        if (mUploadStatus.size() >= mUploadFileNumber) {
                            mCallbackListener.onFinish();
                        }

                        return false;
                    }
                });
                uploadFile.uploadFile();
            } else {
                // do nothing
                LogUtil.trace("到件没有数据可上传");
                mUploadStatus.put("dj", true);
            }

            if (null != fjListData && fjListData.size() != 0) {
                // 上传 ShipmentFileContent
                uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.FJ_TYPE);
                for (int index = 0; index < fjListData.size(); index++) {
                    ShipmentFileContent record = fjListData.get(index);
                    String content = record.getmCurrentValue() + "\r\n";
                    // 循环写入文件
                    uploadFile.writeContentToFile(content, true);
                }

                uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                    @Override
                    public boolean uploadSuccess(String s) {
                        DbManager db = BQDataBaseHelper.getDb();

                        for (int index = 0; index < fjListData.size(); index++) {
                            ShipmentFileContent bean = (ShipmentFileContent) fjListData.get(index);
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("id", "=", bean.getId());
                            LogUtil.trace("上传所有记录 ID：" + bean.getId());
                            try {
                                db.update(ShipmentFileContent.class, whereBuilder, new KeyValue
                                        ("IsUpload", "Load"));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }

                        mUploadStatus.put("fj", true);
                        if (mUploadStatus.size() >= mUploadFileNumber) {
                            mCallbackListener.onSuccess(isAllRecordsUploadSuccess());
                        }

                        return true;
                    }

                    @Override
                    public boolean uploadError(Throwable throwable, boolean b) {
                        mCallbackListener.onError(throwable, b);
                        mUploadStatus.put("fj", false);
                        return false;
                    }

                    @Override
                    public boolean uploadCancel(Callback.CancelledException e) {
                        return false;
                    }

                    @Override
                    public boolean uploadFinish() {
                        if (mUploadStatus.size() >= mUploadFileNumber) {
                            mCallbackListener.onFinish();
                        }

                        return false;
                    }
                });
                uploadFile.uploadFile();
            } else {
                // do nothing
                LogUtil.trace("发件没有数据可上传");
                mUploadStatus.put("fj", true);
            }

            if (null != lcjListData && lcjListData.size() != 0) {
                // 上传 StayHouseFileContent
                uploadFile = new CommonUploadFile(CommonUploadFile.UploadFileType.LC_TYPE);
                for (int index = 0; index < lcjListData.size(); index++) {
                    StayHouseFileContent record = lcjListData.get(index);
                    String content = record.getmCurrentValue() + "\r\n";
                    // 循环写入文件
                    uploadFile.writeContentToFile(content, true);
                }

                uploadFile.setCallbackListener(new ICommonUpdateFileCallBack() {

                    @Override
                    public boolean uploadSuccess(String s) {
                        DbManager db = BQDataBaseHelper.getDb();

                        for (int index = 0; index < lcjListData.size(); index++) {
                            StayHouseFileContent bean = (StayHouseFileContent) lcjListData.get
                                    (index);
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("id", "=", bean.getId());
                            LogUtil.trace("上传所有记录 ID：" + bean.getId());
                            try {
                                db.update(StayHouseFileContent.class, whereBuilder, new KeyValue
                                        ("IsUpload", "Load"));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }

                        mUploadStatus.put("lcj", true);
                        if (mUploadStatus.size() >= mUploadFileNumber) {
                            mCallbackListener.onSuccess(isAllRecordsUploadSuccess());
                        }
                        return true;
                    }

                    @Override
                    public boolean uploadError(Throwable throwable, boolean b) {
                        mCallbackListener.onError(throwable, b);
                        mUploadStatus.put("lcj", false);
                        return false;
                    }

                    @Override
                    public boolean uploadCancel(Callback.CancelledException e) {
                        return false;
                    }

                    @Override
                    public boolean uploadFinish() {
                        if (mUploadStatus.size() >= mUploadFileNumber) {
                            mCallbackListener.onFinish();
                        }

                        return false;
                    }
                });
                uploadFile.uploadFile();
            } else {
                // do nothing
                LogUtil.trace("留仓件没有数据可上传");
                mUploadStatus.put("lcj", true);
            }
        } catch (DbException e) {
            LogUtil.d(TAG, "崩溃信息:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * 判断文件是否上传成功
     *
     * @return
     */
    private String isAllRecordsUploadSuccess() {
        LogUtil.trace("" + mUploadStatus);

        // 如果每个Key-Value中的Value是true，则表示上传成功
        StringBuffer sBuffer = new StringBuffer();
        Iterator iter = mUploadStatus.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            boolean value = (boolean) entry.getValue();
            System.out.println(key + ":" + value);

            if ("zcfj".equals(key)) {
                if (!value) {
                    sBuffer.append("装车发件文件上传失败 ");
                }
            } else if ("xcdj".equals(key)) {
                if (!value) {
                    sBuffer.append("卸车到件文件上传失败 ");
                }
            } else if ("dj".equals(key)) {
                if (!value) {
                    sBuffer.append("到件文件上传失败 ");
                }
            } else if ("fj".equals(key)) {
                if (!value) {
                    sBuffer.append("发件文件上传失败 ");
                }
            } else if ("lcj".equals(key)) {
                if (!value) {
                    sBuffer.append("留仓件文件上传失败 ");
                }
            }
        }
        return sBuffer.toString();
    }
}
