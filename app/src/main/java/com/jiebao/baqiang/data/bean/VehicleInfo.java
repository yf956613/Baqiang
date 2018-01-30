package com.jiebao.baqiang.data.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 封装车辆信息
 */

@Table(name = "vehicleinfo")
public class VehicleInfo {
    @Column(name = "id", isId = true, autoGen = true, property = "NOT NULL")
    private int id;

    @Column(name = "number")
    private String 车牌号;
    @Column(name = "identify")
    private String 车辆识别号;

    public VehicleInfo() {
    }

    public VehicleInfo(String number, String identify) {
        this.车牌号 = number;
        this.车辆识别号 = identify;
    }

    public String get车牌号() {
        return 车牌号;
    }

    public String get车辆识别号() {
        return 车辆识别号;
    }
}
