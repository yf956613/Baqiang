package com.jiebao.baqiang.data.dispatch;

/**
 * 监听器，接收下述状态改变时，修改待写入文本的内容字段
 */

public interface IContentChanged {

    // 下一网点编号改变
    void nextStationChanged(String nextStation);

    // 扫描日期改变
    void scanDateChanged(String scanDate);

    // 快件类型改变
    void shipmentTypeChanged(String shipmentType);

    // 运单编号改变
    void shipmentNumberChanged(String shipmentNumber);

    // 扫描员工编号改变
    void scanEmployeeChanged(String scanEmployee);

    // 操作日期改变
    void operateDateChanged(String operateDate);
}
