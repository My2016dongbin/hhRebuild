package com.haohai.platform.fireforestplatform.ui.multitype;

/**
 * Created by qc
 * on 2024/1/3.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class Grid {
    private String id;
    private String name;
    private String gridNo;
    private String gridLevel;
    private Position position;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getGridLevel() {
        return gridLevel;
    }

    public void setGridLevel(String gridLevel) {
        this.gridLevel = gridLevel;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public static class Position{
        private String lng;
        private String lat;

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }
    }
}
