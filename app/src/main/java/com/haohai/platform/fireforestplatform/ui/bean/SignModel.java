package com.haohai.platform.fireforestplatform.ui.bean;


public class SignModel {
    String afterWorkPlace;//下班的地点
    String attType;//考勤类型
    String cause;//填写相关问题原因
    String configId;//考勤配置id
    String createTime;//创建时间
    String createUser;//创建人
    String deptId;//部门id
    String earlyWorkTime;//早打卡配置时间
    String groupId;//组织id
    String id;//主键id
    String lowerName;//
    String nightWorkTime;//晚打卡配置时间
    String signInState;//签到状态
    String signInTime;//上班打卡时间
    String time;//可用来查看每日日期的员工考勤状态
    String updateTime;//更新时间
    String updateUser;//更新人
    String userId;//用户的id
    String weekTime;//时间和周几
    GeoPoint workPosition;//定位


    public String getAfterWorkPlace() {
        return afterWorkPlace;
    }

    public void setAfterWorkPlace(String afterWorkPlace) {
        this.afterWorkPlace = afterWorkPlace;
    }

    public String getAttType() {
        return attType;
    }

    public void setAttType(String attType) {
        this.attType = attType;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getEarlyWorkTime() {
        return earlyWorkTime;
    }

    public void setEarlyWorkTime(String earlyWorkTime) {
        this.earlyWorkTime = earlyWorkTime;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLowerName() {
        return lowerName;
    }

    public void setLowerName(String lowerName) {
        this.lowerName = lowerName;
    }

    public String getNightWorkTime() {
        return nightWorkTime;
    }

    public void setNightWorkTime(String nightWorkTime) {
        this.nightWorkTime = nightWorkTime;
    }

    public String getSignInState() {
        return signInState;
    }

    public void setSignInState(String signInState) {
        this.signInState = signInState;
    }

    public String getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(String signInTime) {
        this.signInTime = signInTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWeekTime() {
        return weekTime;
    }

    public void setWeekTime(String weekTime) {
        this.weekTime = weekTime;
    }

    public GeoPoint getWorkPosition() {
        return workPosition;
    }

    public void setWorkPosition(GeoPoint workPosition) {
        this.workPosition = workPosition;
    }
}
