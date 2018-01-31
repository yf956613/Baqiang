package com.jiebao.baqiang.data.db;

import android.annotation.SuppressLint;
import android.util.Log;

import com.jiebao.baqiang.util.AsyThreadFactory;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.DbManager;
import org.xutils.DbManager.DaoConfig;
import org.xutils.DbManager.DbUpgradeListener;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;

@SuppressLint("NewApi")
public class BQDataBaseHelper {
    protected static final String TAG = "DbHelper";
    private static DaoConfig mDaoConfig;

    public static DaoConfig getDaoConfig() {
        if (mDaoConfig == null) {
            LogUtil.trace("mDaoConfig is null...");

            mDaoConfig = new DaoConfig().setDbName("baqiang.db").setDbVersion(1)
                    .setAllowTransaction(true).setDbDir(new File("/sdcard/"))
                    .setDbUpgradeListener(new DbUpgradeListener() {

                        @Override
                        public void onUpgrade(DbManager db, int oldVersion,
                                              int newVersion) {

                        }
                    }).setTableCreateListener(new DbManager
                            .TableCreateListener() {

                        @Override
                        public void onTableCreated(DbManager dbManager,
                                                   TableEntity<?> tableEntity) {
                            LogUtil.e(TAG, "start to create table...");
                            String name = tableEntity.getName();
                            LogUtil.e(TAG, "name:" + name);
                        }
                    }).setDbOpenListener(new DbManager.DbOpenListener() {

                        @Override
                        public void onDbOpened(DbManager dbManager) {
                            LogUtil.d(TAG, "start to open database...");
                            // 开启多线程操作 开启WAL, 对写入加速提升巨大
                            dbManager.getDatabase().enableWriteAheadLogging();
                        }
                    });
        }

        return mDaoConfig;
    }

    public static DbManager getDb() {
        return x.getDb(getDaoConfig());
    }

    public static void deleteDB() {
        try {
            getDb().dropDb();
        } catch (DbException e1) {
            e1.printStackTrace();
        }
    }

    public static List<?> queryData(Class<?> cls, String columnName, String
            op, Object value)
            throws DbException {
        return getDb().selector(cls).where(columnName, op, value).findAll();
    }

    public static List<?> queryData(Class<?> cls) {
        try {
            return getDb().selector(cls).findAll();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<?> queryData(Class<?> cls, String key) {
        try {
            return getDb().selector(cls).select(key).findAll();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveToDB(final List<? extends Object> dts) {
        LogUtil.e(TAG, "save  to DB");

        Executor executorService = AsyThreadFactory.getExecutorService();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DbManager db = getDb();
                    db.saveOrUpdate(dts);
                } catch (DbException e) {
                    e.printStackTrace();
                    Log.e(TAG, "save gd mingxi to DB e:" + e.getMessage());
                }
            }
        });
    }
}
