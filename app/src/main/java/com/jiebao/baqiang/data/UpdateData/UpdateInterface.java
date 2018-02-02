package com.jiebao.baqiang.data.UpdateData;

import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.SharedUtil;

/**
 * Created by LENOVO on 2018/2/1.
 */

public class UpdateInterface {

    public  String salesId = SharedUtil.getString(BaqiangApplication.getContext(), Constant
            .PREFERENCE_KEY_SALE_SERVICE);
    public  String  userName = SharedUtil.getString(BaqiangApplication.getContext(), Constant
            .PREFERENCE_KEY_USERNAME);
    public String psw = SharedUtil.getString(BaqiangApplication.getContext(), Constant
            .PREFERENCE_KEY_PSW);
}
