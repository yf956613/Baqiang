package com.jiebao.baqiang.data.bean;

/**
 * Created by open on 2018/1/23.
 */

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 留仓原因
 */
@Table(name = "liucang")
public class LiucangBean {
    @Column(name = "id", isId = true, autoGen = true, property = "NOT NULL")
    private int id;

    @Column(name = "编号")
    private String 编号;

    @Column(name = "名称")
    private String 名称;

    @Column(name = "备注")
    private String 备注;

    public String get编号() {
        return 编号;
    }

    public void set编号(String 编号) {
        this.编号 = 编号;
    }

    public String get名称() {
        return 名称;
    }

    public void set名称(String 名称) {
        this.名称 = 名称;
    }

    public String get备注() {
        return 备注;
    }

    public void set备注(String 备注) {
        this.备注 = 备注;
    }

    public LiucangBean(String number, String NAME, String REMARKS) {
        this.编号 = number;
        this.名称 = NAME;
        this.备注 = REMARKS;
    }

    public LiucangBean() {
    }
}
