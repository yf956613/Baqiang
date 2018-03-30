package com.jiebao.baqiang.data.bean;

/**
 * Activity和CommonDbHelperToUploadFile之间接口，将DB状态反馈给Activity
 * ；实例存在于CommonDbHelperToUploadFile中
 */

public interface IDbHelperToUploadFileCallback {

    boolean onSuccess(String s);

    boolean onError(Throwable throwable, boolean b);

    boolean onFinish();
}
