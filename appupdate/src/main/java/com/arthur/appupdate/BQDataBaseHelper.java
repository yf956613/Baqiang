package com.arthur.appupdate;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.xutils.DbManager;
import org.xutils.DbManager.DaoConfig;
import org.xutils.DbManager.DbUpgradeListener;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.List;

@SuppressLint("NewApi")
public class BQDataBaseHelper {
    protected static final String TAG = "DbHelper";
    private static DaoConfig mDaoConfig;

    public static DaoConfig getDaoConfig() {
        if (mDaoConfig == null) {
            LogUtil.trace("mDaoConfig is null...");

            mDaoConfig = new DaoConfig().setDbName("baqiang.db").setDbVersion(2)
                    .setAllowTransaction(true).setDbDir(new File("/sdcard/"))
                    .setDbUpgradeListener(new DbUpgradeListener() {

                @Override
                public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    LogUtil.trace("oldVersion:" + oldVersion + "; newVersion:" + newVersion);

                    // 使用for实现跨版本升级数据库
                    for (int i = oldVersion; i < newVersion; i++) {
                        switch (i) {
                            case 1: {
                                upgradeToVersion2(db);
                            }
                            break;
                            default:
                        }
                    }

                }
            }).setTableCreateListener(new DbManager.TableCreateListener() {

                @Override
                public void onTableCreated(DbManager dbManager, TableEntity<?> tableEntity) {
                    LogUtil.d(TAG, "start to create table...");
                    String name = tableEntity.getName();
                    LogUtil.d(TAG, "name:" + name);
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

    public static List<?> queryData(Class<?> cls, String columnName, String op, Object value)
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

    /**
     * 数据库的初始版本升级到Version:2
     * <p>
     * 修改部分：liucangjian表中增加字段：是否可用，其值可选为：可用和不可用
     *
     * @param db
     */
    private static void upgradeToVersion2(DbManager db) {

    }

    /**
     * 查询数据库文件中是否有对应表
     *
     * @return false：没有该数据表；true：存在该数据表
     */
    public static boolean tableIsExist(String tableName) {
        boolean result = false;

        if (tableName == null) {
            return false;
        }

        DbManager dbManager = BQDataBaseHelper.getDb();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbManager.getDatabase();
            // 查询内置sqlite_master表，判断是否创建了对应表
            String sql = "select count(*) from sqlite_master where type " + "='table' and name "
                    + "='" + tableName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
        } catch (Exception e) {
            LogUtil.trace(e.getMessage());
        }

        return result;
    }

}
