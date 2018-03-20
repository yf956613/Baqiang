package com.jiebao.baqiang.util;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class TimeUtil {

    // 差距时间：3小时
    public static final long DELTA_TIME_DISTANCE = 10800;

    /**
     * 判断时间差距是否超出预期值
     * <p>
     * 预期时间：3小时
     *
     * @param delta: {天, 时, 分, 秒}
     * @return
     */
    public static boolean isTimeOutOfRange(long[] delta) {
        // 将时间数字转化为数值，判断数值是否超出即可
        long deltaValue = delta[0] * 24 * 60 * 60 + delta[1] * 60 * 60 + delta[2] * 60 + delta[3];
        if (deltaValue > DELTA_TIME_DISTANCE) {
            return true;
        }

        return false;
    }
}
