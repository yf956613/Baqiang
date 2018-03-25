package com.jiebao.baqiang.data.bean;

import com.jiebao.baqiang.data.arrival.CargoArrivalFileContent;
import com.jiebao.baqiang.data.arrival.UnloadArrivalFileContent;
import com.jiebao.baqiang.data.dispatch.ShipmentFileContent;
import com.jiebao.baqiang.data.stay.StayHouseFileContent;
import com.jiebao.baqiang.data.updateData.UpdateInterface;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.util.TextStringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class FileContentHelper {

    /**
     * 装车发件
     *
     * @return
     */
    public static ZCFajianFileContent getZCFajianFileContent() {
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

        return new ZCFajianFileContent(nextStation, scanDate, goodsType, shipmentType,
                shipmentNumber, scanEmployeeNumber, operateDate, weight, identify, status, isUsed);
    }

    /**
     * 卸车到件
     *
     * @return
     */
    public static UnloadArrivalFileContent getUnloadArrivalFileContent() {
        // 上一站网点编号
        String previousStation = String.valueOf("59406");
        // 扫描时间
        Date scanDate = new Date();
        // 物品类型
        String goodsType = "";
        // 快件类型
        String shipmentType = "4";
        // 运单编号
        String shipmentNumber = "";
        // 扫描员工编号
        String scanEmployeeNumber = UpdateInterface.userName;
        // 操作日期
        String operateDate = new SimpleDateFormat("yyyyMMdd").format(scanDate);
        // 重量
        String weight = "0.0";
        // 车辆识别号
        String vehicleID = "";
        // 是否上传状态
        String status = "Unload";
        // 是否可用
        String isUsed = "Used";

        return new UnloadArrivalFileContent(previousStation, scanDate, goodsType, shipmentType,
                shipmentNumber, scanEmployeeNumber, operateDate, weight, vehicleID, status, isUsed);
    }

    /**
     * 到件
     *
     * @return
     */
    public static CargoArrivalFileContent getCargoArrivalFileContent() {
        // 上一站点编号  模拟
        String previousStation = String.valueOf("59406");
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
        // 操作日期
        String operateDate = new SimpleDateFormat("yyyyMMdd").format(scanDate);
        // 重量
        String weight = "0.0";
        // 是否上传状态
        String status = "Unload";
        // 是否可用
        String isUsed = "Used";

        return new CargoArrivalFileContent(previousStation, scanDate, goodsType, shipmentType,
                shipmentNumber, scanEmployeeNumber, operateDate, weight, status, isUsed);
    }

    /**
     * 发件
     *
     * @return
     */
    public static ShipmentFileContent getShipmentFileContent() {
        // 首次创建ShipmentFileContent实体时，内容为虚构，并不写入文本中
        String nextStation = String.valueOf("59406");

        // 扫描时间
        Date scanDate = new Date();
        // 物品类型
        String goodsType = "";
        // 快件类型
        String shipmentType = String.valueOf("2");
        // 运单编号
        String shipmentNumber = "";
        // 扫描员工编号
        String scanEmployeeNumber = UpdateInterface.userName;
        // 操作日期
        String operateDate = new SimpleDateFormat("yyyyMMdd").format(scanDate);
        // 重量
        String weight = "0.0";
        // 是否上传状态
        String status = "Unload";
        // 是否可用
        String isUsed = "Used";

        return new ShipmentFileContent(nextStation, scanDate, goodsType, shipmentType,
                shipmentNumber, scanEmployeeNumber, operateDate, weight, status, isUsed);
    }

    /**
     * 留仓件
     *
     * @return
     */
    public static StayHouseFileContent getStayHouseFileContent() {
        // 扫描时间
        Date scanDate = new Date();
        // 留仓原因
        String stayHouseReason = "";
        // 快件类型
        String shipmentType = "";
        // 运单编号
        String shipmentNumber = "";
        // 扫描员工编号
        String scanEmployeeNumber = UpdateInterface.userName;
        // 操作日期
        String operateDate = new SimpleDateFormat("yyyyMMdd").format(scanDate);
        // 是否上传状态
        String status = "Unload";
        // 是否可用
        String isUsed = "Used";

        return new StayHouseFileContent(scanDate, stayHouseReason, shipmentType, shipmentNumber,
                scanEmployeeNumber, operateDate, status, isUsed);
    }
}
