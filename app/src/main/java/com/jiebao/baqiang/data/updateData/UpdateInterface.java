package com.jiebao.baqiang.data.updateData;

import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.SharedUtil;

/**
 * 所有全局用到网点信息、用户编号和密码
 */

public class UpdateInterface {

    public static String salesId = SharedUtil.getString(BaqiangApplication
            .getContext(), Constant.PREFERENCE_KEY_SALE_SERVICE);
    public static String userName = salesId + SharedUtil.getString
            (BaqiangApplication
            .getContext(), Constant.PREFERENCE_KEY_USERNAME);
    public static String psw = SharedUtil.getString(BaqiangApplication
            .getContext(), Constant.PREFERENCE_KEY_PSW);
}
