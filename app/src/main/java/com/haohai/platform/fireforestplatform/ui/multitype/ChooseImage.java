package com.haohai.platform.fireforestplatform.ui.multitype;

import android.net.Uri;

public class ChooseImage {
    public String uCheckId;

    public Uri uri;

    public boolean isAdd;

    public ChooseImage() {
    }

    public ChooseImage(String uCheckId, Uri uri, boolean isAdd) {
        this.uCheckId = uCheckId;
        this.uri = uri;
        this.isAdd = isAdd;
    }

    public String getuCheckId() {
        return uCheckId;
    }

    public void setuCheckId(String uCheckId) {
        this.uCheckId = uCheckId;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public boolean getAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    @Override
    public String toString() {
        return "ChooseImage{" +
                "uCheckId='" + uCheckId + '\'' +
                ", uri=" + uri +
                ", isAdd=" + isAdd +
                '}';
    }
}