package com.jiebao.baqiang.data.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 封装快递类型，数据来自：e3new.gprs_view_tab_快件类型表
 * <p>
 * 数据内容包括：类型编号, 类型名称
 * <p>
 * ('8', 'EMS')
 */

@Table(name = "shipmenttype")
public class ShipmentType {
    @Column(name = "id", isId = true, autoGen = true, property = "NOT NULL")
    private int id;

    @Column(name = "number")
    private String 类型编号;

    @Column(name = "name")
    private String 类型名称;

    public String get类型编号() {
        return 类型编号;
    }

    public String get类型名称() {
        return 类型名称;
    }

    public ShipmentType() {
    }

    public ShipmentType(String 类型编号, String 类型名称) {
        this.类型编号 = 类型编号;
        this.类型名称 = 类型名称;
    }

}
