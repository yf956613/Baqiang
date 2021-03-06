package com.jiebao.baqiang.global;

public class Constant {
    // 调试阶段开关
    public final static boolean DEBUG = false;

    // 连续扫描延时，单位：ms
    public final static int TIME_SCAN_DELAY = 5000;

    // 扫描按键KeyCode值
    public final static int SCAN_KEY_CODE = 139;

    // F1按键键值
    public final static int F1_KEY_CODE = 131;
    // F2按键键值
    public final static int F2_KEY_CODE = 132;
    // F3按键键值
    public final static int F3_KEY_CODE = 133;

    public final static int DEVICE_VIBRATE_TIME = 500;

    // 7天 14天 --> 1209600000
    public final static long DOUBLE_SEVEN_TIME_DATE = 1209600000L;

    public final static int TEST_ADD_RECORDS_NUMBER = 500;

    public final static String CLIENT_VERSION = "1";

    //public static final String UPDATE_URL_PREFIX =
    // "http://120.76.79.124:8090/upgrade/";
    public static final String UPDATE_URL_PREFIX = "http://www" + "" + "" +
            ".kjb1688.com:8090/upgrade/";
    public static final String UPDATE_VERSION_URL = UPDATE_URL_PREFIX +
            "external/checkupdate.htm";


    public final static String APP_CODE = "stockbao_android";
    //"stockbao_android";
    public final static String APP_NAME = "stockbao";
    /**
     * DB目录.
     */
    public final static String DB_DIR = "db";

    public final static String IMPORT_DIR = "导入文件";
    public final static String EXPORT_DIR = "导出文件";
    public final static String DOWNLOAD_DIR = "tmp";
    public final static String ADMIN = "admin";
    public final static String CONFIG = "config.properties";

    public final static String TB_MATERIAL = "T_BD_Material";
    public final static String TB_WAREHOUSE = "T_BD_Warehouse";
    public final static String TB_SCMCHECK = "T_IM_SCMCHECK";
    public final static String TB_SCMCHECKENTRY = "T_IM_SCMCHECKENTRY";
    public final static String TB_PURINWAREHSBILL = "T_Im_Purinwarehsbill";
    public final static String TB_PURINWAREHSENTRY = "T_Im_Purinwarehsentry";
    public final static String TB_SALEISSUEBILL = "T_Im_Saleissuebill";
    public final static String TB_SALEISSUEENTRY = "T_Im_Saleissueentry";
    public final static String TB_PARAMDISPLAY = "T_BD_ParamDisplay";
    public final static String TB_PARAMEXPORT = "T_BD_ParamExport";
    public final static String TB_PARAMIMPORT = "T_BD_ParamImport";
    public final static String TB_BARCODERULE = "T_BarCodeRule";
    public final static String TB_PARAMETER = "T_BD_Parameter";
    public final static String TB_USERLOG = "T_BD_UserLog";
    public final static String TB_INOUTPARAM = "T_BD_InOutParam";
    public final static String TB_NETWORKCONFIG = "T_BD_NetworkConfig";
    public final static String TB_USER = "T_BD_User";

    public static final String HTLP_EXTRA_TAG = "help";
    public final static int IMPORT_NUMBER_ONCE = 500;


    public final static int MATERIAL_IMPORT_REQUEST = 1;
    public final static int WAREHOUSE_IMPORT_REQUEST = 2;
    public final static int MATERIAL_CHECK_REQUEST = 3;
    public final static int WAREHOUSE_CHECK_REQUEST = 4;


    public final static int ADD_BILL_SUCCESS_MSG = 5;
    public final static int ADD_BILL_FAIL_MSG = 6;

    public final static int IMPORT_DATA_SUCCESS_MSG = 7;
    public final static int IMPORT_DATA_FAIL_MSG = 8;
    public final static int CHECK_DATA_SUCCESS_MSG = 9;
    public final static int CHECK_DATA_FAIL_MSG = 10;

    public final static int EXPORT_DATA_SUCCESS_MSG = 11;
    public final static int EXPORT_DATA_FAIL_MSG = 12;
    public final static int EXPORT_ALL_SUCCESS_MSG = 13;
    public final static int EXPORT_ALL_FAIL_MSG = 14;

    public final static int QUERY_DEVICE_ID = 15;

    public static final int SAVE_PARAM_SUCCESS = 16;
    public static final int SAVE_PARAM_FAIL = 17;
    public static final int CHECK_BARCODE_NO_EXIST = 18;
    public static final int NOT_UNIQUE_CODE = 19;
    public static final int SCAN_EDIT_NUMBER = 20;
    public static final int CLEAR_DATA_SUCCESS = 21;
    public static final int CLEAR_DATA_FAIL = 22;

