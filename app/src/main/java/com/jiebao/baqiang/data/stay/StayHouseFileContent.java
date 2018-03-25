package com.jiebao.baqiang.data.stay;

import com.jiebao.baqiang.data.bean.IFileContentBean;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 货件留仓扫描时生成一行文本，具体参考：货件留仓扫描.txt
 */
@Table(name = "liucangjian")
public class StayHouseFileContent extends IFileContentBean {
    private static final String TAG = StayHouseFileContent.class.getSimpleName();

    private static final int LENGTH = 91;
    // 扫描类型编号长度
    private static final int SCAN_TYPE = 2;
    // 编号长度
    private static final int SERIAL_NUMBER = 14;
    // 扫描日期长度
    private static final int SCAN_DATE = 14;
    // 留仓原因长度
    private static final int STAY_REASON = 2;
    // 快件类别长度
    private static final int SHIPMENT_TYPE = 1;
    // 运单编号长度
    private static final int SHIPMENT_NUMBER = 16;
    // 扫描员工编号长度
    private static final int SCAN_EMPLOYEE_NUMBER = 9;
    // 操作日期长度
    private static final int OPERATE_DATE = 8;
    // 其他空格长度
    private static final int OTHERS = 25;

    // TODO 到件固定的前缀：03
    private static final String TYPE_SUFFIX = "08";
    private static final String SERIAL_NUMBER_VALUE = "LC";

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

    @Column(name = "SerialNumber")
    private String mSerialNumber;

    public String getSerialNumber() {
        return mSerialNumber;
    }

    public void setSerialNumber(String mSerialNumber) {
        this.mSerialNumber = mSerialNumber;
    }

    @Column(name = "ScanDate")
    private Date mScanDate;

    public Date getScanDate() {
        return mScanDate;
    }

    public void setScanDate(Date scanDate) {
        this.mScanDate = scanDate;
    }

    @Column(name = "StayReason")
    private String mStayReason;

    public String getStayReason() {
        return mStayReason;
    }

    public void setStayReason(String stayReason) {
        this.mStayReason = stayReason;
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

    // 是否上传状态标识
    @Column(name = "IsUpload")
    private String mStatus;

    public String getStatus() {
        return mStatus;
    }

    public void setmtatus(String mStatus) {
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
    public StayHouseFileContent() {

    }

    public StayHouseFileContent(Date mScanDate, String mStayReason, String mShipmentType, String
            mShipmentNumber, String mScanEmployeeNumber, String mOperateDate, String mStatus,
                                String mIsUsed) {
        this.mScannerType = TYPE_SUFFIX;
        this.mSerialNumber = SERIAL_NUMBER_VALUE;
        this.mScanDate = mScanDate;
        this.mStayReason = mStayReason;
        this.mShipmentType = mShipmentType;
        this.mShipmentNumber = mShipmentNumber;
        this.mScanEmployeeNumber = mScanEmployeeNumber;
        this.mOperateDate = mOperateDate;
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
        stringBuffer.append(this.mSerialNumber);
        stringBuffer.append(countBlankAndAppend(this.mSerialNumber, SERIAL_NUMBER));

        // 添加扫描时间
        stringBuffer.append(new SimpleDateFormat("yyyyMMddHHmmss").format(mScanDate));

        // 留仓原因
        stringBuffer.append(this.mStayReason);
        stringBuffer.append(countBlankAndAppend(this.mStayReason, STAY_REASON));

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
        return "StayHouseFileContent{" + "mScannerType='" + mScannerType + '\'' + ", " +
                "mSerialNumber='" + mSerialNumber + '\'' + ", mScanDate='" + mScanDate + '\'' +
                ", mStayReason='" + mStayReason + '\'' + ", mShipmentType='" + mShipmentType +
                '\'' + ", mShipmentNumber='" + mShipmentNumber + '\'' + ", mScanEmployeeNumber='"
                + mScanEmployeeNumber + '\'' + ", mOperateDate='" + mOperateDate + '\'' + ", " +
                "mStatus='" + mStatus + '\'' + ", mIsUsed='" + mIsUsed + '\'' + '}';
    }
}
