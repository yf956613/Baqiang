package com.jiebao.baqiang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.jiebao.baqiang.activity.BaseActivityWithTitleAndNumber;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.db.DaojianDBHelper;
import com.jiebao.baqiang.data.db.FajianDBHelper;
import com.jiebao.baqiang.data.db.LiucangDBHelper;
import com.jiebao.baqiang.data.db.XcdjDBHelper;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCfajianUploadFile;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.LogUtil;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class AutoUploadRecordsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.trace("intent.action:" + intent.getAction());

        // TODO 执行记录上传操作，抽取文件上传操作
        ZCfajianUploadFile.uploadZcfjUnloadRecords();
        XcdjDBHelper.uploadXcdjUnloadRecords();
        DaojianDBHelper.uploadDaojianUnloadRecords();
        FajianDBHelper.uploadFajianUnloadRecords();
        LiucangDBHelper.uploadLiucangUnloadRecords();
        Toast.makeText(context, "数据上传成功", Toast.LENGTH_SHORT).show();

        BaseActivityWithTitleAndNumber mTopActivity = (BaseActivityWithTitleAndNumber)
                BaqiangApplication.getTopActivity();
        LogUtil.trace("class name:" + BaqiangApplication.getTopActivityName());

        mTopActivity.syncViewAfterUpload(Constant.SYNC_UNLOAD_DATA_TYPE_ALL);
    }
}
