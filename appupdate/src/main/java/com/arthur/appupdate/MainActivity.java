package com.arthur.appupdate;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static long FIVE_MINUTES = 1000 * 60 * 5;

    private ZCFajianFileContent insertForScanner(String barcode) {
        ZCFajianFileContent content = new ZCFajianFileContent();

        // 当前的时间
        Date scanDate = new Date();
        // 1595174400000 --> 2018-3-12 00:00
        // 7days --> 6048000000L
        // 1min --> 1000 * 60

        content.setGoodsType("2");
        content.setScanDate(scanDate);
        content.setShipmentNumber(barcode);
        // 该结果从 扫码时间 转化得来
        content.setOperateDate(new SimpleDateFormat("yyyMMdd")
                .format(scanDate));
        return content;
    }

    /**
     * 每次扫描后，先将数据存入数据库，需要的数据可根据ZCFajianFileContent对应
     * <p>
     * 1. 存入数据库；
     * 2. 生成了 ID；
     * 3. 生成了 是否可用、是否上传的状态；
     */
    public static boolean insertDataToDatabase(final ZCFajianFileContent
                                                       zcFajianFileContent) {
        DbManager db = BQDataBaseHelper.getDb();
        try {
            db.save(zcFajianFileContent);
            return true;
        } catch (DbException e) {
            LogUtil.trace(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);

        this.findViewById(R.id.btn_save).setOnClickListener(new View
                .OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtil.trace("生成模拟数据");

                // 模拟数据插入：循环插入
                insertDataToDatabase(insertForScanner(""));

                DbManager dbManager = BQDataBaseHelper.getDb();
                try {
                    List<ZCFajianFileContent> list = dbManager.selector
                            (ZCFajianFileContent.class).findAll();
                    if (list != null) {
                        LogUtil.trace("size:" + list.size());

                        for (int index = 0; index < list.size(); index++) {
                            LogUtil.trace("-->" + index + ": " + list.get
                                    (index).toString());
                        }
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });

        this.findViewById(R.id.btn_clear).setOnClickListener(new View
                .OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtil.trace("清除5分钟之前数据");

                Date dateLimited = new Date(new Date().getTime() -
                        FIVE_MINUTES);
                LogUtil.trace("清除：" + new SimpleDateFormat("yyyMMddHHmmss")
                        .format(dateLimited) + "; 之前的数据");

                try {
                    DbManager dbManager = BQDataBaseHelper.getDb();
                    List<ZCFajianFileContent> list = dbManager.selector
                            (ZCFajianFileContent.class).where
                            ("ScanDate", "<=", dateLimited).findAll();
                    LogUtil.trace("size:" + list.size());

                    for (int index = 0; index < list.size(); index++) {
                        dbManager.delete(ZCFajianFileContent.class,
                                WhereBuilder.b("id", "=", list.get(index)
                                        .getId()));
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {"android.permission" + ""
            + "" + "" + "" + ""
            + ".READ_EXTERNAL_STORAGE", "android.permission" + "" +
            ".WRITE_EXTERNAL_STORAGE"};

    /**
     * App申请系统相关权限
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission" +
                            ".WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
