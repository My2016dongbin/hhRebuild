package com.haohai.platform.fireforestplatform.ui.banner;

/**
 * Created by qc
 * on 2023/1/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class BannerBean {
    private String title;
    private String url;
    private String image;
    private String uri;
    private String id;

    public BannerBean(String id,String title, String url, String image, String uri) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.image = image;
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
