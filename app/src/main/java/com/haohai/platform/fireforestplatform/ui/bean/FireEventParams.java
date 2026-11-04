package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2026/8/7.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class FireEventParams {
    private int limit;
    private int page;
    private Dto dto;

    public FireEventParams(int limit, int page, Dto dto) {
        this.limit = limit;
        this.page = page;
        this.dto = dto;
    }

    public static class Dto {
        private String cityName;
        private String cityCode;
        private String countyName;
        private String countyCode;
        private String address;
        private String status;

        public Dto(String cityName, String cityCode, String countyName, String countyCode, String address, String status) {
            this.cityName = cityName;
            this.cityCode = cityCode;
            this.countyName = countyName;
            this.countyCode = countyCode;
            this.address = address;
            this.status = status;
        }
    }
}
