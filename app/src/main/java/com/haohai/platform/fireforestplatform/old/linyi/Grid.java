package com.haohai.platform.fireforestplatform.old.linyi;

import com.google.gson.annotations.SerializedName;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.ArrayList;
import java.util.List;

@Table(name = "grid")
public class Grid {

    /**
     * 主键
     */
    @Column(name = "id",isId = true,autoGen = false)
    private String id;

    //状态 无需持久化
    private boolean status = false;
    //子item 无需持久化
    private List<Grid> children = new ArrayList<>();
    //item资源列表 无需持久化
    private List<ResourceList> resourceList = new ArrayList<>();

    @Column(name = "createuser")
    private String createUser;

    @Column(name = "updateuser")
    private String updateUser;

    @Column(name = "createtime")
    private String createTime;

    @Column(name = "updatetime")
    private String updateTime;

    @Column(name = "resourcetype")
    private String resourceType;

    @Column(name = "groupid")
    private String groupId;

    @Column(name = "parentid")
    private String parentId;

    @Column(name = "name")
    private String name;

    @Column(name = "parentname")
    private String parentName;

    @Column(name = "parentno")
    private String parentNo;

    @Column(name = "gridno")
    private String gridNo;

    @Column(name = "level")
    private String level;
    @SerializedName("position")
    public PositionFirejd positionX;


/*    @Column(name = "streetno")
    private String streetNo;

    @Column(name = "streetname")
    private String streetName;*/

    @Column(name = "state")
    private String state;

    @Column(name = "remark")
    private String remark;

    public Grid(String id, String createUser, String updateUser, String createTime, String updateTime, String resourceType, String groupId, String parentId, String name, String parentName, String parentNo, String gridNo, String level, PositionFirejd positionX, String state, String remark) {
        this.id = id;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.resourceType = resourceType;
        this.groupId = groupId;
        this.parentId = parentId;
        this.name = name;
        this.parentName = parentName;
        this.parentNo = parentNo;
        this.gridNo = gridNo;
        this.level = level;
        this.positionX = positionX;
        this.state = state;
        this.remark = remark;
    }

    public Grid() {
    }

    public List<ResourceList> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<ResourceList> resourceList) {
        this.resourceList = resourceList;
    }

    public List<Grid> getChildren() {
        return children;
    }

    public void setChildren(List<Grid> children) {
        this.children = children;
    }

    public PositionFirejd getPositionX() {
        return positionX;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setPositionX(PositionFirejd positionX) {
        this.positionX = positionX;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getGroupId() {
        return groupId;
    }



    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentNo() {
        return parentNo;
    }

    public void setParentNo(String parentNo) {
        this.parentNo = parentNo;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static class PositionFirejd {
        public double lng;
        public double lat;

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public PositionFirejd(double lng, double lat) {
            this.lng = lng;
            this.lat = lat;
        }
    }

    @Override
    public String toString() {
        return "Grid{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", children=" + children +
                ", resourceList=" + resourceList +
                ", createUser='" + createUser + '\'' +
                ", updateUser='" + updateUser + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", groupId='" + groupId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", name='" + name + '\'' +
                ", parentName='" + parentName + '\'' +
                ", parentNo='" + parentNo + '\'' +
                ", gridNo='" + gridNo + '\'' +
                ", level='" + level + '\'' +
                ", positionX=" + positionX +
                ", state='" + state + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
