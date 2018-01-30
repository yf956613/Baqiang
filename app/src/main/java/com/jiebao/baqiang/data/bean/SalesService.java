package com.jiebao.baqiang.data.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

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

    @Column(name = "serviceNumber")
    private String 网点编号;

    @Column(name = "serviceName")
    private String 网点名称;

    public SalesService() {
    }

    public SalesService(String 网点编号, String 网点名称) {
        this.网点编号 = 网点编号;
        this.网点名称 = 网点名称;
    }

    public String get网点编号() {
        return 网点编号;
    }

    public String get网点名称() {
        return 网点名称;
    }
}
