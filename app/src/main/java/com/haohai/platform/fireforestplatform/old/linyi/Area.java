package com.haohai.platform.fireforestplatform.old.linyi;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by geyang on 2020/7/8.
 */
@Table(name = "area")
public class Area {
    @Column(name = "id",isId = true,autoGen = false)
    public String id;

    @Column(name = "name")
    public String Name;

    @Column(name = "parentid")
    public String ParentId;

    @Column(name = "createtime")
    public String createTime;

    @Column(name = "gridNo")
    public String gridNo;

    @Column(name = "level")
    public String level;   //0国家  1省 2市 3县

    public Area() {
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public Area(String id, String name, String parentId, String createTime, String level) {
        this.id = id;
        Name = name;
        ParentId = parentId;
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
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
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
}
