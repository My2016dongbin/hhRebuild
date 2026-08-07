package com.haohai.platform.fireforestplatform.ui.multitype;

/**
 * Created by qc
 * on 2026/8/3.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class DroneFire extends LandFire {

    private String deviceSn;
    private String eventCode;
    private String eventName;
    private String eventType;
    private String resource;
    private String gridName;

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getGridName() {
        return gridName;
    }

    public void setGridName(String gridName) {
        this.gridName = gridName;
    }
}
