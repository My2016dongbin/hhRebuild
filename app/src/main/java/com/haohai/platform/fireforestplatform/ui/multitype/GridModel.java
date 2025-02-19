package com.haohai.platform.fireforestplatform.ui.multitype;

import com.haohai.platform.fireforestplatform.ui.bean.GridMonitor;

import java.util.List;

public class GridModel {
    GridMonitor monitor;
    List<GridCamera> cameraList;

    boolean checked = false;
    boolean status = false;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public GridModel(GridMonitor monitor, List<GridCamera> cameraList) {
        this.monitor = monitor;
        this.cameraList = cameraList;
    }

    public GridModel() {
    }

    public GridMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(GridMonitor monitor) {
        this.monitor = monitor;
    }

    public List<GridCamera> getCameraList() {
        return cameraList;
    }

    public void setCameraList(List<GridCamera> cameraList) {
        this.cameraList = cameraList;
    }

    @Override
    public String toString() {
        return "GridModel{" +
                "monitor=" + monitor +
                ", cameraList=" + cameraList +
                ", checked=" + checked +
                ", status=" + status +
                '}';
    }
}
