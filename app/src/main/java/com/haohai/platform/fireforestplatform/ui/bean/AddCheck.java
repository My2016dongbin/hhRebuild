package com.haohai.platform.fireforestplatform.ui.bean;

import java.util.List;

public class AddCheck {
    public int checkType;
    public String description;
    public List<ImgsFirejd> imgs;
    public List<ItemsFirejd> items;
    public List<checkuser> checkusers;
    public String planResourceId;
    public int status;
    public String endTime;
    public String createUser;

    public AddCheck() {
    }

    public AddCheck(String description, List<ImgsFirejd> imgs, List<ItemsFirejd> items, String planResourceId) {
        this.description = description;
        this.imgs = imgs;
        this.items = items;
        this.planResourceId = planResourceId;
    }

    public AddCheck(int checkType, String description, List<ImgsFirejd> imgs, List<ItemsFirejd> items, List<checkuser> checkusers, String planResourceId, int status, String endTime) {
        this.checkType = checkType;
        this.description = description;
        this.imgs = imgs;
        this.items = items;
        this.checkusers = checkusers;
        this.planResourceId = planResourceId;
        this.status = status;
        this.endTime = endTime;
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

    public String getPlanResourceId() {
        return planResourceId;
    }

    public void setPlanResourceId(String planResourceId) {
        this.planResourceId = planResourceId;
    }
    public List<checkuser> getCheckusers() {
        return checkusers;
    }

    public void setCheckusers(List<checkuser> checkusers) {
        this.checkusers = checkusers;
    }
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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
