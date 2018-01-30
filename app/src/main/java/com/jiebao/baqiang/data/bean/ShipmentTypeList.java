package com.jiebao.baqiang.data.bean;

import java.util.List;

/**
 * 服务器请求快递类型返回数据
 */

public class ShipmentTypeList {

    private int goodTypesCnt;
    private List<ShipmentType> goodTypeInfo;

    public int getGoodTypesCnt() {
        return goodTypesCnt;
    }

    public List<ShipmentType> getGoodTypeInfo() {
        return goodTypeInfo;
    }
}
