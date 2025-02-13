package com.haohai.platform.fireforestplatform.ui.multitype;

import java.util.List;

/**
 * Created by qc
 * on 2023/9/4.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class MainDevice {
    private int index;
    private String id;
    private String name;
    private String gridNo;
    private String gridName;
    private String isOnline;
    private List<CameraDTOS> cameraDTOS;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public List<CameraDTOS> getCameraDTOS() {
        return cameraDTOS;
    }

    public void setCameraDTOS(List<CameraDTOS> cameraDTOS) {
        this.cameraDTOS = cameraDTOS;
    }

    public static class CameraDTOS{
        private String id;
        private String name;
        private String monitorId;
        private String controlDeviceId;

        public String getControlDeviceId() {
            return controlDeviceId;
        }

        public void setControlDeviceId(String controlDeviceId) {
            this.controlDeviceId = controlDeviceId;
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

        public String getMonitorId() {
            return monitorId;
        }

        public void setMonitorId(String monitorId) {
            this.monitorId = monitorId;
        }

    }
}
