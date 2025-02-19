package com.haohai.platform.fireforestplatform.old.linyi;

public class LookImage {
    public String uCheckId;

    public String uri;

    public LookImage(String uCheckId, String uri) {
        this.uCheckId = uCheckId;
        this.uri = uri;
    }

    public LookImage() {
    }

    public String getuCheckId() {
        return uCheckId;
    }

    public void setuCheckId(String uCheckId) {
        this.uCheckId = uCheckId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
