package com.jiebao.baqiang.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文本字符串工具类
 */

public class TextStringUtil {

    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
            "x", "y", "z", "0",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D",
            "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y",
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
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date
                (current));
        return time;
    }

    /**
     * 生成指定格式的事件字符串
     *
     * @return
     */
    public static String getFormatTime() {
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyyMMdd").format(new Date
                (current));
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
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
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
            String regex = "([0-9]|[1-9]\\d{1," +
                    "3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])";
            if (text.matches(regex)) {
                return true;
            }
        }
        return false;
    }


}
