package com.haohai.platform.fireforestplatform.ui.multitype;

/**
 * Created by qc
 * on 2023/9/4.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class ResourceType {

    private boolean checked;
    //("createUser")
    private String createUser;
    //("updateUser")
    private String updateUser;
    //("createTime")
    private String createTime;
    //("updateTime")
    private String updateTime;
    //("id")
    private String id;
    //("name")
    private String name;
    //("code")
    private String code;
    //("isDisplay")
    private String isDisplay;
    //("totalCount")
    private String totalCount;
    //("useCount")
    private String useCount;
    //("unuseCount")
    private String unuseCount;
    //("apiUrl")
    private String apiUrl;
    //("checkApiUrl")
    private String checkApiUrl;
    //("textColor")
    private String textColor;
    //("iconFile")
    private String iconFile;
    //("description")
    private String description;
    //("groupId")
    private String groupId;
    //("state")
    private String state;

    public ResourceType() {
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIsDisplay() {
        return isDisplay;
    }

    public void setIsDisplay(String isDisplay) {
        this.isDisplay = isDisplay;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getUseCount() {
        return useCount;
    }

    public void setUseCount(String useCount) {
        this.useCount = useCount;
    }

    public String getUnuseCount() {
        return unuseCount;
    }

    public void setUnuseCount(String unuseCount) {
        this.unuseCount = unuseCount;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getCheckApiUrl() {
        return checkApiUrl;
    }

    public void setCheckApiUrl(String checkApiUrl) {
        this.checkApiUrl = checkApiUrl;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getIconFile() {
        return iconFile;
    }

    public void setIconFile(String iconFile) {
        this.iconFile = iconFile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "ResourceType{" +
                "checked=" + checked +
                ", createUser='" + createUser + '\'' +
                ", updateUser='" + updateUser + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", isDisplay='" + isDisplay + '\'' +
                ", totalCount='" + totalCount + '\'' +
                ", useCount='" + useCount + '\'' +
                ", unuseCount='" + unuseCount + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                ", checkApiUrl='" + checkApiUrl + '\'' +
                ", textColor='" + textColor + '\'' +
                ", iconFile='" + iconFile + '\'' +
                ", description='" + description + '\'' +
                ", groupId='" + groupId + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
