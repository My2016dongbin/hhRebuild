package com.haohai.platform.fireforestplatform.ui.bean;

import java.util.List;

/**
 * Created by qc
 * on 2024/8/20.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class ComprehensiveSubmit {
    private int checkStationCount;
    private String checkUserName;
    private List<User> checkuserVOS;
    private String description;
    private String startTime;
    private String endTime;
    private String gridName;
    private String gridNo;
    private String name;
    private int status;
    private int type;
    private List<CheckResource> planResourceDTOS;

    public int getCheckStationCount() {
        return checkStationCount;
    }

    public void setCheckStationCount(int checkStationCount) {
        this.checkStationCount = checkStationCount;
    }

    public String getCheckUserName() {
        return checkUserName;
    }

    public void setCheckUserName(String checkUserName) {
        this.checkUserName = checkUserName;
    }

    public List<User> getCheckuserVOS() {
        return checkuserVOS;
    }

    public void setCheckuserVOS(List<User> checkuserVOS) {
        this.checkuserVOS = checkuserVOS;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getGridName() {
        return gridName;
    }

    public void setGridName(String gridName) {
        this.gridName = gridName;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<CheckResource> getPlanResourceDTOS() {
        return planResourceDTOS;
    }

    public void setPlanResourceDTOS(List<CheckResource> planResourceDTOS) {
        this.planResourceDTOS = planResourceDTOS;
    }

    public static class User{
        private String planInResourceId;
        private int planInResourceType;//3
        private int type;//2
        private String userId;
        private String userName;

        public String getPlanInResourceId() {
            return planInResourceId;
        }

        public void setPlanInResourceId(String planInResourceId) {
            this.planInResourceId = planInResourceId;
        }

        public int getPlanInResourceType() {
            return planInResourceType;
        }

        public void setPlanInResourceType(int planInResourceType) {
            this.planInResourceType = planInResourceType;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
