package com.jiebao.baqiang.data.bean;

/**
 * Created by Administrator on 2018/3/24 0024.
 */

public interface IDbHelperToUploadFileCallback {

    boolean onSuccess(String s);

    boolean onError(Throwable throwable, boolean b);

    boolean onFinish();
}
