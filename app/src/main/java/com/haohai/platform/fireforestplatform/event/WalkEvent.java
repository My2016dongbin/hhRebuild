package com.haohai.platform.fireforestplatform.event;

/**
 * Created by qc
 * on 2023/9/20.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class WalkEvent {
    private String distance;

    public WalkEvent() {
    }

    public WalkEvent(String distance) {
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
