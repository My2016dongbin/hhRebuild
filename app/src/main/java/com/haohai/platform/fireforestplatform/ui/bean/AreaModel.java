package com.haohai.platform.fireforestplatform.ui.bean;

import java.util.List;

public class AreaModel {
    String id;
    String parentId;
    String name;
    String no;
    List<AreaModel> children;

    public AreaModel(String name, String no) {
        this.name = name;
        this.no = no;
    }
    public AreaModel() {
    }

    public List<AreaModel> getChildren() {
        return children;
    }

    public void setChildren(List<AreaModel> children) {
        this.children = children;
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
        return "AreaModel{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", name='" + name + '\'' +
                ", no='" + no + '\'' +
                ", children='" + children + '\'' +
                '}';
    }
}
