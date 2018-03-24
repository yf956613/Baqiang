package com.jiebao.baqiang.data.bean;

import org.xutils.common.Callback;

/**
 * Created by Administrator on 2018/3/23 0023.
 */

public interface ICommonUpdateFileCallBack {
    boolean uploadSuccess(String s);

    boolean uploadError(Throwable throwable, boolean b);

    boolean uploadCancel(Callback.CancelledException e);

    boolean uploadFinish();
}
