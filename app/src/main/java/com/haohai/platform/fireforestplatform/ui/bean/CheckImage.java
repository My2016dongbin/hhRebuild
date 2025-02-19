package com.haohai.platform.fireforestplatform.ui.bean;

import android.net.Uri;

/**
 * Created by qc
 * on 2024/8/15.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class CheckImage {
    private String url;
    private Uri uri;
    private String id;
    private String name;

    public CheckImage() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
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
}
