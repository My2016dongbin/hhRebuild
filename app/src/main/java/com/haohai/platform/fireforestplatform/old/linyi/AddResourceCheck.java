package com.haohai.platform.fireforestplatform.old.linyi;

import java.util.List;

public class AddResourceCheck {
    public int checkType;
    public String description;
    public List<ImgsFirejd> imgs;
    public List<ItemsFirejd> items;
    public List<checkuser> checkusers;
    public String resourceId;
    public int status;
    public String endTime;
    public String startTime;
    public String name;
    public String parentGridNo;
    public String parentGridName;
    public String gridNo;
    public String gridName;
    public double longitude;
    public double latitude;
    public String resourceType;
    public String groupId;
    public String regulation;
    public AddResourceCheck() {
    }

    public AddResourceCheck(int checkType, String description, List<ImgsFirejd> imgs, List<ItemsFirejd> items, List<checkuser> checkusers, String resourceId, int status, String endTime, String startTime, String name, String parentGridNo, String parentGridName, String gridNo, String gridName, double longitude, double latitude, String resourceType, String groupId) {
        this.checkType = checkType;
        this.description = description;
        this.imgs = imgs;
        this.items = items;
        this.checkusers = checkusers;
        this.resourceId = resourceId;
        this.status = status;
        this.endTime = endTime;
        this.startTime = startTime;
        this.name = name;
        this.parentGridNo = parentGridNo;
        this.parentGridName = parentGridName;
        this.gridNo = gridNo;
        this.gridName = gridName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.resourceType = resourceType;
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentGridNo() {
        return parentGridNo;
    }

    public void setParentGridNo(String parentGridNo) {
        this.parentGridNo = parentGridNo;
    }

    public String getParentGridName() {
        return parentGridName;
    }

    public void setParentGridName(String parentGridName) {
        this.parentGridName = parentGridName;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getGridName() {
        return gridName;
    }

    public void setGridName(String gridName) {
        this.gridName = gridName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCheckType() {
        return checkType;
    }

    public void setCheckType(int checkType) {
        this.checkType = checkType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<checkuser> getCheckusers() {
        return checkusers;
    }

    public void setCheckusers(List<checkuser> checkusers) {
        this.checkusers = checkusers;
    }
    public String getRegulation() {
        return regulation;
    }

    public void setRegulation(String regulation) {
        this.regulation = regulation;
    }
    public static class ImgsFirejd {
        public ImgsFirejd(String img, int type) {
            this.img = img;
            this.type = type;
        }

        public String img;
        public int type;
    }

    public static class ItemsFirejd {
        public ItemsFirejd(String code, String planResourceId, int status, int type) {
            this.code = code;
            this.planResourceId = planResourceId;
            this.status = status;
            this.type = type;
        }

        public String code;
        public String planResourceId;
        public int status;
        public int type;
    }
}
