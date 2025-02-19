package com.haohai.platform.fireforestplatform.ui.bean;

import org.json.JSONObject;

/**
 * Created by qc
 * on 2023/9/22.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class Resource {

    private String apiUrl;
    private JSONObject obj;
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
    //("resourceType")
    private String resourceType;
    //("name")
    private String name;
    private String resourceName;
    //("position")
    private PositionDTO position;
    //("address")
    private String address;
    //("districtNo")
    private String districtNo;
    //("districtName")
    private String districtName;
    //("streetNo")
    private String streetNo;
    //("streetName")
    private String streetName;
    //("gridId")
    private String gridId;
    //("gridNo")
    private String gridNo;
    //("gridName")
    private String gridName;
    //("leaderId")
    private String leaderId;
    //("leaderName")
    private String leaderName;
    //("leaderPhone")
    private String leaderPhone;
    //("description")
    private String description;
    //("groupId")
    private String groupId;
    //("state")
    private String state;
    //("distance")
    private String distance;
    //("picture")
    private String picture;
    //("typeName")
    private String typeName;
    //("provinceCode")
    private String provinceCode;
    //("provinceName")
    private String provinceName;
    //("cityCode")
    private String cityCode;
    //("cityName")
    private String cityName;
    //("countyCode")
    private String countyCode;
    //("countyName")
    private String countyName;
    //("villageCode")
    private String villageCode;
    //("villageName")
    private String villageName;
    //("gridNoLike")
    private String gridNoLike;
    //("gridNos")
    private String gridNos;
    //("type")
    private String type;
    //("textColor")
    private String textColor;
    //("iconFile")
    private String iconFile;
    //("otherPic")
    private String otherPic;
    //("checkState")
    private String checkState;

    public JSONObject getObj() {
        return obj;
    }

    public void setObj(JSONObject obj) {
        this.obj = obj;
    }

    public Resource() {
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
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

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PositionDTO getPosition() {
        return position;
    }

    public void setPosition(PositionDTO position) {
        this.position = position;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getDistrictNo() {
        return districtNo;
    }

    public void setDistrictNo(String districtNo) {
        this.districtNo = districtNo;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getStreetNo() {
        return streetNo;
    }

    public void setStreetNo(String streetNo) {
        this.streetNo = streetNo;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getGridId() {
        return gridId;
    }

    public void setGridId(String gridId) {
        this.gridId = gridId;
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

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getLeaderPhone() {
        return leaderPhone;
    }

    public void setLeaderPhone(String leaderPhone) {
        this.leaderPhone = leaderPhone;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getVillageCode() {
        return villageCode;
    }

    public void setVillageCode(String villageCode) {
        this.villageCode = villageCode;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getGridNoLike() {
        return gridNoLike;
    }

    public void setGridNoLike(String gridNoLike) {
        this.gridNoLike = gridNoLike;
    }

    public String getGridNos() {
        return gridNos;
    }

    public void setGridNos(String gridNos) {
        this.gridNos = gridNos;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getOtherPic() {
        return otherPic;
    }

    public void setOtherPic(String otherPic) {
        this.otherPic = otherPic;
    }

    public String getCheckState() {
        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
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

    @Override
    public String toString() {
        return "Resource{" +
                "apiUrl='" + apiUrl + '\'' +
                ", obj=" + obj +
                ", createUser='" + createUser + '\'' +
                ", updateUser='" + updateUser + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", id='" + id + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", address='" + address + '\'' +
                ", districtNo='" + districtNo + '\'' +
                ", districtName='" + districtName + '\'' +
                ", streetNo='" + streetNo + '\'' +
                ", streetName='" + streetName + '\'' +
                ", gridId='" + gridId + '\'' +
                ", gridNo='" + gridNo + '\'' +
                ", gridName='" + gridName + '\'' +
                ", leaderId='" + leaderId + '\'' +
                ", leaderName='" + leaderName + '\'' +
                ", leaderPhone='" + leaderPhone + '\'' +
                ", description='" + description + '\'' +
                ", groupId='" + groupId + '\'' +
                ", state='" + state + '\'' +
                ", distance='" + distance + '\'' +
                ", picture='" + picture + '\'' +
                ", typeName='" + typeName + '\'' +
                ", provinceCode='" + provinceCode + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", cityName='" + cityName + '\'' +
                ", countyCode='" + countyCode + '\'' +
                ", countyName='" + countyName + '\'' +
                ", villageCode='" + villageCode + '\'' +
                ", villageName='" + villageName + '\'' +
                ", gridNoLike='" + gridNoLike + '\'' +
                ", gridNos='" + gridNos + '\'' +
                ", type='" + type + '\'' +
                ", textColor='" + textColor + '\'' +
                ", iconFile='" + iconFile + '\'' +
                ", otherPic='" + otherPic + '\'' +
                ", checkState='" + checkState + '\'' +
                '}';
    }
}
