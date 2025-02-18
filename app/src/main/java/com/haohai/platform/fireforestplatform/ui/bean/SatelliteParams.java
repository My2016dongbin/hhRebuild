package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2023/12/28.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class SatelliteParams {
    private int limit;
    private int page;
    private DTO dto;

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

    public DTO getDto() {
        return dto;
    }

    public void setDto(DTO dto) {
        this.dto = dto;
    }

    public static class DTO{
        private String city;
        private String cityCode;
        private String county;
        private String countyCode;
        private String formattedAddress;
        private String endTime;
        private String startTime;
        private String isHandle;

        public DTO(String city, String cityCode, String county, String countyCode, String formattedAddress, String endTime, String startTime, String isHandle) {
            this.city = city;
            this.cityCode = cityCode;
            this.county = county;
            this.countyCode = countyCode;
            this.formattedAddress = formattedAddress;
            this.endTime = endTime;
            this.startTime = startTime;
            this.isHandle = isHandle;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public String getCountyCode() {
            return countyCode;
        }

        public void setCountyCode(String countyCode) {
            this.countyCode = countyCode;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public void setFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getIsHandle() {
            return isHandle;
        }

        public void setIsHandle(String isHandle) {
            this.isHandle = isHandle;
        }
    }
}
