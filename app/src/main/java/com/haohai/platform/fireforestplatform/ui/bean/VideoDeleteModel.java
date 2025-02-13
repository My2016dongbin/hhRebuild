package com.haohai.platform.fireforestplatform.ui.bean;
public class VideoDeleteModel {
    private int index;
    private String monitorId;
    private String channelId;
    private String deviceId;

    public VideoDeleteModel(int index, String monitorId, String channelId) {
        this.index = index;
        this.monitorId = monitorId;
        this.channelId = channelId;
    }

    public VideoDeleteModel(int index, String monitorId, String channelId, String deviceId) {
        this.index = index;
        this.monitorId = monitorId;
        this.channelId = channelId;
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
