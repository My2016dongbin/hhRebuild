package com.haohai.platform.fireforestplatform.old.linyi;

import org.json.JSONObject;

public class Resource {
    public String id;
    public String name;
    public double lat;
    public double lng;
    public boolean isChoose;
    public double distance;
    public String type;
    public String code;
    public String apiUrl;
    public String gridId;
    public String gridName;
    public String gridNo;
    public String districtNo;
    public String resourceName;
    public String groupId;
    public String address;
    public JSONObject obj;


    public String resourceId;
    public String resourceType;
    public double latitude;
    public double longitude;
    public String parentGridName;
    public String parentGridNo;
    public Position position;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
/*  public Resource(String id, String name, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.isChoose = false;
        this.distance = 0;
    }*/

    public JSONObject getObj() {
        return obj;
    }

    public void setObj(JSONObject obj) {
        this.obj = obj;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrictNo() {
        return districtNo;
    }

    public void setDistrictNo(String districtNo) {
        this.districtNo = districtNo;
    }

    public Resource(String id, String name, double lng , double lat, String type, String code, String apiUrl, String gridId, String gridName, String gridNo, String resourceName, String groupId) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.code = code;
        this.apiUrl = apiUrl;
        this.gridId = gridId;
        this.gridName = gridName;
        this.gridNo = gridNo;
        this.resourceName = resourceName;
        this.isChoose = false;
        this.distance = 0;
        this.groupId = groupId;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getGridId() {
        return gridId;
    }

    public void setGridId(String gridId) {
        this.gridId = gridId;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", isChoose=" + isChoose +
                ", distance=" + distance +
                ", type='" + type + '\'' +
                ", code='" + code + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                ", gridId='" + gridId + '\'' +
                ", gridName='" + gridName + '\'' +
                ", gridNo='" + gridNo + '\'' +
                ", districtNo='" + districtNo + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", groupId='" + groupId + '\'' +
                ", address='" + address + '\'' +
                ", obj=" + obj +
                ", resourceId='" + resourceId + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", parentGridName='" + parentGridName + '\'' +
                ", parentGridNo='" + parentGridNo + '\'' +
                '}';
    }

    public static class Position{
        private double lat;
        private double lng;

        public Position(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }
}