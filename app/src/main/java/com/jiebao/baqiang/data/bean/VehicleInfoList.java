package com.jiebao.baqiang.data.bean;

import java.util.List;

/**
 * 请求服务器车辆信息
 */

public class VehicleInfoList {
    private int carsCnt;
    private List<VehicleInfo> carInfo;

    public int getVehicleInfoCnt() {
        return carsCnt;
    }

    public List<VehicleInfo> getVehicleInfo() {
        return carInfo;
    }

}
