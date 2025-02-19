package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/1/12.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class VersionBean {
    private int page;
    private int limit;
    private DTO dto;

    public VersionBean(int page, int limit, DTO dto) {
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

    public DTO getDto() {
        return dto;
    }

    public void setDto(DTO dto) {
        this.dto = dto;
    }

    public static class DTO{
        private String version;
        private String groupId;
        private String versionName;
        private String flag;

        public DTO(String version, String groupId, String versionName, String flag) {
            this.version = version;
            this.groupId = groupId;
            this.versionName = versionName;
            this.flag = flag;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }
    }
}
