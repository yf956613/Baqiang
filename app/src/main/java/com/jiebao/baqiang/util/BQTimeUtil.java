package com.jiebao.baqiang.util;

import android.text.TextUtils;
import android.util.TimeUtils;

import java.util.Date;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class BQTimeUtil {

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

    /**
     * 将查询时间字符串转化为数据库时间格式
     *
     * @param time：begin:2018-3-22 00:00; end:2018-3-22 23:59
     * @param type：1表示开始时间，2表示结束时间
     * @return result：20180320204809
     */
    public static String convertSearchTime(String time, int type) {

        if (!TextUtils.isEmpty(time)) {
            // 区分年月日和时分
            String[] values = time.split(" ");
            if (values != null && values.length == 2) {
                String yearMonthDay = values[0];
                String hourMinute = values[1];

                String[] yearMonthDayArray = yearMonthDay.split("-");
                String[] hourMinuteArray = hourMinute.split(":");

                if (yearMonthDayArray != null && hourMinuteArray != null) {
                    StringBuffer sb = new StringBuffer();

                    for (int index = 0; index < yearMonthDayArray.length; index++) {
                        sb.append(yearMonthDayArray[index]);
                    }

                    for (int index = 0; index < hourMinuteArray.length; index++) {
                        sb.append(hourMinuteArray[index]);
                    }

                    switch (type) {
                        case 1: {
                            sb.append("00");
                            break;
                        }

                        case 2: {
                            sb.append("59");
                            break;
                        }
                    }

                    return sb.toString();
                }
            }
        }
        return null;
    }
}
