package com.jiebao.baqiang.global;

/**
 * Created by Administrator on 2018/3/14 0014.
 */

public interface IDownloadStatus {
    void startDownload(int id);
    void downloadFinish(int id);
    void updateDataFinish(int id);
    void downLoadError(int id, String errorMsg);
}
