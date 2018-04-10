package com.jiebao.baqiang.data.bean;

import com.google.gson.Gson;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.updateData.UpdateInterface;
import com.jiebao.baqiang.global.FileConstant;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.util.FileIOUtils;
import com.jiebao.baqiang.util.FileUtils;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * 封装 上传文件 类，功能如下：
 * <p>
 * 1. 根据类型（装车发件、卸车到件、发件、到件、留仓件等），创建不同文件名的文件；
 * 2. 在网络可用的情况下，上传文件到服务器；
 * 3. 文件上传状态回调；
 */

public class CommonUploadFile {
    private static final String TAG = CommonUploadFile.class.getSimpleName();

    // 装车发件 文件名前缀
    private static final String ZCFJ_FILE_PREFIX = "zc";
    // 卸车到件 文件名前缀
    private static final String XCDJ_FILE_PREFIX = "xc";
    // 到件 文件名前缀
    private static final String DJ_FILE_PREFIX = "dj";
    // 发件 文件名前缀
    private static final String FJ_FILE_PREFIX = "fj";
    // 留仓件 文件名前缀
    private static final String LC_FILE_PREFIX = "lc";
    // 文件后缀
    private static final String FILE_SUFFIX = ".txt";

    // 文件名：时间
    private String mTime;
    // 文件名：不重复字段内容
    private String mNoRepeatString;
    // 完整文件名
    private String mCurrentFileName;

    private UploadFileType type;
    private File mFile;
    private String mUploadUrl = "";
    private ICommonUpdateFileCallBack mCallbackListener;

    public enum UploadFileType {
        ZCFJ_TYPE, XCDJ_TYPE, DJ_TYPE, FJ_TYPE, LC_TYPE
    }

    public CommonUploadFile() {

    }

    public CommonUploadFile(UploadFileType type) {
        this.mTime = TextStringUtil.getFormatTimeString();
        this.mNoRepeatString = TextStringUtil.generateNumberUuid();
        this.type = type;

        // /sdcard/BaQiang目录下生成文件
        mFile = getFileInstance();
    }

    public void setCallbackListener(ICommonUpdateFileCallBack
                                            callbackListener) {
        this.mCallbackListener = callbackListener;
    }

    /**
     * 生成文件
     *
     * @return
     */
    public File getFileInstance() {
        if (linkToTXTFile()) {
            return FileUtils.getFileByPath(mCurrentFileName);
        } else {
            return null;
        }
    }

    /**
     * 根据当前JavaBean字段内容，在SD卡的指定目录创建指定名称的文本
     *
     * @return
     */
    public boolean linkToTXTFile() {
        StringBuffer currentFileName = new StringBuffer();

        if (UploadFileType.ZCFJ_TYPE.equals(this.type)) {
            currentFileName.append(ZCFJ_FILE_PREFIX);
        } else if (UploadFileType.XCDJ_TYPE.equals(this.type)) {
            currentFileName.append(XCDJ_FILE_PREFIX);
        } else if (UploadFileType.DJ_TYPE.equals(this.type)) {
            currentFileName.append(DJ_FILE_PREFIX);
        } else if (UploadFileType.FJ_TYPE.equals(this.type)) {
            currentFileName.append(FJ_FILE_PREFIX);
        } else if (UploadFileType.LC_TYPE.equals(this.type)) {
            currentFileName.append(LC_FILE_PREFIX);
        } else {
            LogUtil.trace("没有匹配到类型");
        }

        currentFileName.append(this.mTime + this.mNoRepeatString + FILE_SUFFIX);
        final String fileName = FileConstant.APP_SDCARD_FILE_NAME +
                currentFileName.toString();

        if (FileUtils.createOrExistsFile(fileName)) {
            mCurrentFileName = fileName;
            return true;
        }

        return false;
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
            FileIOUtils.writeFileFromBytesByStream(mFile, valueContent
                            .getBytes("GB2312"),
                    isAppend);
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 上传文件
     *
     * @return
     */
    public void uploadFile() {
        mUploadUrl = SharedUtil.getServletAddresFromSP(BaqiangApplication
                        .getContext(),
                NetworkConstant.UPLOAD_SERVLET);
        RequestParams params = new RequestParams(mUploadUrl);

        params.addQueryStringParameter("saleId", UpdateInterface.getSalesId());
        params.addQueryStringParameter("userName", UpdateInterface
                .getUserName());
        params.addQueryStringParameter("password", UpdateInterface.getPsw());
        params.addBodyParameter("file", mFile);
        params.addQueryStringParameter(NetworkConstant.PKG_OWER, "zhang");
        params.addQueryStringParameter(NetworkConstant.PKG_NAME, mFile
                .getName());
        params.addQueryStringParameter(NetworkConstant.PKG_SIZE, "" + this
                .mFile.length());
        params.addQueryStringParameter(NetworkConstant.PGK_CHECKSUM, "sdfa");
        params.addQueryStringParameter(NetworkConstant.PKG_TYPE, "1");
        params.addQueryStringParameter(NetworkConstant.PKG_ENC, "0");

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String s) {
                LogUtil.trace("uploadFile:onSuccess-->" + s + "; type:" + type);

                Gson gson = new Gson();
                UploadFileResponseBean loginResponse = gson.fromJson(s,
                        UploadFileResponseBean.class);

                if (loginResponse != null) {
                    if (loginResponse.getUploadRet() == 1) {
                        mCallbackListener.uploadSuccess(s);
                    } else {
                        // do nothing 可能其他原因导致失败
                    }
                } else {
                    // 解析出错
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.trace("uploadFile:onError:" + throwable
                        .getLocalizedMessage());

                mCallbackListener.uploadError(throwable, b);
            }

            @Override
            public void onCancelled(CancelledException e) {
                LogUtil.trace("uploadFile:onCancelled:" + e
                        .getLocalizedMessage());
                mCallbackListener.uploadCancel(e);
            }

            @Override
            public void onFinished() {
                LogUtil.trace("onFinished:");
                mCallbackListener.uploadFinish();
            }
        });
    }
}
