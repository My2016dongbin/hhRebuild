package com.haohai.platform.fireforestplatform.ui.multitype;

/**
 * Created by qc
 * on 2023/8/1.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class Message {
    String createUser;
    String updateUser;
    String createTime;
    String updateTime;
    String groupId;
    String id;
    String name;
    String unreadCount;
    String gridNo;
    String type;

    public Message(String createUser, String updateUser, String createTime, String updateTime, String groupId, String id, String name, String unreadCount, String gridNo, String type) {
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.groupId = groupId;
        this.id = id;
        this.name = name;
        this.unreadCount = unreadCount;
        this.gridNo = gridNo;
        this.type = type;
    }

    public Message() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(String unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "createUser='" + createUser + '\'' +
                ", updateUser='" + updateUser + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", groupId='" + groupId + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", unreadCount='" + unreadCount + '\'' +
                ", gridNo='" + gridNo + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
