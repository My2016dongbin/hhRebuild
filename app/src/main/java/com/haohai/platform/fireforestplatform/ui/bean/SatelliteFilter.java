package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/4/10.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class SatelliteFilter {
    private int filterState = 0;//0按时间分类  1按编号分类
    private String startTime;
    private String endTime;

    public SatelliteFilter() {
    }

    public int getFilterState() {
        return filterState;
    }

    public void setFilterState(int filterState) {
        this.filterState = filterState;
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

    @Override
    public String toString() {
        return "SatelliteFilter{" +
                "filterState=" + filterState +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
