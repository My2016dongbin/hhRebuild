package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2022/12/2.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class FlameListModel {
    private int page;
    private int limit;
    private Dto dto;

    public FlameListModel(int page, int limit, Dto dto) {
        this.page = page;
        this.limit = limit;
        this.dto = dto;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Dto getDto() {
        return dto;
    }

    public void setDto(Dto dto) {
        this.dto = dto;
    }

    @Override
    public String toString() {
        return "FrameListModel{" +
                "page=" + page +
                ", limit=" + limit +
                ", dto=" + dto +
                '}';
    }

    public static class Dto{
        private String address;
        private String startTime;
        private String endTime;

        public Dto(String address, String startTime, String endTime) {
            this.address = address;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
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
            return "Dto{" +
                    "address='" + address + '\'' +
                    ", startTime='" + startTime + '\'' +
                    ", endTime='" + endTime + '\'' +
                    '}';
        }
    }
}
