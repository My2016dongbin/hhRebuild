package com.haohai.platform.fireforestplatform.event;

/**
 * Created by qc
 * on 2023/9/15.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class SettingEvent {
    private String type;
    private boolean value;

    public SettingEvent(String type, boolean value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
