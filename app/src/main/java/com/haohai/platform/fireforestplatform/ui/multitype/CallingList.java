package com.haohai.platform.fireforestplatform.ui.multitype;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class CallingList {
    private String fullName;
    private String phone;
    private String id;
    private String roleName;
    private String headUrl;
    private boolean state;

    public CallingList() {
    }

    public CallingList(String fullName, String phone, String id, String roleName, String headUrl, boolean state) {
        this.fullName = fullName;
        this.phone = phone;
        this.id = id;
        this.roleName = roleName;
        this.headUrl = headUrl;
        this.state = state;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
