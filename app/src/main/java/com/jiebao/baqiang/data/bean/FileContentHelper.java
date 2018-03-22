package com.jiebao.baqiang.data.bean;

import com.jiebao.baqiang.data.updateData.UpdateInterface;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.util.TextStringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class FileContentHelper {

    public static ZCFajianFileContent getZCFajianFileContent() {
        String scanType = "23";
        // 下一站网点编码
        String nextStation = "";
        // 扫描时间
        Date scanDate = new Date();
        // 物品类型
        String goodsType = "";
        // 快件类型
        String shipmentType = "";
        // 运单编号
        String shipmentNumber = "";
        // 扫描员工编号
        String scanEmployeeNumber = UpdateInterface.userName;
        // 操作日期 <-- 扫码时间（必须相同）
        String operateDate = new SimpleDateFormat("yyyyMMdd").format(scanDate);
        // 重量
        String weight = "0.0";
        //车辆识别码
        String identify = "";
        // 是否上传状态
        String status = "Unload";
        // 是否可用
        String isUsed = "Used";

        return new ZCFajianFileContent(scanType, nextStation, scanDate, goodsType, shipmentType,
                shipmentNumber, scanEmployeeNumber, operateDate, weight, identify, status, isUsed);
    }
}
