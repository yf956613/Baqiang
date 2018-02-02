package com.jiebao.baqiang.data.arrival;

import com.jiebao.baqiang.util.LogUtil;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 封装卸车到件模块中待上传文件信息，扫描信息存入数据库，具体格式参考：卸车.txt
 */

@Table(name = "xcdaojian")
public class UnloadArrivalFileContent {
    private static final String TAG = UnloadArrivalFileContent.class
            .getSimpleName();

    // 22C             20170221235220  45067672999      A800     20170221 0
    //      Z000024
    private static final int LENGTH = 109;
    // 扫描类型编号长度
    private static final int SCAN_TYPE = 2;
    // 上一站点编号长度
    private static final int PREVIOUS_STATION = 14;
    // 扫描日期长度
    private static final int SCAN_DATE = 14;
    // 物品类别长度
    private static final int GOODS_TYPE = 2;
    // 快件类别长度
    private static final int SHIPMENT_TYPE = 1;
    // 运单编号长度
    private static final int SHIPMENT_NUMBER = 16;
    // 扫描员工编号长度
    private static final int SCAN_EMPLOYEE_NUMBER = 9;
    // 操作日期长度
    private static final int OPERATE_DATE = 8;
    // 空格长度
    private static final int BLANK_SPACE_1 = 1;
    // 重量
    private static final int WEIGHT = 7;
    // 空格长度
    private static final int BLANK_SPACE_2 = 2;
    // 车辆识别号长度
    private static final int VEHICLE_ID = 15;
    // 其他空格长度
    private static final int OTHERS = 18;

    // TODO 卸车到件固定的前缀：22
    private static final String TYPE_SUFFIX = "22";

    @Column(name = "id", isId = true, autoGen = true, property = "NOT NULL")
    private int id;

    @Column(name = "扫描类型编号")
    private String mScannerType;

    public String getmScannerType() {
        return mScannerType;
    }

    public void setmScannerType(String mScannerType) {
        this.mScannerType = mScannerType;
    }

    @Column(name = "下一站网点编号")
    private String mPreviousStation;

    public String getNextStation() {
        return mPreviousStation;
    }

    public void setPreviousStation(String previousStation) {
        this.mPreviousStation = previousStation;
    }

    @Column(name = "扫描时间")
    private String mScanDate;

    public String getScanDate() {
        return mScanDate;
    }

    public void setScanDate(String scanDate) {
        this.mScanDate = scanDate;
    }

    @Column(name = "物品类别")
    private String mGoodsType;

    public String getGoodsType() {
        return mGoodsType;
    }

    public void setGoodsType(String goodsType) {
        this.mGoodsType = goodsType;
    }

    @Column(name = "快件类型")
    private String mShipmentType;

    public String getShipmentType() {
        return mShipmentType;
    }

    public void setShipmentType(String shipmentType) {
        this.mShipmentType = shipmentType;
    }

    @Column(name = "运单编号")
    private String mShipmentNumber;

    public String getShipmentNumber() {
        return mShipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.mShipmentNumber = shipmentNumber;
    }

    @Column(name = "扫描员工编号")
    private String mScanEmployeeNumber;

    public String getScanEmployeeNumber() {
        return mScanEmployeeNumber;
    }

    public void setScanEmployeeNumber(String scanEmployeeNumber) {
        this.mScanEmployeeNumber = scanEmployeeNumber;
    }

    @Column(name = "操作日期")
    private String mOperateDate;

    public String getOperateDate() {
        return mOperateDate;
    }

    public void setOperateDate(String operateDate) {
        this.mOperateDate = operateDate;
    }

    @Column(name = "重量")
    private String mWeight;

    public String getWeight() {
        return mWeight;
    }

    public void setWeight(String weight) {
        this.mWeight = weight;
    }

    @Column(name = "车辆识别号")
    private String mVehicleId;

    public String getVehicleId() {
        return mVehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.mVehicleId = vehicleId;
    }

