package com.haohai.platform.fireforestplatform.ui.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "area")
public class Area {
    @Column(name = "id",isId = true,autoGen = false)
    public String id;

    @Column(name = "name")
    public String name;

    @Column(name = "parentId")
    public String parentId;

    @Column(name = "createTime")
    public String createTime;

    @Column(name = "level")
    public String level;   //0国家  1省 2市 3县

    @Column(name = "areaLevel")
    public String areaLevel;   //0国家  1省 2市 3县

    public Area() {
    }

    public String getAreaLevel() {
        return areaLevel;
    }

    public void setAreaLevel(String areaLevel) {
        this.areaLevel = areaLevel;
    }

    public Area(String id, String name, String parentId, String createTime, String level) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.createTime = createTime;
        this.level = level;
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Area{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", parentId='" + parentId + '\'' +
                ", createTime='" + createTime + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
