package com.jiebao.baqiang.data.bean;

import java.util.List;

/**
 * 网络请求付款方式
 */

public class PaymentTypeList {

    private int payWaysCnt;

    private List<PaymentType> payWayInfo;

    public int getPayWaysCnt() {
        return payWaysCnt;
    }

    public List<PaymentType> getPayWayInfo() {
        return payWayInfo;
    }
}
