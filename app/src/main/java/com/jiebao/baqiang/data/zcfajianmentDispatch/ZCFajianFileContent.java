package com.jiebao.baqiang.data.zcfajianmentDispatch;


import com.jiebao.baqiang.data.bean.IFileContentBean;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 封装 装车发件 功能中，待存储文件中每一个内容，文件内容编码为：gb2312
 * <p>
 * 同时，作为SQLite数据表存储的JavaBean
 */

@Table(name = "zcfajian")
public class ZCFajianFileContent implements IFileContentBean {
    private static final String TAG = ZCFajianFileContent.class.getSimpleName();

    // 0259200         20170221235917  2880273772877    5955513  20170221 0
    // 每一行包括91个字符，其中中文2个字符，每个字段长度不够时向右补空格
    private static final int LENGTH = 91;
    // 扫描类型编号
    private static final int SCAN_TYPE = 2;
    // 下一站点编号
    private static final int NEXT_STATION = 14;
    // 扫描日期
    private static final int SCAN_DATE = 14;
    // 物品类别
    private static final int GOODS_TYPE = 2;
    // 快件类别
    private static final int SHIPMENT_TYPE = 1;
    // 运单编号
    private static final int SHIPMENT_NUMBER = 16;
    // 扫描员工编号
    private static final int SCAN_EMPLOYEE_NUMBER = 9;
    // 操作日期
    private static final int OPERATE_DATE = 8;
    // 空格
    private static final int BLANK_SPACE = 1;
    // 重量
    private static final int WEIGHT = 7;
    // 空格
    private static final int BLANK_SPACE1 = 2;
    //车辆识别码
    private static final int IDENTFY_NUMBER = 15;
    // 其他空格
    private static final int OTHERS = 18;

    private static final String TYPE_SUFFIX = "23";

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

    public String getmScannerType() {
        return mScannerType;
    }

    public void setmScannerType(String mScannerType) {
        this.mScannerType = mScannerType;
    }

    @Column(name = "NextStationID")
    private String mNextStation;

    public String getNextStation() {
        return mNextStation;
    }

    public void setNextStation(String nextStation) {
        this.mNextStation = nextStation;
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

    @Column(name = "VehicleID")
    private String mVehicleID;

    public String getmVehicleID() {
        return mVehicleID;
    }

    public void setmVehicleID(String mVehicleID) {
        this.mVehicleID = mVehicleID;
    }

    @Column(name = "IsUpload")
    private String mStatus;

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    @Column(name = "IsUsed")
    private String mIsUsed;

    public String getmIsUsed() {
        return mIsUsed;
    }

    public void setmIsUsed(String mIsUsed) {
        this.mIsUsed = mIsUsed;
    }

    public ZCFajianFileContent() {

    }

    public ZCFajianFileContent(String mScannerType, String mNextStation, Date mScanDate, String
            mGoodsType, String mShipmentType, String mShipmentNumber, String mScanEmployeeNumber,
                               String mOperateDate, String mWeight, String mVehicleID, String
                                       mStatus, String mIsUsed) {
        this.mScannerType = mScannerType;
        this.mNextStation = mNextStation;
        this.mScanDate = mScanDate;
        this.mGoodsType = mGoodsType;
        this.mShipmentType = mShipmentType;
        this.mShipmentNumber = mShipmentNumber;
        this.mScanEmployeeNumber = mScanEmployeeNumber;
        this.mOperateDate = mOperateDate;
        this.mWeight = mWeight;
        this.mVehicleID = mVehicleID;
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

        // 添加网点编号
        stringBuffer.append(this.mNextStation);
        stringBuffer.append(countBlankAndAppend(this.mNextStation, NEXT_STATION));

        // 添加扫描时间
        stringBuffer.append(new SimpleDateFormat("yyyyMMddHHmmss").format(mScanDate));

        // 物品类别
        stringBuffer.append(this.mGoodsType);
        stringBuffer.append(countBlankAndAppend(this.mGoodsType, GOODS_TYPE));

        // 快件类型
        stringBuffer.append(this.mShipmentType);

        // 运单编号
        stringBuffer.append(this.mShipmentNumber);
        stringBuffer.append(countBlankAndAppend(this.mShipmentNumber, SHIPMENT_NUMBER));

        // 扫描员工编号
        stringBuffer.append(this.mScanEmployeeNumber);
        stringBuffer.append(countBlankAndAppend(this.mScanEmployeeNumber, SCAN_EMPLOYEE_NUMBER));

        // 操作日期
        stringBuffer.append(this.mOperateDate);

        // 空格
        stringBuffer.append(appendBlankSpace(BLANK_SPACE));

        // 重量
        stringBuffer.append(this.mWeight);
        stringBuffer.append(countBlankAndAppend(this.mWeight, WEIGHT));
        // 空格
        stringBuffer.append(appendBlankSpace(BLANK_SPACE1));
        //车辆识别码
        stringBuffer.append(this.mVehicleID);
        stringBuffer.append(countBlankAndAppend(this.mVehicleID, IDENTFY_NUMBER));

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
        return "ZCFajianFileContent{" + "mScannerType='" + mScannerType + '\'' + ", " +
                "mNextStation='" + mNextStation + '\'' + ", mScanDate='" + mScanDate + '\'' + ", " +
                "" + "" + "" + "" + "" + "" + "" + "" + "" + "mGoodsType='" + mGoodsType + '\'' +
                ", " + "mShipmentType='" + mShipmentType + '\'' + ", mShipmentNumber='" +
                mShipmentNumber + '\'' + ", " + "mScanEmployeeNumber='" + mScanEmployeeNumber +
                '\'' + ", " + "mOperateDate='" + mOperateDate + '\'' + ", " + "mWeight='" +
                mWeight + '\'' + ", mVehicleID='" + mVehicleID + '\'' + ", mStatus='" + mStatus +
                '\'' + ", " + "mIsUsed='" + mIsUsed + '\'' + '}';
    }
}
