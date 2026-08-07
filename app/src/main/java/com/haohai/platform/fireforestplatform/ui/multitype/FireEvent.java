package com.haohai.platform.fireforestplatform.ui.multitype;

/**
 * Created by qc
 * on 2026/8/7.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class FireEvent {
    private String id;
    private String fireName;
    private String fireTime;
    private String reportTime;
    private String createTime;
    private String picPath1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFireName() {
        return fireName;
    }

    public void setFireName(String fireName) {
        this.fireName = fireName;
    }

    public String getFireTime() {
        return fireTime;
    }

    public void setFireTime(String fireTime) {
        this.fireTime = fireTime;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPicPath1() {
        return picPath1;
    }

    public void setPicPath1(String picPath1) {
        this.picPath1 = picPath1;
    }
}
