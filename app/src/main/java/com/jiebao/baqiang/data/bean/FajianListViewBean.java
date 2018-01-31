package com.jiebao.baqiang.data.bean;

public class FajianListViewBean {
    // 序号
    private int id;
    // 单号
    private String scannerData;
    // 数据是否上传的状态标识（value：已上传；未上传）
    private String status;

    public FajianListViewBean(){}

    public FajianListViewBean(int id, String scannerData, String status) {
        this.id = id;
        this.scannerData = scannerData;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getScannerData() {
        return scannerData;
    }

    public void setScannerData(String scannerData) {
        this.scannerData = scannerData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
