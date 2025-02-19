package com.haohai.platform.fireforestplatform.event;
public class VideoStream {
    private String url;
    private boolean isRtsp;
    private int index;

    public VideoStream(String url, boolean isRtsp) {
        this.url = url;
        this.isRtsp = isRtsp;
    }

    public VideoStream(String url, boolean isRtsp, int index) {
        this.url = url;
        this.isRtsp = isRtsp;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public VideoStream(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isRtsp() {
        return isRtsp;
    }

    public void setRtsp(boolean rtsp) {
        isRtsp = rtsp;
    }
}
