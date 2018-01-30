package com.jiebao.baqiang.data.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by yaya on 2018/1/26.
 */

@Table(name = "paymenttype")
public class PaymentType {
    @Column(name = "id", isId = true, autoGen = true, property = "NOT NULL")
    private int id;

    @Column(name = "number")
    private String 付款方式编号;

    @Column(name = "name")
    private String 付款方式名称;

    public PaymentType() {
    }

    public PaymentType(String number, String name) {
        this.付款方式编号 = number;
        this.付款方式名称 = name;
    }

    public String get付款方式编号() {
        return 付款方式编号;
    }

    public String get付款方式名称() {
        return 付款方式名称;
    }
}
