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
    private String userCode;
    private String fullName;
    private int totalAttendance;
    private int totalAlarm;
    private String manageArea;

    public String getName() {
        return fullName == null || fullName.length() == 0 ? name : fullName;
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
        return manageArea == null || manageArea.length() == 0 ? area : manageArea;
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
        return totalAttendance != 0 ? totalAttendance : attendanceTimes;
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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getFullName() {
        return fullName == null || fullName.length() == 0 ? name : fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getTotalAttendance() {
        return totalAttendance != 0 ? totalAttendance : attendanceTimes;
    }

    public void setTotalAttendance(int totalAttendance) {
        this.totalAttendance = totalAttendance;
    }

    public int getTotalAlarm() {
        return totalAlarm;
    }

    public void setTotalAlarm(int totalAlarm) {
        this.totalAlarm = totalAlarm;
    }

    public String getManageArea() {
        return manageArea == null || manageArea.length() == 0 ? area : manageArea;
    }

    public void setManageArea(String manageArea) {
        this.manageArea = manageArea;
    }
}
