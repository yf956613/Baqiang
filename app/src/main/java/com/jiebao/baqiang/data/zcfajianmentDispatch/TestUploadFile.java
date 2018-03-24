package com.jiebao.baqiang.data.zcfajianmentDispatch;

import android.content.Context;
import android.content.SharedPreferences;

import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.IFileContentBean;
import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.dispatch.IShipmentFileUpload;
import com.jiebao.baqiang.data.updateData.UpdateInterface;
import com.jiebao.baqiang.global.IDownloadStatus;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.util.FileIOUtils;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Administrator on 2018/3/23 0023.
 */

public class TestUploadFile {

    private static final String TAG = ZCfajianUploadFile.class.getSimpleName();

    private File mFile;
    private String mUploadUrl = "";
    private IShipmentFileUpload mCallbackListener;

    public TestUploadFile(File file) {
        this.mFile = file;
    }

    public void setCallbackListener(IShipmentFileUpload callbackListener) {
        this.mCallbackListener = callbackListener;
    }

    /**
     * 将文本内容写入到文本文件中
     *
     * @param valueContent
     * @param isAppend
     * @return
     */
    public boolean writeContentToFile(String valueContent, boolean isAppend) {
        try {
            FileIOUtils.writeFileFromBytesByStream(mFile, valueContent.getBytes("GB2312"),
                    isAppend);
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean uploadFile() {
        LogUtil.trace("4444444444444444444444");
        mUploadUrl = SharedUtil.getServletAddresFromSP(BaqiangApplication.getContext(),
                NetworkConstant.UPLOAD_SERVLET);
        RequestParams params = new RequestParams(mUploadUrl);

        params.addQueryStringParameter("saleId", UpdateInterface.salesId);
        params.addQueryStringParameter("userName", UpdateInterface.userName);
        params.addQueryStringParameter("password", UpdateInterface.psw);
        params.addBodyParameter("file", mFile);
        params.addQueryStringParameter(NetworkConstant.PKG_OWER, "zhang");
        params.addQueryStringParameter(NetworkConstant.PKG_NAME, mFile.getName());
        params.addQueryStringParameter(NetworkConstant.PKG_SIZE, "" + this.mFile.length());
        params.addQueryStringParameter(NetworkConstant.PGK_CHECKSUM, "sdfa");
        params.addQueryStringParameter(NetworkConstant.PKG_TYPE, "1");
        params.addQueryStringParameter(NetworkConstant.PKG_ENC, "0");

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String s) {
                LogUtil.trace("<5555555555555555555555555>");
                mCallbackListener.uploadSuccess();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.trace("error: " + throwable.getMessage());
            }

            @Override
            public void onCancelled(CancelledException e) {
                LogUtil.trace();
            }

            @Override
            public void onFinished() {
                LogUtil.trace();
            }
        });

        return false;
    }

    /**
     * 单条记录上传
     * <p>
     * 1. 运单号匹配；
     * 2. 数据可用；
     * 3. 未上传；
     *
     * @param record
     */
    public static void singleRecordUpload(ZCFajianFileContent record) {
        DbManager db = BQDataBaseHelper.getDb();
        ZCFajianDispatchFileName mZcFajianDispatchFileName = new ZCFajianDispatchFileName();
        if (mZcFajianDispatchFileName.linkToTXTFile()) {
            UploadServerFile mZcfajianUploadFile = new UploadServerFile(mZcFajianDispatchFileName
                    .getFileInstance());
            String content = record.getmCurrentValue() + "\r\n";
            if (mZcfajianUploadFile.writeContentToFile(content, true)) {
                // FIXME 文件是否上传成功
                mZcfajianUploadFile.uploadFile();
            } else {
                LogUtil.trace("写入文件失败");
            }

            LogUtil.trace("<22222222222222>");
            // 1. 判断文件是否写入成功
            WhereBuilder whereBuilder = WhereBuilder.b();
            whereBuilder.and("ShipmentID", "=", record.getShipmentNumber());
            whereBuilder.and("IsUsed", "=", "Used");
            whereBuilder.and("IsUpload", "=", "Unload");
            try {
                int result = db.update(ZCFajianFileContent.class, whereBuilder, new KeyValue
                        ("IsUpload", "Load"));
            } catch (DbException e) {
                e.printStackTrace();
            }

        } else {
            LogUtil.trace("创建文件失败");
        }
    }

    /**
     * 搜索界面，重复上传记录，有可能包含有未上传记录
     * <p>
     * 1. 运单号匹配；
     * 2. 数据可用；
     * 3. 未上传；
     *
     * @param records
     */
    public static void redoUploadRecords(List<IFileContentBean> records) {
        LogUtil.trace();

        if (records != null && records.size() != 0) {
            DbManager db = BQDataBaseHelper.getDb();
            ZCFajianDispatchFileName mZcFajianDispatchFileName = new ZCFajianDispatchFileName();

            if (mZcFajianDispatchFileName.linkToTXTFile()) {
                UploadServerFile mZcfajianUploadFile = new UploadServerFile
                        (mZcFajianDispatchFileName.getFileInstance());

                for (int index = 0; index < records.size(); index++) {
                    ZCFajianFileContent record = (ZCFajianFileContent) records.get(index);
                    String content = record.getmCurrentValue() + "\r\n";
                    if (mZcfajianUploadFile.writeContentToFile(content, true)) {
                        WhereBuilder whereBuilder = WhereBuilder.b();
                        whereBuilder.and("ShipmentID", "=", record.getShipmentNumber());
                        whereBuilder.and("IsUsed", "=", "Used");
                        whereBuilder.and("IsUpload", "=", "Unload");
                        try {
                            db.update(ZCFajianFileContent.class, whereBuilder, new KeyValue
                                    ("IsUpload", "Load"));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
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
            // do nothing
        }
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
}
