package com.jiebao.baqiang.data.bean;

import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

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
                    whereBuilder.and("ShipmentID", "=", value.getShipmentNumber());
                    whereBuilder.and("IsUsed", "=", "Used");
                    whereBuilder.and("IsUpload", "=", "Unload");
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
        } else {
            // do other things
        }

    }


}
