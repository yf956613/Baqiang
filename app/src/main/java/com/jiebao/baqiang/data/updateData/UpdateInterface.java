package com.jiebao.baqiang.data.updateData;

import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.SharedUtil;

/**
 * Created by LENOVO on 2018/2/1.
 */

public class UpdateInterface {

    public static String salesId = SharedUtil.getString(BaqiangApplication
            .getContext(), Constant
            .PREFERENCE_KEY_SALE_SERVICE);
    public static String userName = SharedUtil.getString(BaqiangApplication
            .getContext(), Constant
            .PREFERENCE_KEY_USERNAME);
    public static String psw = SharedUtil.getString(BaqiangApplication
            .getContext(), Constant
            .PREFERENCE_KEY_PSW);
}
