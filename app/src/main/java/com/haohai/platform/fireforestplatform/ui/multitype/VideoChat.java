package com.haohai.platform.fireforestplatform.ui.multitype;

/**
 * Created by qc
 * on 2023/8/1.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class VideoChat {
    long id;
    String header;
    String name;
    boolean audio;
    boolean video;

    public VideoChat(long id, String header, String name, boolean audio, boolean video) {
        this.id = id;
        this.header = header;
        this.name = name;
        this.audio = audio;
        this.video = video;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAudio() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio = audio;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }
}
