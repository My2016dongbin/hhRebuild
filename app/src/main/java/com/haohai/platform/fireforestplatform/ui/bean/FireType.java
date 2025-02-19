package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/1/24.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class FireType {

    /**
     * createUser : 城阳应急用户
     * updateUser : 城阳应急用户
     * createTime : 2024-01-08T15:02:10.000+0800
     * updateTime : 2024-01-08T15:02:14.000+0800
     * id : 0785f078-48ac-4fd5-867e-c319f64454b0
     * type : unreal_type
     * name : 误报类型
     * description : 太阳误报
     * value : 5
     * comment :
     * parentId : null
     * showCode : 1
     * subPublicCodes : null
     */

    private String createUser;
    private String updateUser;
    private String createTime;
    private String updateTime;
    private String id;
    private String type;
    private String name;
    private String description;
    private String value;
    private String comment;
    private String parentId;
    private String showCode;
    private String subPublicCodes;

    private boolean checked;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Object getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getShowCode() {
        return showCode;
    }

    public void setShowCode(String showCode) {
        this.showCode = showCode;
    }

    public Object getSubPublicCodes() {
        return subPublicCodes;
    }

    public void setSubPublicCodes(String subPublicCodes) {
        this.subPublicCodes = subPublicCodes;
    }
}
