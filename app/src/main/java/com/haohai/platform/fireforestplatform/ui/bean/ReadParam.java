package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/3/12.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class ReadParam {
    private String id;
    private String readStatus;

    public ReadParam(String id, String readStatus) {
        this.id = id;
        this.readStatus = readStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }
}
