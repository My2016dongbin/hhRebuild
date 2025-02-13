package com.haohai.platform.fireforestplatform.event;

/**
 * Created by qc
 * on 2023/8/1.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class MessageChange {
    private String message;

    public MessageChange(String message) {
        this.message = message;
    }

    public MessageChange() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
