package com.jiebao.baqiang.data.zcfajianmentDispatch;

import android.content.Context;
import android.content.SharedPreferences;

import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.dispatch.IShipmentFileUpload;
import com.jiebao.baqiang.global.NetworkConstant;
import com.jiebao.baqiang.util.FileIOUtils;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created by LENOVO on 2018/2/2.
 */

public class ZCfajianUploadFile  implements IShipmentFileUpload {
    private static final String TAG = ZCfajianUploadFile.class.getSimpleName();

    private File mFile;
    private String mUploadUrl = "";

    public ZCfajianUploadFile(File file) {
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
            FileIOUtils.writeFileFromBytesByStream(mFile, valueContent
                            .getBytes("GB2312"),
                    isAppend);
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean uploadFile() {
        SharedPreferences sp = BaqiangApplication.getContext()
                .getSharedPreferences
                        ("ServerInfo", Context.MODE_PRIVATE);
        if (sp != null) {
            String ip = sp.getString("Ip", "");
            String port = sp.getString("Port", "");
            mUploadUrl = NetworkConstant.HTTP_DOMAIN + ip + ":" + port +
                    NetworkConstant
                            .UPLOAD_SERVLET;
        }
        LogUtil.d(TAG, "Server login url: " + mUploadUrl);

        RequestParams params = new RequestParams(mUploadUrl);


        // 传输文件
        params.addBodyParameter("file", mFile);
        LogUtil.d(TAG, "name:"+mFile.getName());

        params.addQueryStringParameter(NetworkConstant.PKG_OWER, "zhang");
        params.addQueryStringParameter(NetworkConstant.PKG_NAME, mFile.getName());
        params.addQueryStringParameter(NetworkConstant.PKG_SIZE, ""+this.mFile.length());
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
                LogUtil.trace();
            }
        });

        return false;
    }

    @Override
    public boolean uploadSuccess() {
        return false;
    }


}
