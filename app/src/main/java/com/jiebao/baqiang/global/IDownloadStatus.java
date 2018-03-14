package com.jiebao.baqiang.global;

/**
 * Created by Administrator on 2018/3/14 0014.
 */

public interface IDownloadStatus {

    void downloadFinish();

    void downLoadError(String errorMsg);
}
