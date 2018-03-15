package com.jiebao.baqiang.util;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文本字符串工具类
 */

public class TextStringUtil {

    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y",
            "Z"};

    /**
     * 生成8位带有字母或数字的字符串，有效避免重复
     *
     * @return
     */
    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }

    /**
     * 生成指定格式的事件字符串
     *
     * @return
     */
    public static String getFormatTimeString() {
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(current));
        return time;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 1 格式：20180314204610
     * @param str2 时间参数 2 格式：20180314204200
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            // Wed Mar 14 20:42:00 CST 2018
            one = df.parse(str1);
            System.out.println(one);
            // 631166400000
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] times = {day, hour, min, sec};

        // [0, 0, 4, 10]
        return times;
    }

    /**
     * 生成指定格式的事件字符串
     *
     * @return
     */
    public static String getFormatTime() {
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyyMMdd").format(new Date(current));
        return time;
    }

    /**
     * 判断IP地址的合法性，采用了正则表达式的方法来判断
     *
     * @param text
     * @return
     */
    public static boolean isIpAddressAvailable(String text) {
        if (!TextUtils.isEmpty(text)) {
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "" +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "" +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "" +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            if (text.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 端口地址校验，使用正则表达式
     *
     * @param text
     * @return
     */
    public static boolean isPortCheckAvailable(String text) {
        if (text != null && !text.isEmpty()) {
            String regex = "([0-9]|[1-9]\\d{1," + "3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])";
            if (text.matches(regex)) {
                return true;
            }
        }
        return false;
    }


}
