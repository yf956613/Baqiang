package com.jiebao.baqiang.global;

/**
 * Created by Administrator on 2018/1/23 0023.
 */

public interface NetworkConstant {
    String HTTP_DOMAIN = "http://";
    String LOGIN_SERVLET = "/JBBaQiang/Login";

    // TODO 数据上传相关
    String UPLOAD_SERVLET = "/JBBaQiang/UploadFile";
    // 上专数据包的用户名字信息
    String PKG_OWER = "pkgOwer";
    // 上专数据包的名字
    String PKG_NAME = "pkgName";
    // 数据包大小
    String PKG_SIZE = "pkgSize";
    // 文件数据校验
    String PGK_CHECKSUM = "pkgCheckSum";
    // 数据包类型：1 为TXT文本
    String PKG_TYPE = "pkgType";
    // 数据包加密方式： 0为不加密
    String PKG_ENC = "pkgEnc";
    // 数据包内容
    String PKG_DATA = "pkgData";


    // TODO 快件类型
    String GOOD_TYPE_SERVLET = "/JBBaQiang/GoodType";

    //TODO 预付款
    String PREPAY_STATE = "/JBBaQiang/PrepayState";
    // TODO 车辆信息
    String VEHICLE_INFO_SERVLET = "/JBBaQiang/CarInfo";

    // TODO 付款方式
    String PAYMENT_TYPE_SERVLET = "/JBBaQiang/PayWay";

    //TODO 留仓原因
    String LiuCang_TYPE ="/JBBaQiang/Storehouse";

    // TODO 公司员工表
    String EMPLOYEE = "/JBBaQiang";

    // TODO 留仓原因
    String KEEP_WAREHOUSE_SERVLET = "/JBBaQiang";

    // TODO 目的地
    String DESTINATION = "/JBBaQiang";

    // TODO 区域表
    String AREA = "/JBBaQiang";

    // TODO 区域派送表
    String AREA_SHIPMENT = "/JBBaQiang";

    // TODO 问题件类型
    String PROBLEM_TYPE = "/JBBaQiang";

    // TODO 请求营业网点编号
    String NEXT_SALES_SERVICE_SERVLET = "/JBBaQiang/SaleInfo";

    // TODO 删除记录表

    // TODO 预付款开账

    // app软件更新
    String APP_UPDATE_INFO = "/JBWeb/ServerInfo";

    // apk下载路径
    String APK_DOWNLOAD_URL = "/JBWeb/DownloadFile";

}
