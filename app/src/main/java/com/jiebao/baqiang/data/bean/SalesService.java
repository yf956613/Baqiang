package com.jiebao.baqiang.data.bean;

import com.alibaba.fastjson.util.ASMClassLoader;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.sql.Date;

/**
 * 封装营业网点数据：来自e3new.gprs_view_tab_营业网点表.sql
 * <p>
 * 包括：网点编号, 网点名称, 所属网点, 所属财务中心, 启用标识, 允许到付, 城市, 省份, 更新状态, 更新时间, 类型, 县
 * <p>
 * ('71701', '宜昌', '武汉中心', '武汉中心', '1', 1, '宜昌市', '湖北省', 2, to_date
 * ('16-05-2016 10:05:28', 'dd-mm-yyyy hh24:mi:ss'), '中心', null);
 */

@Table(name = "salesservice")
public class SalesService {

    @Column(name = "id", isId = true, autoGen = true, property = "NOT NULL")
    private int id;

    @Column(name = "serviceNumber", property = "NOT NULL")
    private String 网点编号;

    @Column(name = "serviceName", property = "NOT NULL")
    private String 网点名称;

    @Column(name = "belongService")
    private String 所属网点;

    @Column(name = "belongFinance")
    private String 所属财务中心;

    @Column(name = "isUsing")
    private String 启用标识;

    @Column(name = "isCollect")
    private int 允许到付;

    @Column(name = "city")
    private String 城市;

    @Column(name = "province")
    private String 省份;

    @Column(name = "updateStatus")
    private String 更新状态;

    @Column(name = "updateTime")
    private Date 更新时间;

    @Column(name = "type")
    private String 类型;

    @Column(name = "belongGoodsCentre")
    private String 所属提交货中心;

    @Column(name = "county")
    private String 县;

    public SalesService() {
    }

    public SalesService(String 网点编号, String 网点名称, String 所属网点, String 所属财务中心, String 启用标识, int
            允许到付, String 城市, String 省份, String 更新状态, Date 更新时间, String 类型, String 所属提交货中心,
                        String 县) {
        this.网点编号 = 网点编号;
        this.网点名称 = 网点名称;
        this.所属网点 = 所属网点;
        this.所属财务中心 = 所属财务中心;
        this.启用标识 = 启用标识;
        this.允许到付 = 允许到付;
        this.城市 = 城市;
        this.省份 = 省份;
        this.更新状态 = 更新状态;
        this.更新时间 = 更新时间;
        this.类型 = 类型;
        this.所属提交货中心 = 所属提交货中心;
        this.县 = 县;
    }

    public String get网点编号() {
        return 网点编号;
    }

    public String get网点名称() {
        return 网点名称;
    }

    public String get所属网点() {
        return 所属网点;
    }

    public String get所属财务中心() {
        return 所属财务中心;
    }

    public String get启用标识() {
        return 启用标识;
    }

    public int get允许到付() {
        return 允许到付;
    }

    public String get城市() {
        return 城市;
    }

    public String get省份() {
        return 省份;
    }

    public String get更新状态() {
        return 更新状态;
    }

    public Date get更新时间() {
        return 更新时间;
    }

    public String get类型() {
        return 类型;
    }

    public String get所属提交货中心() {
        return 所属提交货中心;
    }

    public String get县() {
        return 县;
    }

    @Override
    public String toString() {
        return "SalesService{" + "网点编号='" + 网点编号 + '\'' + ", 网点名称='" + 网点名称 + '\'' + ", 所属网点='" +
                所属网点 + '\'' + ", 所属财务中心='" + 所属财务中心 + '\'' + ", 启用标识='" + 启用标识 + '\'' + ", " +
                "允许到付='" + 允许到付 + '\'' + ", 城市='" + 城市 + '\'' + ", 省份='" + 省份 + '\'' + ", 更新状态='"
                + 更新状态 + '\'' + ", 更新时间='" + 更新时间 + '\'' + ", 类型='" + 类型 + '\'' + ", 所属提交货中心='" +
                所属提交货中心 + '\'' + ", 县='" + 县 + '\'' + '}';
    }
}
