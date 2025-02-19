package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/4/10.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class EmergencyFilter {
    private String title;
    private String area;
    private String areaId;
    private String startTime;
    private String endTime;
    private String time;
    private String level;
    private String type;
    private String typeId;

    public EmergencyFilter() {
    }

    public EmergencyFilter(String title, String area, String areaId, String startTime, String endTime, String time, String level, String type, String typeId) {
        this.title = title;
        this.area = area;
        this.areaId = areaId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.time = time;
        this.level = level;
        this.type = type;
        this.typeId = typeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return "EmergencyFilter{" +
                "title='" + title + '\'' +
                ", area='" + area + '\'' +
                ", areaId='" + areaId + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", time='" + time + '\'' +
                ", level='" + level + '\'' +
                ", type='" + type + '\'' +
                ", typeId='" + typeId + '\'' +
                '}';
    }
}
