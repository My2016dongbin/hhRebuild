package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/5/22.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class PostIdentify {

    /**
     * limit : 15
     * page : 1
     * dto : {"cityName":"","cityCode":"","countyName":"","countyCode":"","startTime":"","endTime":"","isHandle":"1","name":"","alarmType":"","ip":0,"isReal":"1"}
     * isAndroid : 0
     */

    private int limit;
    private int page;
    private DtoBean dto;
    private int isAndroid;

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
         * cityName :
         * cityCode :
         * countyName :
         * countyCode :
         * startTime :
         * endTime :
         * isHandle : 1
         * name :
         * alarmType :
         * ip : 0
         * isReal : 1
         */

        private String cityName;
        private String cityCode;
        private String countyName;
        private String countyCode;
        private String startTime;
        private String endTime;
        private String isHandle;
        private String name;
        private String alarmType;
        private int ip;
        private String isDisposition;
        private String isReal;

        public String getIsDisposition() {
            return isDisposition;
        }

        public void setIsDisposition(String isDisposition) {
            this.isDisposition = isDisposition;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public String getCountyName() {
            return countyName;
        }

        public void setCountyName(String countyName) {
            this.countyName = countyName;
        }

        public String getCountyCode() {
            return countyCode;
        }

        public void setCountyCode(String countyCode) {
            this.countyCode = countyCode;
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

        public String getIsReal() {
            return isReal;
        }

        public void setIsReal(String isReal) {
            this.isReal = isReal;
        }
    }
}
