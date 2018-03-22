package com.arthur.appupdate;

import org.xutils.x;

import android.app.Application;

/**
 * Created by Administrator on 2018/3/22 0022.
 */

public class MainApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
        x.Ext.setDebug(false);
    }
}
