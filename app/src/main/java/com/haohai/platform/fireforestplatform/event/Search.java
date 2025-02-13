package com.haohai.platform.fireforestplatform.event;

/**
 * Created by qc
 * on 2024/12/25.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class Search {
    double lat;
    double lng;

    public Search(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
