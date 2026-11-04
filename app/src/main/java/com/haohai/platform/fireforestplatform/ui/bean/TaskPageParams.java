package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2026/8/7.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class TaskPageParams {
    private int limit;
    private int page;
    private Dto dto;

    public TaskPageParams(int limit, int page, Dto dto) {
        this.limit = limit;
        this.page = page;
        this.dto = dto;
    }

    public static class Dto {
        private String groupId;
        private String startTime;
        private String endTime;
        private String status;
        private String taskContent;

        public Dto(String groupId, String startTime, String endTime, String status, String taskContent) {
            this.groupId = groupId;
            this.startTime = startTime;
            this.endTime = endTime;
            this.status = status;
            this.taskContent = taskContent;
        }
    }
}