    public static final int TEST_CONNECT_SUCCESS = 23;
    public static final int TEST_CONNECT_FAIL = 24;

    public static final int IMPORT_DATA_LOG = 25;

    public static final int DOWNLOAD_DATA_SUCCESS = 26;
    public static final int DOWNLOAD_DATA_FAIL = 27;
    public static final int DOWNLOAD_NO_CONTENT = 28;
    public static final int VERSION_NO_MATCH = 29;
    public static final int NETWORK_REQUEST_FAIL = 30;

    public static final int REQUEST_FOR_SCAN = 1000;

    public enum LogAction {
        ACTION_OPEN_BILL, ACTION_DELETE_BILL, ACTION_DELETE_ALL_BILL,
        ACTION_EXPORT_BILL,
        ACTION_EXPORT_ALL_BILL, ACTION_EXPORT_ADD_LOACTION,
        ACTION_DELETE_LOACTION,
        ACTION_MODIFY_NUM, ACTION_UPLOAD_BILL, ACTION_UPLOAD_ALL_BILL,
        ACTION_DOWNLOAD_PARAM,
        ACTION_DOWNLOAD_MATERIAL, ACTION_DOWNLOAD_WAREHOUSE,
        ACTION_DOWNLOAD_USER,
        ACTION_CLEAR_DATA, ACTION_IMPORT_DATA, ACTION_GOODS_QUERY,
        ACTION_PARAM_SET,
        ACTION_IMPORT_SET, ACTION_EXPORT_SET, ACTION_DISPLAY_SET,
        ACTION_TIME_SET,
    }

    public static final int DOWNLOAD_FAILED = 1000000;
    public static final int DOWNLOAD_SUCCESS = 1000001;
    public static final int UPDATE_FAILED = 1000002;
    public static final int UPDATE_SUCCESS = 1000003;
    public static final int DOWNLOAD_UPDATE_DONE = 1000004;

    public static final int MAX_DOWNLOAD_COUNT = 4;
    public static final int MAX_DOWNLOAD_STEP = 1;

    public static final int DO_ALL_FINISH = 100000000;

    public static final int BASE_INFO_ID = 0;
    public static final int SERVER_INFO_ID = BASE_INFO_ID + 1;
    public static final int SALESINFO_ID = BASE_INFO_ID + 2;
    public static final int SHIPMENTTYPEINFO_ID = BASE_INFO_ID + 3;
    public static final int LIUCANGTYPEINFO_ID = BASE_INFO_ID + 4;
    public static final int VEHICEINFO_ID = BASE_INFO_ID + 5;

    public static final int STARTDOWNLOAD_INFO = 1000;
    public static final int STARTDOWNLOAD_SERVERINFO = STARTDOWNLOAD_INFO +
            SERVER_INFO_ID;
    public static final int STARTDOWNLOAD_SALESINFO = STARTDOWNLOAD_INFO +
            SALESINFO_ID;
    public static final int STARTDOWNLOAD_SHIPMENTTYPEINFO =
            STARTDOWNLOAD_INFO +
                    SHIPMENTTYPEINFO_ID;
    public static final int STARTDOWNLOAD_LIUCANGTYPEINFO =
            STARTDOWNLOAD_INFO + LIUCANGTYPEINFO_ID;
    public static final int STARTDOWNLOAD_VEHICEINFO = STARTDOWNLOAD_INFO +
            VEHICEINFO_ID;

    public static final int DOWNLOAD_INFO_SUCCESS = 2000;
    public static final int DOWNLOAD_SERVERINFO_SUCCESS =
            DOWNLOAD_INFO_SUCCESS + SERVER_INFO_ID;
    public static final int DOWNLOAD_SALESINFO_SUCCESS =
            DOWNLOAD_INFO_SUCCESS + SALESINFO_ID;
    public static final int DOWNLOAD_SHIPMENTTYPEINFO_SUCCESS =
            DOWNLOAD_INFO_SUCCESS +
                    SHIPMENTTYPEINFO_ID;
    public static final int DOWNLOAD_LIUCANGTYPEINFO_SUCCESS =
            DOWNLOAD_INFO_SUCCESS +
                    LIUCANGTYPEINFO_ID;
    public static final int DOWNLOAD_VEHICEINFO_SUCCESS =
            DOWNLOAD_INFO_SUCCESS + VEHICEINFO_ID;


