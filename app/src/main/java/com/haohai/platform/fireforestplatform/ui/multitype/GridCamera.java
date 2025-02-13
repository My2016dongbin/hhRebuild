package com.haohai.platform.fireforestplatform.ui.multitype;

public class GridCamera {
    String name;
    String monitorId;
    String id;
    String rtspUrl;
    String subRtspUrl;
    String state;
    String collectionState;
    String isOnLine;//0 offline  1 online
    int cameraType;

    String deviceId;
    String mediaDeviceId;
    String controlDeviceId;

    public String getIsOnLine() {
        return isOnLine;
    }

    public void setIsOnLine(String isOnLine) {
        this.isOnLine = isOnLine;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMediaDeviceId() {
        return mediaDeviceId;
    }

    public void setMediaDeviceId(String mediaDeviceId) {
        this.mediaDeviceId = mediaDeviceId;
    }

    public String getControlDeviceId() {
        return controlDeviceId;
    }

    public void setControlDeviceId(String controlDeviceId) {
        this.controlDeviceId = controlDeviceId;
    }

    boolean status = false;

    public boolean isStatus() {
        return status;
    }

    public String getCollectionState() {
        return collectionState;
    }

    public void setCollectionState(String collectionState) {
        this.collectionState = collectionState;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRtspUrl() {
        return rtspUrl;
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }

    public String getSubRtspUrl() {
        return subRtspUrl;
    }

    public void setSubRtspUrl(String subRtspUrl) {
        this.subRtspUrl = subRtspUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getCameraType() {
        return cameraType;
    }

    public void setCameraType(int cameraType) {
        this.cameraType = cameraType;
    }

    @Override
    public String toString() {
        return "GridCamera{" +
                "name='" + name + '\'' +
                ", monitorId='" + monitorId + '\'' +
                ", id='" + id + '\'' +
                ", rtspUrl='" + rtspUrl + '\'' +
                ", subRtspUrl='" + subRtspUrl + '\'' +
                ", state='" + state + '\'' +
                ", collectionState='" + collectionState + '\'' +
                ", cameraType=" + cameraType +
                ", status=" + status +
                '}';
    }
}
