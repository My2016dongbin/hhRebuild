package com.haohai.platform.fireforestplatform.ui.cell;

/**
 * Created by qc
 * on 2024/5/23.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class PostCheck {

    /**
     * algorithmAlarmId : f83ee3f4-9bba-4bdf-af89-423ede0f181c
     * content : 这是督导意见
     */

    private String algorithmAlarmId;
    private String content;

    public PostCheck(String algorithmAlarmId, String content) {
        this.algorithmAlarmId = algorithmAlarmId;
        this.content = content;
    }

    public PostCheck() {
    }

    public String getAlgorithmAlarmId() {
        return algorithmAlarmId;
    }

    public void setAlgorithmAlarmId(String algorithmAlarmId) {
        this.algorithmAlarmId = algorithmAlarmId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
