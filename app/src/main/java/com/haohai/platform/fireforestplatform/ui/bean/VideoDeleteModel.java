package com.haohai.platform.fireforestplatform.ui.bean;
public class VideoDeleteModel {
    private int index;
    private String monitorId;
    private String channelId;

    public VideoDeleteModel(int index, String monitorId, String channelId) {
        this.index = index;
        this.monitorId = monitorId;
        this.channelId = channelId;
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