    public static final int DOWNLOAD_INFO_FAILED = 3000;
    public static final int DOWNLOAD_SERVERINFO_FAILED = DOWNLOAD_INFO_FAILED
            + SERVER_INFO_ID;
    public static final int DOWNLOAD_SALESINFO_FAILED = DOWNLOAD_INFO_FAILED
            + SALESINFO_ID;
    public static final int DOWNLOAD_SHIPMENTTYPEINFO_FAILED =
            DOWNLOAD_INFO_FAILED +
                    SHIPMENTTYPEINFO_ID;
    public static final int DOWNLOAD_LIUCANGTYPEINFO_FAILED =
            DOWNLOAD_INFO_FAILED +
                    LIUCANGTYPEINFO_ID;
    public static final int DOWNLOAD_VEHICEINFO_FAILED = DOWNLOAD_INFO_FAILED
            + VEHICEINFO_ID;


    public static final int UPDATE_DATA_SUCCESS = 4000;
    public static final int UPDATE_SERVERINFO_SUCCESS = UPDATE_DATA_SUCCESS +
            SERVER_INFO_ID;
    public static final int UPDATE_SALESINFO_SUCCESS = UPDATE_DATA_SUCCESS +
            SALESINFO_ID;
    public static final int UPDATE_SHIPMENTTYPEINFO_SUCCESS =
            UPDATE_DATA_SUCCESS +
                    SHIPMENTTYPEINFO_ID;
    public static final int UPDATE_LIUCANGTYPEINFO_SUCCESS =
            UPDATE_DATA_SUCCESS +
                    LIUCANGTYPEINFO_ID;
    public static final int UPDATE_VEHICEINFO_SUCCESS = UPDATE_DATA_SUCCESS +
            VEHICEINFO_ID;

    public static final int UPDATE_DATA_FAILED = 5000;
    public static final int UPDATE_SEVERINFO_FAILED = UPDATE_DATA_FAILED +
            SERVER_INFO_ID;
    public static final int UPDATE_SALESINFO_FAILED = UPDATE_DATA_FAILED +
            SALESINFO_ID;
    public static final int UPDATE_SHIPMENTTYPEINFO_FAILED =
            UPDATE_DATA_FAILED +
                    SHIPMENTTYPEINFO_ID;
    public static final int UPDATE_LIUCANGTYPEINFO_FAILED =
            UPDATE_DATA_FAILED + LIUCANGTYPEINFO_ID;
    public static final int UPDATE_VEHICEINFO_FAILED = UPDATE_DATA_FAILED +
            VEHICEINFO_ID;


    public static final String ARG_USE_CAMERA = "useCamera";
    public static final String ARG_OPERATE_TYPE = "operateType";
    public static final String ARG_BILL_ROWS = "billRows";
    public static final String BILL_DATA = "checkData";
    public final static String BILL_ID = "billId";

    public static final int useCamera = 0;  // 1为使用
    public static final int operateType = 1;  // 1为PDA 2为手机
    public static final int billRows = 50;

    /*
    * 列头的分隔符为,   列的内容为_#&_
    * */
    public static final String FILE_LIST_SEPERATOR = ",";
    public static final String ROW_HEAD_SEPERATOR = ",";
    public static final String CONTENT_SEPERATOR = "_#&_";
    //public static final String INFORMATION_SEPERATOR = "_#&_";//",";

    public static String getInitConfig() {
        StringBuffer config = new StringBuffer();
        config.append(ARG_USE_CAMERA);
        config.append("=");
        config.append(useCamera);
        config.append("\r\n");
        config.append(ARG_OPERATE_TYPE);
        config.append("=");
        config.append(operateType);
        config.append("\r\n");
        config.append(ARG_BILL_ROWS);
        config.append("=");
        config.append(billRows);
        return config.toString();
    }

    // TODO 用户保存在SharedPreference中的数据key名称
    public static final String PRERENCE_LANGUAGE_CHOICE =
            "multiLanguageChoiced";
    public static final String PREFERENCE_KEY_LANGUAGE = "defaultLanguage";
    public static final String PREFERENCE_KEY_SYSTEM_ARG = "system_arg";
    public static final String PREFERENCE_KEY_DEVICE_ID = "device_id";

    //
    public static final String PREFERENCE_KEY_BAQIANG_FIRST_START =
            "baqiang_first_start";

