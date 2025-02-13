package com.haohai.platform.fireforestplatform.old.bean;

import java.util.List;

/**
 * Created by qc
 * on 2023/1/5.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class Tree {
    private String id;
    private String parentId;
    private List<Tree> children;
    private List<Person> personList;
    private String name;
    private String no;
    private String count;
    private int level;//当前递归层级
    private boolean status;//当前选择状态

    public Tree() {
    }

    public Tree(String id, String parentId, List<Tree> children, String name, String no, String count) {
        this.id = id;
        this.parentId = parentId;
        this.children = children;
        this.name = name;
        this.no = no;
        this.count = count;
    }

    @Override
    public String toString() {
        return "Tree{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", children=" + children +
                ", personList=" + personList +
                ", name='" + name + '\'' +
                ", no='" + no + '\'' +
                ", count='" + count + '\'' +
                ", level='" + level + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public List<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public List<Tree> getChildren() {
        return children;
    }

    public void setChildren(List<Tree> children) {
        this.children = children;
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

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
