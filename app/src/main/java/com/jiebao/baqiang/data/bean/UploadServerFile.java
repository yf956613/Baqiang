package com.jiebao.baqiang.data.bean;

import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.updateData.UpdateInterface;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.util.FileIOUtils;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * 文件上传封装类，其功能包括将指定内容写到当前文件中
 * <p>
 * 上传服务器Servlet地址固定，UploadServerFile为全局文件上传类
 */

public class UploadServerFile {
    private static final String TAG = UploadServerFile.class.getSimpleName();

    private File mFile;
    private String mUploadUrl = "";

    public UploadServerFile(File file) {
        this.mFile = file;
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
        mUploadUrl = SharedUtil.getServletAddresFromSP(BaqiangApplication.getContext(),
                NetworkConstant.UPLOAD_SERVLET);
        RequestParams params = new RequestParams(mUploadUrl);

        params.addQueryStringParameter("saleId", UpdateInterface.getSalesId());
        params.addQueryStringParameter("userName", UpdateInterface.getUserName());
        params.addQueryStringParameter("password", UpdateInterface.getPsw());
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
                LogUtil.trace();
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
                // TODO 判断是否上传成功
                LogUtil.trace();
            }
        });

        return false;
    }

}
