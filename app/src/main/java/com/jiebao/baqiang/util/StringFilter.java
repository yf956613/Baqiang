package com.jiebao.baqiang.util;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * 过滤单引号'和/ ，这里两个符号会引起sql异常
 * Created by shanjiang_gao on 2017/5/9.
 */

public class StringFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String s = source.toString();
        return s.replaceAll("'", "").replaceAll("/", "");
    }
}
