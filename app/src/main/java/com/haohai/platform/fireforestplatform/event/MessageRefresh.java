package com.haohai.platform.fireforestplatform.event;

/**
 * Created by qc
 * on 2023/8/2.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class MessageRefresh {
    private int count;

    public MessageRefresh(int count) {
        this.count = count;
    }

    public MessageRefresh() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
