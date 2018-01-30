package com.jiebao.baqiang.data.bean;

import java.util.List;

/**
 * 请求营业网点数据，必须和服务器反馈的JSon文件格式对应
 */

public class SalesServiceList {

    // 营业网点统计个数，类型可以由String-->int
    private int salesInfo;

    // 营业网点具体信息集合
    private List<SalesService> saleInfo;

    public int getCount() {
        return salesInfo;
    }

    public List<SalesService> getSalesServiceList() {
        return saleInfo;
    }

}
