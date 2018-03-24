package com.jiebao.baqiang.data.bean;

/**
 * Created by Administrator on 2018/3/24 0024.
 */

public class CommonScannerListViewBean {
    private int id;
    private IFileContentBean bean;

    public CommonScannerListViewBean() {
    }

    public CommonScannerListViewBean(int id, IFileContentBean bean) {
        this.id = id;
        this.bean = bean;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public IFileContentBean getScannerBean() {
        return bean;
    }

    public void setScannerBean(IFileContentBean bean) {
        this.bean = bean;
    }
}
