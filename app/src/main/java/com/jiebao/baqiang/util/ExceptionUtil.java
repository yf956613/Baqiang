package com.jiebao.baqiang.util;

import com.jiebao.baqiang.application.BaqiangApplication;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * java异常工具类
 * Created by Administrator on 2016/2/22.
 */
public class ExceptionUtil {
    /**
     * 从异常中读取所有异常信息
     *
     * @param ex
     * @return
     */
    public static String getExceptionAllinformation(Exception ex) {
        String sOut = "";
        StackTraceElement[] trace = ex.getStackTrace();
        for (StackTraceElement s : trace) {
            sOut += "\tat " + s + "\r\n";
        }
        return sOut;
    }

    /**
     * 从异常中读取所有异常信息
     *
     * @param ex
     * @return
     */
    public static String getExceptionAllinformation_01(Exception ex) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        ex.printStackTrace(pout);
        String ret = new String(out.toByteArray());
        pout.close();
        try {
            out.close();
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * 从异常中读取所有异常信息
     *
     * @param e
     * @return
     */
    public static String getExceptionInfo(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

    public static void printStackTrace(Throwable e) {
        if (e != null) {
            if (BaqiangApplication.isDebug()){
                e.printStackTrace();
            }
        }
    }
}
