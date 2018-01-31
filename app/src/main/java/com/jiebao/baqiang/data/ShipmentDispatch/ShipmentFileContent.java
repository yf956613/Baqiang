package com.jiebao.baqiang.data.ShipmentDispatch;


import com.jiebao.baqiang.util.LogUtil;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 封装发件功能中，待存储文件中每一个内容，文件内容编码为：gb2312
 * <p>
 * 同时，作为SQLite数据表存储的JavaBean
 */

@Table(name = "fajian")
public class ShipmentFileContent implements IContentChanged {
    private static final String TAG = ShipmentFileContent.class.getSimpleName();

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
    // 其他空格
    private static final int OTHERS = 17;

    private static final String TYPE_SUFFIX = "02";

    @Column(name = "mNextStation")
    private String mNextStation;

    @Column(name = "mScanDate")
    private String mScanDate;

    @Column(name = "mGoodsType")
    private String mGoodsType;

    @Column(name = "mShipmentType")
    private String mShipmentType;

    @Column(name = "mShipmentNumber")
    private String mShipmentNumber;

    @Column(name = "mScanEmployeeNumber")
    private String mScanEmployeeNumber;

    @Column(name = "mOperateDate")
    private String mOperateDate;

    @Column(name = "mWeight")
    private String mWeight;

    // 是否上传状态标识
    private String mStatus;

    // 待写入文件的一行数据
    private String mCurrentValue;

    public ShipmentFileContent() {

    }

    public ShipmentFileContent(String mNextStation, String mScanDate, String
            mGoodsType, String mShipmentType, String mShipmentNumber, String
                                       mScanEmployeeNumber, String
                                       mOperateDate, String mWeight, String
                                       mStatus) {
        this.mNextStation = mNextStation;
        this.mScanDate = mScanDate;
        this.mGoodsType = mGoodsType;
        this.mShipmentType = mShipmentType;
        this.mShipmentNumber = mShipmentNumber;
        this.mScanEmployeeNumber = mScanEmployeeNumber;
        this.mOperateDate = mOperateDate;
        this.mWeight = mWeight;
        this.mStatus = mStatus;

        this.mCurrentValue = releaseLineContent();
    }

    public String getmCurrentValue() {
        return mCurrentValue;
    }

    public String getNextStation() {
        return mNextStation;
    }

    public void setNextStation(String nextStation) {
        this.mNextStation = nextStation;
    }

    public String getScanDate() {
        return mScanDate;
    }

    public void setScanDate(String scanDate) {
        this.mScanDate = scanDate;
    }

    public String getGoodsType() {
        return mGoodsType;
    }

    public void setGoodsType(String goodsType) {
        this.mGoodsType = goodsType;
    }

    public String getShipmentType() {
        return mShipmentType;
    }

    public void setShipmentType(String shipmentType) {
        this.mShipmentType = shipmentType;
    }

    public String getShipmentNumber() {
        return mShipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.mShipmentNumber = shipmentNumber;
    }

    public String getScanEmployeeNumber() {
        return mScanEmployeeNumber;
    }

    public void setScanEmployeeNumber(String scanEmployeeNumber) {
        this.mScanEmployeeNumber = scanEmployeeNumber;
    }

    public String getOperateDate() {
        return mOperateDate;
    }

    public void setOperateDate(String operateDate) {
        this.mOperateDate = operateDate;
    }

    public String getWeight() {
        return mWeight;
    }

    public void setWeight(String weight) {
        this.mWeight = weight;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
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
        stringBuffer.append(countBlankAndAppend(this.mNextStation,
                NEXT_STATION));

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
        stringBuffer.append(appendBlankSpace(BLANK_SPACE));

        // 重量
        stringBuffer.append(this.mWeight);
        stringBuffer.append(countBlankAndAppend(this.mWeight, WEIGHT));

        // 其他空格
        stringBuffer.append(appendBlankSpace(OTHERS));

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
    public void nextStationChanged(String nextStation) {
        String currentNextStation = this.getNextStation();
        // 根据mCurrentValue中的currentNextStation截取字段
        String[] arr = mCurrentValue.split(currentNextStation);

        this.setNextStation(nextStation);
        mCurrentValue = new String(arr[0] + nextStation + arr[1]);
    }

    @Override
    public void scanDateChanged(String scanDate) {
        String currentScanData = this.getScanDate();
        LogUtil.d(TAG, "currentScanData:" + currentScanData);

        // 根据mCurrentValue中的currentScanData截取字段
        String[] arr = mCurrentValue.split(currentScanData);

        this.setScanDate(scanDate);
        mCurrentValue = new String(arr[0] + scanDate + arr[1]);
    }

    @Override
    public void shipmentTypeChanged(String shipmentType) {
        String currentShipmentType = this.getShipmentType();
        // 根据mCurrentValue中的currentShipmentType截取字段
        String[] arr = mCurrentValue.split(currentShipmentType);

        this.setShipmentType(shipmentType);
        mCurrentValue = new String(arr[0] + shipmentType + arr[1]);
    }

    @Override
    public void shipmentNumberChanged(String shipmentNumber) {
        String currentShipmentNumber = this.getShipmentNumber();
        // 根据mCurrentValue中的currentShipmentNumber截取字段
        String[] arr = mCurrentValue.split(currentShipmentNumber);

        this.setShipmentNumber(shipmentNumber);
        mCurrentValue = new String(arr[0] + shipmentNumber + arr[1]);
    }

    @Override
    public void scanEmployeeChanged(String scanEmployee) {
        String currentScanEmployee = this.getScanEmployeeNumber();
        // 根据mCurrentValue中的currentScanEmployee截取字段
        String[] arr = mCurrentValue.split(currentScanEmployee);

        this.setScanEmployeeNumber(scanEmployee);
        mCurrentValue = new String(arr[0] + scanEmployee + arr[1]);
    }

    @Override
    public void operateDateChanged(String operateDate) {
        String currentOperateDate = this.getOperateDate();
        // 根据mCurrentValue中的currentOperateDate截取字段
        String[] arr = mCurrentValue.split(currentOperateDate);

        this.setOperateDate(operateDate);
        mCurrentValue = new String(arr[0] + operateDate + arr[1]);
    }

    @Override
    public String toString() {
        return "ShipmentFileContent{" + "mNextStation='" + mNextStation +
                '\'' + ", mScanDate='"
                + mScanDate + '\'' + ", mGoodsType='" + mGoodsType + '\'' +
                ", mShipmentType='" +
                mShipmentType + '\'' + ", mShipmentNumber='" +
                mShipmentNumber + '\'' + ", " +
                "mScanEmployeeNumber='" + mScanEmployeeNumber + '\'' + ", " +
                "mOperateDate='" +
                mOperateDate + '\'' + ", mWeight='" + mWeight + '\'' + ", " +
                "mCurrentValue='" +
                mCurrentValue + '\'' + '}';
    }
}
