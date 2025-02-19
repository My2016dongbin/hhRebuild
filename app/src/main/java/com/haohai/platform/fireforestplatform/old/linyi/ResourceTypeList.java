package com.haohai.platform.fireforestplatform.old.linyi;

public class ResourceTypeList {

    public String id;
    public String name;
    public String code;
    public String isDisplay;
    public int totalCount;
    public int useCount;
    public Object unuseCount;
    public String apiUrl;
    public Object checkApiUrl;
    public Object textColor;
    public String iconFile;
    public Object description;
    public String groupId;
    public String state;

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

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public Object getUnuseCount() {
        return unuseCount;
    }

    public void setUnuseCount(Object unuseCount) {
        this.unuseCount = unuseCount;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public Object getCheckApiUrl() {
        return checkApiUrl;
    }

    public void setCheckApiUrl(Object checkApiUrl) {
        this.checkApiUrl = checkApiUrl;
    }

    public Object getTextColor() {
        return textColor;
    }

    public void setTextColor(Object textColor) {
        this.textColor = textColor;
    }

    public String getIconFile() {
        return iconFile;
    }

    public void setIconFile(String iconFile) {
        this.iconFile = iconFile;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
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

    public ResourceTypeList(String id, String name, String code, String isDisplay, int totalCount, int useCount, Object unuseCount, String apiUrl, Object checkApiUrl, Object textColor, String iconFile, Object description, String groupId, String state) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.isDisplay = isDisplay;
        this.totalCount = totalCount;
        this.useCount = useCount;
        this.unuseCount = unuseCount;
        this.apiUrl = apiUrl;
        this.checkApiUrl = checkApiUrl;
        this.textColor = textColor;
        this.iconFile = iconFile;
        this.description = description;
        this.groupId = groupId;
        this.state = state;
    }
}
