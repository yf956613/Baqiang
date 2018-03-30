package com.jiebao.baqiang.data.bean;

import org.xutils.common.Callback;

/**
 * 待上传文件状态回调接口，其实例放置在待上传文件中，将网络状态回调给CommonDbHelperToUploadFile
 */

public interface ICommonUpdateFileCallBack {
    boolean uploadSuccess(String s);

    boolean uploadError(Throwable throwable, boolean b);

    boolean uploadCancel(Callback.CancelledException e);

    boolean uploadFinish();
}
