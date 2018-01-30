package com.jiebao.baqiang.data.db;

import com.jiebao.baqiang.util.AsyThreadFactory;
import android.annotation.SuppressLint;
import android.util.Log;


import org.xutils.DbManager;
import org.xutils.DbManager.DaoConfig;
import org.xutils.DbManager.DbUpgradeListener;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@SuppressLint("NewApi")
public class BQDataBaseHelper {

    protected static final String TAG = "DbHelper";
    private static DaoConfig daoConfig;

    public static DaoConfig getDaoConfig() {
        if (daoConfig == null) {
            daoConfig = new DaoConfig().setDbName("baqiang.db")
                    .setDbVersion(1).setAllowTransaction(true)
                    .setDbDir(new File("/sdcard/"))
                    .setDbUpgradeListener(new DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        }
                    });
        }

        return daoConfig;
    }

    public static DbManager getDb(){
        return x.getDb(getDaoConfig());
    }

    public static void deleteDB(){
        try {
            getDb().dropDb();
        } catch (DbException e1) {
            e1.printStackTrace();
        }

    }


    public static List<?> queryData(Class<?> cls ,
                                    String columnName, String op, Object value) throws DbException{
        return getDb().selector(cls).where(columnName, op, value).findAll();
    }

    public static List<?> queryData(Class<?> cls){
        try {
            return getDb().selector(cls).findAll();
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static List<?> queryData(Class<?> cls,String key){
        try {
            return getDb().selector(cls).select(key).findAll();
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    public static void saveToDB(final List<?extends Object> dts) {
        Log.i(TAG, "save  to DB");
        Executor executorService = AsyThreadFactory.getExecutorService();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DbManager db = getDb();
                    db.saveOrUpdate(dts);
                } catch (DbException e) {
                    e.printStackTrace();
                    Log.e(TAG, "save gd mingxi to DB e:"+e.getMessage());
                }
            }
        });
    }
}
