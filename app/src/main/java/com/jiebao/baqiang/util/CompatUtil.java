package com.jiebao.baqiang.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * android API版本兼容工具类
 * Created by shanjiang_gao on 2016/8/7.
 */

public class CompatUtil {

    /**
     * 获取颜色值
     *
     * @param context
     * @param color
     * @return
     */
    public static int getColor(Context context, int color) {
        if (Build.VERSION.SDK_INT >= 23) {
            return context.getResources().getColor(color, context.getTheme());
        } else {
            return context.getResources().getColor(color);
        }
    }

    /**
     * 获取图片
     *
     * @param context
     * @param drawable
     * @return
     */
    public static Drawable getDrawable(Context context, int drawable) {
        if (Build.VERSION.SDK_INT >= 21) {
            return context.getResources().getDrawable(drawable, context.getTheme());
        } else {
            return context.getResources().getDrawable(drawable);
        }
    }

    /**
     * 设置view背景
     *
     * @param view
     * @param drawable
     */
    public static void setBackGroudDrawable(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }
}
