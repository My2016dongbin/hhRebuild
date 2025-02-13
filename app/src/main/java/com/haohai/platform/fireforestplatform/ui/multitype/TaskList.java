package com.haohai.platform.fireforestplatform.ui.multitype;

import java.util.List;

/**
 * Created by qc
 * on 2023/8/1.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class TaskList {

    //("createUser")
    private String createUser;
    //("updateUser")
    private String updateUser;
    //("createTime")
    private String createTime;
    //("updateTime")
    private String updateTime;
    //("groupId")
    private String groupId;
    //("id")
    private String id;
    //("userId")
    private String userId;
    //("taskType")
    private String taskType;
    //("status")
    private String status;
    //("fireType")
    private String fireType;
    //("position")
    private PositionDTO position;
    //("taskContent")
    private String taskContent;
    //("taskImg")
    private String taskImg;
    //("taskMember")
    private String taskMember;
    //("taskStartTime")
    private String taskStartTime;
    //("taskEndTime")
    private String taskEndTime;
    //("priority")
    private String priority;
    //("ccId")
    private String ccId;
    //("duration")
    private String duration;
    //("deptId")
    private String deptId;
    //("operatorId")
    private String operatorId;
    //("operatorName")
    private String operatorName;
    //("description")
    private String description;
    //("reserve")
    private String reserve;
    //("fireId")
    private String fireId;
    //("taskRegion")
    private String taskRegion;
    //("memberName")
    private String memberName;
    //("fireEngine")
    private String fireEngine;
    //("peopleCount")
    private String peopleCount;
    //("fireEquipment")
    private String fireEquipment;
    //("isAutoDelegate")
    private String isAutoDelegate;
    //("gridNo")
    private String gridNo;
    private String roomId;

    private List<TaskDetailDTOList> taskDetailDTOList;

    public List<TaskDetailDTOList> getTaskDetailDTOList() {
        return taskDetailDTOList;
    }

    public void setTaskDetailDTOList(List<TaskDetailDTOList> taskDetailDTOList) {
        this.taskDetailDTOList = taskDetailDTOList;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public static class PositionDTO {
        //("lng")
        private String lng;
        //("lat")
        private String lat;

        public PositionDTO() {
        }

        public PositionDTO(String lng, String lat) {
            this.lng = lng;
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFireType() {
        return fireType;
    }

    public void setFireType(String fireType) {
        this.fireType = fireType;
    }

    public PositionDTO getPosition() {
        return position;
    }

    public void setPosition(PositionDTO position) {
        this.position = position;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public String getTaskImg() {
        return taskImg;
    }

    public void setTaskImg(String taskImg) {
        this.taskImg = taskImg;
    }

    public String getTaskMember() {
        return taskMember;
    }

    public void setTaskMember(String taskMember) {
        this.taskMember = taskMember;
    }

    public String getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(String taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public String getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(String taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCcId() {
        return ccId;
    }

    public void setCcId(String ccId) {
        this.ccId = ccId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public String getFireId() {
        return fireId;
    }

    public void setFireId(String fireId) {
        this.fireId = fireId;
    }

    public String getTaskRegion() {
        return taskRegion;
    }

    public void setTaskRegion(String taskRegion) {
        this.taskRegion = taskRegion;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getFireEngine() {
        return fireEngine;
    }

    public void setFireEngine(String fireEngine) {
        this.fireEngine = fireEngine;
    }

    public String getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(String peopleCount) {
        this.peopleCount = peopleCount;
    }

    public String getFireEquipment() {
        return fireEquipment;
    }

    public void setFireEquipment(String fireEquipment) {
        this.fireEquipment = fireEquipment;
    }

    public String getIsAutoDelegate() {
        return isAutoDelegate;
    }

    public void setIsAutoDelegate(String isAutoDelegate) {
        this.isAutoDelegate = isAutoDelegate;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }


    public static class TaskDetailDTOList{
        private String createUser;
        private String updateUser;
        private String createTime;
        private String updateTime;
        private String id;
        private String imgUrl;
        private String videoUrl;
        private String siteConditions;
        private String otherConditions;
        private String groupId;
        private String taskId;
        private String longitude;
        private String latitude;
        private String fireId;

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

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getSiteConditions() {
            return siteConditions;
        }

        public void setSiteConditions(String siteConditions) {
            this.siteConditions = siteConditions;
        }

        public String getOtherConditions() {
            return otherConditions;
        }

        public void setOtherConditions(String otherConditions) {
            this.otherConditions = otherConditions;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getFireId() {
            return fireId;
        }

        public void setFireId(String fireId) {
            this.fireId = fireId;
        }
    }
}
