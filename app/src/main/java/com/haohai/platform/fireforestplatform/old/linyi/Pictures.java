package com.haohai.platform.fireforestplatform.old.linyi;

import android.net.Uri;

public class Pictures {
    private Uri uri;
    private String url;
    private int type;

    public Pictures() {
    }

    public int getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
