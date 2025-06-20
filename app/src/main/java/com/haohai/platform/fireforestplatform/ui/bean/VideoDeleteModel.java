package com.haohai.platform.fireforestplatform.ui.bean;
public class VideoDeleteModel {
    private int index;
    private String monitorId;
    private String channelId;
    private String deviceId;
    private String serial;

    public VideoDeleteModel(int index, String monitorId, String channelId, String deviceId, String serial) {
        this.index = index;
        this.monitorId = monitorId;
        this.channelId = channelId;
        this.deviceId = deviceId;
        this.serial = serial;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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