    // 是否上传状态标识
    @Column(name = "是否上传")
    private String mStatus;

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }


    /**
     * 默认构造函数
     */
    public UnloadArrivalFileContent() {

    }

    public UnloadArrivalFileContent(String mPreviousStation, String mScanDate,
                                    String mGoodsType, String mShipmentType,
                                    String mShipmentNumber, String
                                            mScanEmployeeNumber, String
                                            mOperateDate, String mWeight,
                                    String mVehicleId,
                                    String mStatus) {
        this.mScannerType = TYPE_SUFFIX;
        this.mPreviousStation = mPreviousStation;
        this.mScanDate = mScanDate;
        this.mGoodsType = mGoodsType;
        this.mShipmentType = mShipmentType;
        this.mShipmentNumber = mShipmentNumber;
        this.mScanEmployeeNumber = mScanEmployeeNumber;
        this.mOperateDate = mOperateDate;
        this.mWeight = mWeight;
        this.mVehicleId = mVehicleId;
        this.mStatus = mStatus;
    }

    public String getmCurrentValue() {
        return releaseLineContent();
    }

    /**
     * 组装成指定格式的一行数据
     *
     * @return
     */
    private String releaseLineContent() {
        // 添加扫描类型
        StringBuffer stringBuffer = new StringBuffer(TYPE_SUFFIX);

        // 添加上一站网点编号
        stringBuffer.append(this.mPreviousStation);
        stringBuffer.append(countBlankAndAppend(this.mPreviousStation,
                PREVIOUS_STATION));

        // 添加扫描时间
        stringBuffer.append(this.mScanDate);
        stringBuffer.append(countBlankAndAppend(this.mScanDate, SCAN_DATE));

        // 物品类别
        stringBuffer.append(this.mGoodsType);
        stringBuffer.append(countBlankAndAppend(this.mGoodsType, GOODS_TYPE));

        // 快件类型
        stringBuffer.append(this.mShipmentType);

        // 运单编号
        stringBuffer.append(this.mShipmentNumber);
        stringBuffer.append(countBlankAndAppend(this.mShipmentNumber,
                SHIPMENT_NUMBER));

        // 扫描员工编号
        stringBuffer.append(this.mScanEmployeeNumber);
        stringBuffer.append(countBlankAndAppend(this.mScanEmployeeNumber,
                SCAN_EMPLOYEE_NUMBER));

        // 操作日期
        stringBuffer.append(this.mOperateDate);

        // 空格
        stringBuffer.append(appendBlankSpace(BLANK_SPACE_1));

        // 重量
        stringBuffer.append(this.mWeight);
        stringBuffer.append(countBlankAndAppend(this.mWeight,
                WEIGHT));

        stringBuffer.append(appendBlankSpace(BLANK_SPACE_2));

        // 车辆识别号
        stringBuffer.append(this.mVehicleId);
        stringBuffer.append(countBlankAndAppend(this.mVehicleId, VEHICLE_ID));

        // 其他空格
        stringBuffer.append(appendBlankSpace(OTHERS));
        LogUtil.trace(stringBuffer + ";");

        LogUtil.d(TAG, "length of StringBuffer is " + stringBuffer.length());
        return new String(stringBuffer);
    }

    /**
     * 根据指定内容计算需要添加多少个空格内容
     *
     * @param content
     * @param allLength
     * @return
     */
    private StringBuffer countBlankAndAppend(String content, int allLength) {
        StringBuffer sb = new StringBuffer();

        int lengthContent = content.length();
        int spaceNumber = allLength - lengthContent;
        if (spaceNumber > 0) {
            for (int index = 0; index < spaceNumber; index++) {
                // 添加对应的空格
                sb.append(" ");
            }
        }

        return sb;
    }

    /**
     * 返回指定数量的空格
     *
     * @param count
     * @return
     */
    private StringBuffer appendBlankSpace(int count) {
        StringBuffer sb = new StringBuffer();

        for (int index = 0; index < count; index++) {
            sb.append(" ");
        }

        return sb;
    }

    @Override
    public String toString() {
        return "UnloadArrivalFileContent{" +
                "mScannerType='" + mScannerType + '\'' +
                ", mPreviousStation='" + mPreviousStation + '\'' +
                ", mScanDate='" + mScanDate + '\'' +
                ", mGoodsType='" + mGoodsType + '\'' +
                ", mShipmentType='" + mShipmentType + '\'' +
                ", mShipmentNumber='" + mShipmentNumber + '\'' +
                ", mScanEmployeeNumber='" + mScanEmployeeNumber + '\'' +
                ", mOperateDate='" + mOperateDate + '\'' +
                ", mWeight='" + mWeight + '\'' +
                ", mVehicleId='" + mVehicleId + '\'' +
                ", mStatus='" + mStatus + '\'' +
                '}';
    }
}
