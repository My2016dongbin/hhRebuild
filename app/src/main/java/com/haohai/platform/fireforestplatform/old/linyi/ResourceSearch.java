package com.haohai.platform.fireforestplatform.old.linyi;

import java.util.List;

public class ResourceSearch {

    public int checkType;
    public List<CheckuserVOSFirejd> checkusers;
    public String createTime;
    public String createUser;
    public String description;
    public String endTime;
    public String gridName;
    public String gridNo;
    public String groupId;
    public String historyId;
    public String id;
    public List<ImgsFirejd> imgs;
    public List<ItemsFirejd> items;
    public double latitude;
    public double longitude;
    public String name;
    public String parentGridName;
    public String parentGridNo;
    public String planId;
    public String resourceId;
    public String resourceType;
    public int sequence;
    public String startTime;
    public int status;
    public int statusCount;
    public String userId;
    public int userType;

    public ResourceSearch(int checkType, List<CheckuserVOSFirejd> checkuserVOS, String createTime, String createUser, String description, String endTime, String gridName, String gridNo, String groupId, String historyId, String id, List<ImgsFirejd> imgs, List<ItemsFirejd> items, double latitude, double longitude, String name, String parentGridName, String parentGridNo, String planId, String resourceId, String resourceType, int sequence, String startTime, int status, int statusCount, String userId, int userType) {
        this.checkType = checkType;
        this.checkusers = checkusers;
        this.createTime = createTime;
        this.createUser = createUser;
        this.description = description;
        this.endTime = endTime;
        this.gridName = gridName;
        this.gridNo = gridNo;
        this.groupId = groupId;
        this.historyId = historyId;
        this.id = id;
        this.imgs = imgs;
        this.items = items;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.parentGridName = parentGridName;
        this.parentGridNo = parentGridNo;
        this.planId = planId;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.sequence = sequence;
        this.startTime = startTime;
        this.status = status;
        this.statusCount = statusCount;
        this.userId = userId;
        this.userType = userType;
    }

    public List<CheckuserVOSFirejd> getCheckusers() {
        return checkusers;
    }

    public void setCheckusers(List<CheckuserVOSFirejd> checkusers) {
        this.checkusers = checkusers;
    }

    public int getCheckType() {
        return checkType;
    }

    public void setCheckType(int checkType) {
        this.checkType = checkType;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ImgsFirejd> getImgs() {
        return imgs;
    }

    public void setImgs(List<ImgsFirejd> imgs) {
        this.imgs = imgs;
    }

    public List<ItemsFirejd> getItems() {
        return items;
    }

    public void setItems(List<ItemsFirejd> items) {
        this.items = items;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentGridName() {
        return parentGridName;
    }

    public void setParentGridName(String parentGridName) {
        this.parentGridName = parentGridName;
    }

    public String getParentGridNo() {
        return parentGridNo;
    }

    public void setParentGridNo(String parentGridNo) {
        this.parentGridNo = parentGridNo;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatusCount() {
        return statusCount;
    }

    public void setStatusCount(int statusCount) {
        this.statusCount = statusCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public static class CheckuserVOSFirejd {
        public String groupId;
        public String id;
        public String planInResourceId;
        public int planInResourceType;
        public int type;
        public String userId;
        public String userName;

        public CheckuserVOSFirejd(String groupId, String id, String planInResourceId, int planInResourceType, int type, String userId, String userName) {
            this.groupId = groupId;
            this.id = id;
            this.planInResourceId = planInResourceId;
            this.planInResourceType = planInResourceType;
            this.type = type;
            this.userId = userId;
            this.userName = userName;
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

    public static class ImgsFirejd {
        public String groupId;
        public String id;
        public String img;
        public String regulation;
        public int type;

        public ImgsFirejd(String groupId, String id, String img, String regulation, int type) {
            this.groupId = groupId;
            this.id = id;
            this.img = img;
            this.regulation = regulation;
            this.type = type;
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

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getRegulation() {
            return regulation;
        }

        public void setRegulation(String regulation) {
            this.regulation = regulation;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public static class ItemsFirejd {
        public String code;
        public int count;
        public String createTime;
        public String createUser;
        public String description;
        public String groupId;
        public String id;
        public int level;
        public String parentId;
        public String planResourceId;
        public String regulation;
        public int status;
        public int type;

        public ItemsFirejd(String code, int count, String createTime, String createUser, String description, String groupId, String id, int level, String parentId, String planResourceId, String regulation, int status, int type) {
            this.code = code;
            this.count = count;
            this.createTime = createTime;
            this.createUser = createUser;
            this.description = description;
            this.groupId = groupId;
            this.id = id;
            this.level = level;
            this.parentId = parentId;
            this.planResourceId = planResourceId;
            this.regulation = regulation;
            this.status = status;
            this.type = type;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getPlanResourceId() {
            return planResourceId;
        }

        public void setPlanResourceId(String planResourceId) {
            this.planResourceId = planResourceId;
        }

        public String getRegulation() {
            return regulation;
        }

        public void setRegulation(String regulation) {
            this.regulation = regulation;
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
    }
}
