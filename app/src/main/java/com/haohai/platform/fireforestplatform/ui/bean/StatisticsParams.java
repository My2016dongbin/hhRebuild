package com.haohai.platform.fireforestplatform.ui.bean;

import java.util.List;

/**
 * Created by qc
 * on 2023/8/2.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class StatisticsParams {
    private List<String> ids;
    private String attendanceStatus;
    private String startTime;
    private String endTime;
    private int limit;
    private int page;
    private Dto dto;

    public static class Dto{
        private String startTime;
        private String endTime;
        private List<String> ids;
        private String attendanceStatus;

        public Dto(String startTime, String endTime, List<String> ids, String attendanceStatus) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.ids = ids;
            this.attendanceStatus = attendanceStatus;
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

        public List<String> getIds() {
            return ids;
        }

        public void setIds(List<String> ids) {
            this.ids = ids;
        }

        public String getAttendanceStatus() {
            return attendanceStatus;
        }

        public void setAttendanceStatus(String attendanceStatus) {
            this.attendanceStatus = attendanceStatus;
        }
    }

    public StatisticsParams(int page, int limit, Dto dto) {
        this.dto = dto;
        this.limit = limit;
        this.page = page;
    }

    public StatisticsParams( int limit, int page,List<String> ids, String attendanceStatus) {
        this.ids = ids;
        this.attendanceStatus = attendanceStatus;
        this.limit = limit;
        this.page = page;
    }

    public Dto getDto() {
        return dto;
    }

    public void setDto(Dto dto) {
        this.dto = dto;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
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

    public StatisticsParams() {
    }
}
