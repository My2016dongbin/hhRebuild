package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/1/24.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class FireTypePost {
    private int page;
    private int limit;
    private Dto dto;

    public FireTypePost() {
    }

    public FireTypePost(int page, int limit, Dto dto) {
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

    public static class Dto{
        private String name;
        private String type;

        public Dto() {
        }

        public Dto(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
