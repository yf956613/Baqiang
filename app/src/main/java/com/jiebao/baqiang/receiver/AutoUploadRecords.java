package com.jiebao.baqiang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.DbManager;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class AutoUploadRecords extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.trace("intent.action:" + intent.getAction());

        // TODO 执行记录上传操作，抽取文件上传操作
    }
}
