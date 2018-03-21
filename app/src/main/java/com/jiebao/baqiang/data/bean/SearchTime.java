package com.jiebao.baqiang.data.bean;

import com.jiebao.baqiang.util.LogUtil;

import java.util.Date;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class SearchTime {
    private int year;
    private int month;
    private int day;

    private int hour;
    private int minute;
    private int second;

    public SearchTime() {
    }

    public SearchTime(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public String getDayString() {
        return year + "-" + month + "-" + day;
    }

    public String getHourString() {
        StringBuffer tmp = new StringBuffer();

        if (hour < 10) {
            tmp.append(0).append(hour);
        } else {
            tmp.append(hour);
        }

        tmp.append(":");

        if (minute < 10) {
            tmp.append(0).append(minute);
        } else {
            tmp.append(minute);
        }

        return tmp.toString();
    }

    @Override
    public String toString() {
        return "SearchTime{" + "year=" + year + ", month=" + month + ", day=" + day + ", hour=" +
                hour + ", minute=" + minute + ", second=" + second + '}';
    }
}
