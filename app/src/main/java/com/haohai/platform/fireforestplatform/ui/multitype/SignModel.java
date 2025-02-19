package com.haohai.platform.fireforestplatform.ui.multitype;

/**
 * Created by qc
 * on 2023/12/14.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class SignModel {
    private int index;
    private String name;
    private String userId;
    private String area;
    private String contactNumber;
    private int attendanceTimes;
    private String lastPatrolDate;
    private int totalPatrolLength;

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public int getAttendanceTimes() {
        return attendanceTimes;
    }

    public void setAttendanceTimes(int attendanceTimes) {
        this.attendanceTimes = attendanceTimes;
    }

    public String getLastPatrolDate() {
        return lastPatrolDate;
    }

    public void setLastPatrolDate(String lastPatrolDate) {
        this.lastPatrolDate = lastPatrolDate;
    }

    public int getTotalPatrolLength() {
        return totalPatrolLength;
    }

    public void setTotalPatrolLength(int totalPatrolLength) {
        this.totalPatrolLength = totalPatrolLength;
    }
}
