package com.haohai.platform.fireforestplatform.ui.multitype;

import java.util.List;

/**
 * Created by qc
 * on 2023/9/25.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class RangerGrid {
    private boolean checked;
    private String id;
    private String parentId;
    private List<RangerGrid> children;
    private String name;
    private String no;
    private String count;
    private String onlineUsers;
    private String totalPeople;
    private List<Ranger> data;

    public RangerGrid() {
    }

    public List<Ranger> getData() {
        return data;
    }

    public void setData(List<Ranger> data) {
        this.data = data;
    }

    public String getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(String onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public String getTotalPeople() {
        return totalPeople;
    }

    public void setTotalPeople(String totalPeople) {
        this.totalPeople = totalPeople;
    }

    public static class Ranger{
        private String id;
        private String userCode;
        private String userPasswd;
        private String fullName;
        private String sex;
        private String gridNo;
        private String onlineState;

        public Ranger() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        public String getUserPasswd() {
            return userPasswd;
        }

        public void setUserPasswd(String userPasswd) {
            this.userPasswd = userPasswd;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getGridNo() {
            return gridNo;
        }

        public void setGridNo(String gridNo) {
            this.gridNo = gridNo;
        }

        public String getOnlineState() {
            return onlineState;
        }

        public void setOnlineState(String onlineState) {
            this.onlineState = onlineState;
        }
    }
    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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

    public List<RangerGrid> getChildren() {
        return children;
    }

    public void setChildren(List<RangerGrid> children) {
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

    @Override
    public String toString() {
        return "RangerGrid{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", children=" + children +
                ", name='" + name + '\'' +
                ", no='" + no + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
}
