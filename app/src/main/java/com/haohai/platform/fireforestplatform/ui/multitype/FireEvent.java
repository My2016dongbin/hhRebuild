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
    private String picPath2;
    private String videoPath1;
    private String videoPath2;
    private String content;

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

    public String getPicPath2() {
        return picPath2;
    }

    public void setPicPath2(String picPath2) {
        this.picPath2 = picPath2;
    }

    public String getVideoPath1() {
        return videoPath1;
    }

    public void setVideoPath1(String videoPath1) {
        this.videoPath1 = videoPath1;
    }

    public String getVideoPath2() {
        return videoPath2;
    }

    public void setVideoPath2(String videoPath2) {
        this.videoPath2 = videoPath2;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
