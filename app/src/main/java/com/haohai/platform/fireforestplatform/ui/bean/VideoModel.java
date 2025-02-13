package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2023/7/5.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class VideoModel {
    private String title;
    private String id;
    private String url;

    public VideoModel() {
    }

    public VideoModel(String title, String id, String url) {
        this.title = title;
        this.id = id;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "VideoModel{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
