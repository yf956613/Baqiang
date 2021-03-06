package com.arthur.appupdate;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 封装到件模块中待上传文件信息，扫描信息存入数据库，具体格式参考：到件.txt
 */

@Table(name = "daojian")
public class CargoArrivalFileContent {
    private static final String TAG = CargoArrivalFileContent.class.getSimpleName();

    private static final int LENGTH = 91;
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
    // 其他空格长度
    private static final int OTHERS = 17;

    // TODO 到件固定的前缀：03
    private static final String TYPE_SUFFIX = "03";

    @Column(name = "id", isId = true, autoGen = true, property = "NOT NULL")
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "ScannerType")
    private String mScannerType;

    public String getScannerType() {
        return mScannerType;
    }

    public void setScannerType(String mScannerType) {
        this.mScannerType = mScannerType;
    }

    @Column(name = "PreviousStationID")
    private String mPreviousStation;

    public String getPreviousStation() {
        return mPreviousStation;
    }

    public void setPreviousStation(String previousStation) {
        this.mPreviousStation = previousStation;
    }

    @Column(name = "ScanDate")
    private Date mScanDate;

    public Date getScanDate() {
        return mScanDate;
    }

    public void setScanDate(Date scanDate) {
        this.mScanDate = scanDate;
    }

    @Column(name = "GoodsType")
    private String mGoodsType;

    public String getGoodsType() {
        return mGoodsType;
    }

    public void setGoodsType(String goodsType) {
        this.mGoodsType = goodsType;
    }

    @Column(name = "ShipmentType")
    private String mShipmentType;

    public String getShipmentType() {
        return mShipmentType;
    }

    public void setShipmentType(String shipmentType) {
        this.mShipmentType = shipmentType;
    }

    @Column(name = "ShipmentID")
    private String mShipmentNumber;

    public String getShipmentNumber() {
        return mShipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.mShipmentNumber = shipmentNumber;
    }

    @Column(name = "ScanEmployeeNumber")
    private String mScanEmployeeNumber;

    public String getScanEmployeeNumber() {
        return mScanEmployeeNumber;
    }

    public void setScanEmployeeNumber(String scanEmployeeNumber) {
        this.mScanEmployeeNumber = scanEmployeeNumber;
    }

    @Column(name = "OperateDate")
    private String mOperateDate;

    public String getOperateDate() {
        return mOperateDate;
    }

    public void setOperateDate(String operateDate) {
        this.mOperateDate = operateDate;
    }

    @Column(name = "Weight")
    private String mWeight;

    public String getWeight() {
        return mWeight;
    }

    public void setWeight(String weight) {
        this.mWeight = weight;
    }

    // 是否上传状态标识
    @Column(name = "IsUpload")
    private String mStatus;

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    @Column(name = "IsUsed")
    private String mIsUsed;

    public String getIsUsed() {
        return mIsUsed;
    }

    public void setIsUsed(String mIsUsed) {
        this.mIsUsed = mIsUsed;
    }

    /**
     * 默认构造函数
     */
    public CargoArrivalFileContent() {

    }

    public CargoArrivalFileContent(String mPreviousStation, Date mScanDate, String mGoodsType,
                                   String mShipmentType, String mShipmentNumber, String
                                           mScanEmployeeNumber, String mOperateDate, String
                                           mWeight, String mStatus, String mIsUsed) {
        this.mScannerType = TYPE_SUFFIX;
        this.mPreviousStation = mPreviousStation;
        this.mScanDate = mScanDate;
        this.mGoodsType = mGoodsType;
        this.mShipmentType = mShipmentType;
        this.mShipmentNumber = mShipmentNumber;
        this.mScanEmployeeNumber = mScanEmployeeNumber;
        this.mOperateDate = mOperateDate;
        this.mWeight = mWeight;
        this.mStatus = mStatus;
        this.mIsUsed = mIsUsed;
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
        stringBuffer.append(countBlankAndAppend(this.mPreviousStation, PREVIOUS_STATION));

        // 添加扫描时间
        stringBuffer.append(new SimpleDateFormat("yyyyMMddHHmmss").format(mScanDate));

        // 物品类别
        stringBuffer.append(this.mGoodsType);
        stringBuffer.append(countBlankAndAppend(this.mGoodsType, GOODS_TYPE));

        // 快件类型
        stringBuffer.append(this.mShipmentType);
        stringBuffer.append(countBlankAndAppend(this.mShipmentType, SHIPMENT_TYPE));

        // 运单编号
        stringBuffer.append(this.mShipmentNumber);
        stringBuffer.append(countBlankAndAppend(this.mShipmentNumber, SHIPMENT_NUMBER));

        // 扫描员工编号
        stringBuffer.append(this.mScanEmployeeNumber);
        stringBuffer.append(countBlankAndAppend(this.mScanEmployeeNumber, SCAN_EMPLOYEE_NUMBER));

        // 操作日期
        stringBuffer.append(this.mOperateDate);

        // 空格
        stringBuffer.append(appendBlankSpace(BLANK_SPACE_1));

        // 重量
        stringBuffer.append(this.mWeight);
        stringBuffer.append(countBlankAndAppend(this.mWeight, WEIGHT));

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
        return "CargoArrivalFileContent{" + "mScannerType='" + mScannerType + '\'' + ", " +
                "mPreviousStation='" + mPreviousStation + '\'' + ", " + "mScanDate='" + mScanDate
                + '\'' + ", mGoodsType='" + mGoodsType + '\'' + ", " + "mShipmentType='" +
                mShipmentType + '\'' + ", mShipmentNumber='" + mShipmentNumber + '\'' + ", " +
                "mScanEmployeeNumber='" + mScanEmployeeNumber + '\'' + ", " + "mOperateDate='" +
                mOperateDate + '\'' + ", mWeight='" + mWeight + '\'' + ", " + "mStatus='" +
                mStatus + '\'' + ", mIsUsed='" + mIsUsed + '\'' + '}';
    }
}
