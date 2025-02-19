package com.haohai.platform.fireforestplatform.old.linyi;

/**
 * Created by qc
 * on 2022/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class Around {
    private Position position;
    private int distance;

    public Around(Position position, int distance) {
        this.position = position;
        this.distance = distance;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public static class Position {
        private double lat;
        private double lng;

        public Position(double lat, double lng) {
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
}
