package com.haohai.platform.fireforestplatform.ui.bean;

public class checkuser {

    public String planInResourceId;
    public int planInResourceType;
    public int type;
    public String userId;
    public String userName;

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

    public checkuser(String planInResourceId, int planInResourceType, int type, String userId, String userName) {
        this.planInResourceId = planInResourceId;
        this.planInResourceType = planInResourceType;
        this.type = type;
        this.userId = userId;
        this.userName = userName;
    }
}
