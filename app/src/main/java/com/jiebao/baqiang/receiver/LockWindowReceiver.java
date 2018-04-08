package com.jiebao.baqiang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jiebao.baqiang.activity.BaseActivityWithTitleAndNumber;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.util.LogUtil;

/**
 * Created by Administrator on 2018/4/8 0008.
 */

public class LockWindowReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isEnable = intent.getBooleanExtra("enable", false);
        final BaseActivityWithTitleAndNumber mTopActivity = (BaseActivityWithTitleAndNumber)
                BaqiangApplication.getTopActivity();
        LogUtil.trace("class name:" + BaqiangApplication.getTopActivityName());

        if (isEnable) {
            LogUtil.trace("can input -->");

            if (mTopActivity != null) {
                mTopActivity.dimissLockWindow();
            }

        } else {
            LogUtil.trace("can not input -->");

            if (mTopActivity != null) {
                mTopActivity.showLockWindow();
            }
        }
    }
}
