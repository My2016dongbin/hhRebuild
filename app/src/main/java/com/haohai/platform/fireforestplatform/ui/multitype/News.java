package com.haohai.platform.fireforestplatform.ui.multitype;

/**
 * Created by qc
 * on 2023/8/1.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class News {
    String id;
    String newsName;
    String newsContent;
    String pic;
    String sendUser;
    String sendTime;
    String groupId;
    String state;
    String status;
    String createUser;
    String updateUser;
    String createTime;
    String updateTime;
    int page;
    int limit;
    Dto dto;

    public News(String id, String newsName, String newsContent, String pic, String sendUser, String sendTime, String groupId, String state, String status, String createUser, String updateUser, String createTime, String updateTime) {
        this.id = id;
        this.newsName = newsName;
        this.newsContent = newsContent;
        this.pic = pic;
        this.sendUser = sendUser;
        this.sendTime = sendTime;
        this.groupId = groupId;
        this.state = state;
        this.status = status;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public News(String groupId) {
        this.groupId = groupId;
    }

    public Dto getDto() {
        return dto;
    }

    public News(String groupId, int page, int limit, Dto dto) {
        this.groupId = groupId;
        this.page = page;
        this.limit = limit;
        this.dto = dto;
    }

    public void setDto(Dto dto) {
        this.dto = dto;
    }

    public News(String groupId, int page, int limit) {
        this.groupId = groupId;
        this.page = page;
        this.limit = limit;
    }

    public News() {
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewsName() {
        return newsName;
    }

    public void setNewsName(String newsName) {
        this.newsName = newsName;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public static class Dto{
        private String id;

        public Dto() {
        }

        public Dto(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
    @Override
    public String toString() {
        return "News{" +
                "id='" + id + '\'' +
                ", newsName='" + newsName + '\'' +
                ", newsContent='" + newsContent + '\'' +
                ", pic='" + pic + '\'' +
                ", sendUser='" + sendUser + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", groupId='" + groupId + '\'' +
                ", state='" + state + '\'' +
                ", status='" + status + '\'' +
                ", createUser='" + createUser + '\'' +
                ", updateUser='" + updateUser + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
