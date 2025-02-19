package com.haohai.platform.fireforestplatform.ui.multitype;

import java.util.List;

public class GridTrees {
    int tags;
    String id;
    String parentId;
    List<GridTrees> children;
    String name;
    String no;
    String count;
    String groupId;
    String cameraCount;
    String cameraOnlineCount;
    String level;
    String status;
    boolean last;
    List<GridModel> monitorDetailVOs;
    boolean checked;

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getTags() {
        return tags;
    }

    public void setTags(int tags) {
        this.tags = tags;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    boolean state = false;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCameraCount() {
        return cameraCount;
    }

    public void setCameraCount(String cameraCount) {
        this.cameraCount = cameraCount;
    }

    public String getCameraOnlineCount() {
        return cameraOnlineCount;
    }

    public void setCameraOnlineCount(String cameraOnlineCount) {
        this.cameraOnlineCount = cameraOnlineCount;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<GridModel> getMonitorDetailVOs() {
        return monitorDetailVOs;
    }

    public void setMonitorDetailVOs(List<GridModel> monitorDetailVOs) {
        this.monitorDetailVOs = monitorDetailVOs;
    }

    public List<GridTrees> getChildren() {
        return children;
    }

    public void setChildren(List<GridTrees> children) {
        this.children = children;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    @Override
    public String toString() {
        return "GridTrees{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", children=" + children +
                ", name='" + name + '\'' +
                ", no='" + no + '\'' +
                ", count='" + count + '\'' +
                ", groupId='" + groupId + '\'' +
                ", cameraCount='" + cameraCount + '\'' +
                ", cameraOnlineCount='" + cameraOnlineCount + '\'' +
                ", level='" + level + '\'' +
                ", status='" + status + '\'' +
                ", monitorDetailVOs=" + monitorDetailVOs +
                ", checked=" + checked +
                ", state=" + state +
                '}';
    }
}
