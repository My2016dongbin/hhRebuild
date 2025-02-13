package com.haohai.platform.fireforestplatform.old;

/**
 * Created by qc
 * on 2022/7/29.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class SignMonth2 {
    private String date;
    private String checkInTime;
    private String checkOutTime;
    private String week;

    public SignMonth2() {
    }

    public SignMonth2(String date, String checkInTime, String checkOutTime, String week) {
        this.date = date;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.week = week;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}
