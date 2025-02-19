package com.haohai.platform.fireforestplatform.ui.bean;

import java.util.List;

/**
 * Created by qc
 * on 2024/8/15.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class CheckResource {

    /**
     * checkType : 2
     * description : 存在问题
     * imgs : [{"img":"http://117.132.5.139:10081/group1/M00/00/0D/wKgBCGbCmpCEIvINAAAAANVqzcY052.png","type":1}]
     * latitude : 34.79123
     * longitude : 118.08341
     * name : 测试-队伍103
     * resourceType : team
     * status : 4
     */

    private int checkType;
    private String description;
    private double latitude;
    private double longitude;
    private String name;
    private String resourceType;
    private int status;
    private List<ImgsBean> imgs;

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

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ImgsBean> getImgs() {
        return imgs;
    }

    public void setImgs(List<ImgsBean> imgs) {
        this.imgs = imgs;
    }

    public static class ImgsBean {
        /**
         * img : http://117.132.5.139:10081/group1/M00/00/0D/wKgBCGbCmpCEIvINAAAAANVqzcY052.png
         * type : 1
         */

        private String img;
        private int type;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
