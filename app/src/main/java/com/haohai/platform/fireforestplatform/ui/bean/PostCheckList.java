package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/5/24.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class PostCheckList {

    /**
     * limit : 15
     * page : 1
     * dto : {"startTime":"","endTime":"","isHandle":"","name":"","alarmType":"","ip":0}
     * isAndroid : 0
     */

    private int limit;
    private int page;
    private DtoBean dto;
    private int isAndroid;

    public PostCheckList() {

    }

    public PostCheckList(int limit, int page, DtoBean dto, int isAndroid) {
        this.limit = limit;
        this.page = page;
        this.dto = dto;
        this.isAndroid = isAndroid;
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

    public DtoBean getDto() {
        return dto;
    }

    public void setDto(DtoBean dto) {
        this.dto = dto;
    }

    public int getIsAndroid() {
        return isAndroid;
    }

    public void setIsAndroid(int isAndroid) {
        this.isAndroid = isAndroid;
    }

    public static class DtoBean {
        /**
         * startTime :
         * endTime :
         * isHandle :
         * name :
         * alarmType :
         * ip : 0
         */

        private String startTime;
        private String endTime;
        private String isHandle;
        private String name;
        private String alarmType;
        private int ip;
        private String superviseFlag;

        public DtoBean() {
        }

        public DtoBean(String startTime, String endTime, String isHandle, String name, String alarmType, int ip, String superviseFlag) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.isHandle = isHandle;
            this.name = name;
            this.alarmType = alarmType;
            this.ip = ip;
            this.superviseFlag = superviseFlag;
        }

        public String getSuperviseFlag() {
            return superviseFlag;
        }

        public void setSuperviseFlag(String superviseFlag) {
            this.superviseFlag = superviseFlag;
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

        public String getIsHandle() {
            return isHandle;
        }

        public void setIsHandle(String isHandle) {
            this.isHandle = isHandle;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAlarmType() {
            return alarmType;
        }

        public void setAlarmType(String alarmType) {
            this.alarmType = alarmType;
        }

        public int getIp() {
            return ip;
        }

        public void setIp(int ip) {
            this.ip = ip;
        }
    }
}
