package com.haohai.platform.fireforestplatform.event;

/**
 * Created by qc
 * on 2023/7/24.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class MainTabChange {
    private int index;
    private String type;
    private String id;

    public MainTabChange() {
    }

    public MainTabChange(int index, String type) {
        this.index = index;
        this.type = type;
    }

    public MainTabChange(int index, String type,String id) {
        this.index = index;
        this.type = type;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MainTabChange(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
