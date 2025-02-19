package com.haohai.platform.fireforestplatform.ui.bean;

import android.net.Uri;

import java.util.Date;
import java.util.Random;

/**
 * Created by qc
 * on 2024/2/22.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class Picture {
    private String id;
    private String url;
    private Uri uri;

    public Picture(String url, Uri uri) {
        this.url = url;
        this.uri = uri;
        this.id = "pic_" +  new Random().nextInt(10000) + "_" + new Date().getTime();
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

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", uri=" + uri +
                '}';
    }
}