    // 是否记住用户名和密码
    public static final String KEY_IS_REMEMBER_PSW = "is_remember_psw";
    // 网点编号，和员工编号相关
    public static final String PREFERENCE_KEY_SALE_SERVICE = "sale_service";
    // 密码
    public static final String PREFERENCE_KEY_PSW = "psw";
    // 用户名 需要注意：此处用户名不包含网点编号
    public static final String PREFERENCE_KEY_USERNAME = "username";
    // 数据库服务器地址
    public static final String PREFERENCE_KEY_DATA_SERVER_ADDRESS =
            "data_server_address";
    // 数据库服务器端口
    public static final String PREFERENCE_KEY_DATA_SERVER_PORT =
            "data_server_port";
    // 快件查询地址
    public static final String PREFERENCE_KEY_EXPRESS_QUERY_ADDRESS =
            "express_address";
    // 捷宝数据服务器地址
    public static final String PREFERENCE_KEY_JB_SERVER =
            "jiebao_server_address";
    // 捷宝数据服务器端口
    public static final String PREFERENCE_KEY_JB_SERVER_PORT =
            "jiebao_server_port";
    // 称重预付款设置
    public static final String PREFERENCE_KEY_WEIGH_FORE_PAYMENT =
            "weigh_fore_payment";
    // 称重录单设置
    public static final String PREFERENCE_KEY_WEIGH_INPUT_BUSINESS =
            "weigh_input_business";
    // 到件预付款设置
    public static final String PREFERENCE_KEY_ARRIVAL_FORE_PAYMENT =
            "arrival_fore_payment";
    // 到/发件扫描判断开关
    public static final String PREFERENCE_KEY_SCAN_SWITCH = "scan_switch";

    // 装车发件表名
    public static final String DB_TABLE_NAME_LOAD_SEND = "zcfajian";
    // 卸车到件表名
    public static final String DB_TABLE_NAME_UNLOAD_ARRIVAL = "xcdaojian";
    // 到件表名
    public static final String DB_TABLE_NAME_CARGO_ARRIVAL = "daojian";
    // 发件表名
    public static final String DB_TABLE_NAME_SHIPMENT = "fajian";
    // 留仓件表名
    public static final String DB_TABLE_NAME_STAY_HOUSE = "liucangjian";

    // 基础数据：车辆信息
    public static final String DB_TABLE_NAME_VEHICLE_INFO = "vehicleinfo";
    // 基础数据：留仓信息
    public static final String DB_TABLE_NAME_LIU_CANG = "liucang";
    // 基础数据：网点信息
    public static final String DB_TABLE_NAME_SALE_SERVICE = "salesservice";
    // 基础数据：快件类型
    public static final String DB_TABLE_NAME_SHIPMENT_TYPE = "shipmenttype";

    // 更新所有记录
    public static final int SYNC_UNLOAD_DATA_TYPE_ALL = 0;
    // 更新装车发件 未上传记录数
    public static final int SYNC_UNLOAD_DATA_TYPE_ZCFJ =
            SYNC_UNLOAD_DATA_TYPE_ALL + 1;
    // 更新卸车到件 未上传记录数
    public static final int SYNC_UNLOAD_DATA_TYPE_XCDJ =
            SYNC_UNLOAD_DATA_TYPE_ZCFJ + 1;
    // 更新到件 未上传记录数
    public static final int SYNC_UNLOAD_DATA_TYPE_DJ =
            SYNC_UNLOAD_DATA_TYPE_XCDJ + 1;
    // 更新发件 未上传记录数
    public static final int SYNC_UNLOAD_DATA_TYPE_FJ =
            SYNC_UNLOAD_DATA_TYPE_DJ + 1;
    // 更新留仓件 未上传记录数
    public static final int SYNC_UNLOAD_DATA_TYPE_LCJ =
            SYNC_UNLOAD_DATA_TYPE_FJ + 1;

    // SP存储未上传记录数：装车发件
    public static final String PREFERENCE_NAME_ZCFJ =
            "unload_number_records_zcfj";
    // SP存储未上传记录数：卸车到件
    public static final String PREFERENCE_NAME_XCDJ =
            "unload_number_records_xcdj";
    // SP存储未上传记录数：到件
    public static final String PREFERENCE_NAME_DJ = "unload_number_records_dj";
    // SP存储未上传记录数：发件
    public static final String PREFERENCE_NAME_FJ = "unload_number_records_fj";
    // SP存储未上传记录数：留仓件
    public static final String PREFERENCE_NAME_LCJ =
            "unload_number_records_lcj";

    // 自动上传记录
    public static final String AUTO_ACTION_UPLOAD_RECORDS = "android.intent"
            + ".action.jiebao" +
            ".upload" + ".records";
    public static final String PREFERENCE_NAME_AUTO_UPLOAD_TIME =
            "auto_upload_time";

    public static final String SEARCH_NAME_ZCFJ = "装车发件";
    public static final String SEARCH_NAME_XCDJ = "卸车到件";
    public static final String SEARCH_NAME_DJ = "到件";
    public static final String SEARCH_NAME_FJ = "发件";
    public static final String SEARCH_NAME_LCJ = "留仓件";


}
