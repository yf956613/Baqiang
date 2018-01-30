package com.jiebao.baqiang.global;

public class Constant {

    public final static String CLIENT_VERSION = "1";

    //public static final String UPDATE_URL_PREFIX = "http://120.76.79.124:8090/upgrade/";
    public static final String UPDATE_URL_PREFIX = "http://www.kjb1688.com:8090/upgrade/";
    public static final String UPDATE_VERSION_URL = UPDATE_URL_PREFIX + "external/checkupdate.htm";


    public final static String APP_CODE = "stockbao_android";//"stockbao_android";
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
        ACTION_OPEN_BILL,
        ACTION_DELETE_BILL,
        ACTION_DELETE_ALL_BILL,
        ACTION_EXPORT_BILL,
        ACTION_EXPORT_ALL_BILL,
        ACTION_EXPORT_ADD_LOACTION,
        ACTION_DELETE_LOACTION,
        ACTION_MODIFY_NUM,
        ACTION_UPLOAD_BILL,
        ACTION_UPLOAD_ALL_BILL,
        ACTION_DOWNLOAD_PARAM,
        ACTION_DOWNLOAD_MATERIAL,
        ACTION_DOWNLOAD_WAREHOUSE,
        ACTION_DOWNLOAD_USER,
        ACTION_CLEAR_DATA,
        ACTION_IMPORT_DATA,
        ACTION_GOODS_QUERY,
        ACTION_PARAM_SET,
        ACTION_IMPORT_SET,
        ACTION_EXPORT_SET,
        ACTION_DISPLAY_SET,
        ACTION_TIME_SET,
    }


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

    public static final String PRERENCE_LANGUAGE_CHOICE = "multiLanguageChoiced";
    public static final String PREFERENCE_KEY_LANGUAGE = "defaultLanguage";
    public static final String PREFERENCE_KEY_SYSTEM_ARG = "system_arg";
    public static final String PREFERENCE_KEY_DEVICE_ID = "device_id";
    public static final String KEY_IS_REMEMBER_PSW = "is_remember_psw";
    public static final String PREFERENCE_KEY_SALE_SERVICE = "sale_service";
    public static final String PREFERENCE_KEY_PSW = "psw";
    public static final String PREFERENCE_KEY_USERNAME = "username";

}
