package com.jiebao.baqiang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.jiebao.baqiang.activity.BaseActivityWithTitleAndNumber;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.bean.CommonDbHelperToUploadFile;
import com.jiebao.baqiang.data.bean.IDbHelperToUploadFileCallback;
import com.jiebao.baqiang.data.db.DaojianDBHelper;
import com.jiebao.baqiang.data.db.FajianDBHelper;
import com.jiebao.baqiang.data.db.LiucangDBHelper;
import com.jiebao.baqiang.data.db.XcdjDBHelper;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCfajianUploadFile;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.NetworkUtils;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class AutoUploadRecordsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.trace("intent.action:" + intent.getAction());
        final BaseActivityWithTitleAndNumber mTopActivity = (BaseActivityWithTitleAndNumber)
                BaqiangApplication.getTopActivity();
        LogUtil.trace("class name:" + BaqiangApplication.getTopActivityName());

        if (mTopActivity != null) {
            if (!NetworkUtils.isNetworkConnected(mTopActivity)) {
                Toast.makeText(mTopActivity, "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                return;
            }

            // 此处的Activity不显示Dialog，完成后，更新UI即可
            new CommonDbHelperToUploadFile<ZCFajianFileContent>().setCallbackListener(new IDbHelperToUploadFileCallback() {

                @Override
                public boolean onSuccess(String s) {
                    LogUtil.trace("s:" + s);

                    Toast.makeText(mTopActivity, s, Toast.LENGTH_SHORT).show();
                    // F1事件，传递给Activity更新UI
                    mTopActivity.syncViewAfterUpload(Constant.SYNC_UNLOAD_DATA_TYPE_ALL);
                    return true;
                }

                @Override
                public boolean onError(Throwable throwable, boolean b) {
                    return false;
                }

                @Override
                public boolean onFinish() {
                    LogUtil.trace();

                    // F1事件，传递给Activity更新UI
                    mTopActivity.syncViewAfterUpload(Constant.SYNC_UNLOAD_DATA_TYPE_ALL);

                    return false;
                }
            }).uploadUnloadRecords();
        }
    }
}
