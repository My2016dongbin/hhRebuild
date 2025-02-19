package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2022/9/2.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class PostStar {
    String cameraId;
    String cameraName;
    String id;
    String monitorId;

    public PostStar(String cameraId, String cameraName, String id, String monitorId) {
        this.cameraId = cameraId;
        this.cameraName = cameraName;
        this.id = id;
        this.monitorId = monitorId;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }
}
