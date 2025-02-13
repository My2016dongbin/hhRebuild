package com.haohai.platform.fireforestplatform.ui.multitype;

import java.util.ArrayList;
import java.util.List;

public class SheQu {
    private Geometry geometry;
    private String type;
    private Properties properties;
    private boolean checked;

    public SheQu() {
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public static class Geometry{
        private List<ArrayList<Double>> coordinates;
        private String type;

        public Geometry() {
        }

        public Geometry(List<ArrayList<Double>> coordinates, String type) {
            this.coordinates = coordinates;
            this.type = type;
        }

        public List<ArrayList<Double>> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<ArrayList<Double>> coordinates) {
            this.coordinates = coordinates;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Geometry{" +
                    "coordinates=" + coordinates +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
    public static class Properties{
        private String ORIG_FID;
        private String Shape_Area;
        private String Shape_Leng;
        private String UserID;
        private String phone;
        private String gridNo;
        private String gridType;
        private String gridMian;
        private String gridName;
        private String gridZhi;
        private String gridDan;
        private String person;

        public Properties() {
        }

        public Properties(String ORIG_FID, String shape_Area, String shape_Leng, String userID, String phone, String gridNo, String gridType, String gridMian, String gridName, String gridZhi, String gridDan, String person) {
            this.ORIG_FID = ORIG_FID;
            Shape_Area = shape_Area;
            Shape_Leng = shape_Leng;
            UserID = userID;
            this.phone = phone;
            this.gridNo = gridNo;
            this.gridType = gridType;
            this.gridMian = gridMian;
            this.gridName = gridName;
            this.gridZhi = gridZhi;
            this.gridDan = gridDan;
            this.person = person;
        }

        public String getORIG_FID() {
            return ORIG_FID;
        }

        public void setORIG_FID(String ORIG_FID) {
            this.ORIG_FID = ORIG_FID;
        }

        public String getShape_Area() {
            return Shape_Area;
        }

        public void setShape_Area(String shape_Area) {
            Shape_Area = shape_Area;
        }

        public String getShape_Leng() {
            return Shape_Leng;
        }

        public void setShape_Leng(String shape_Leng) {
            Shape_Leng = shape_Leng;
        }

        public String getUserID() {
            return UserID;
        }

        public void setUserID(String userID) {
            UserID = userID;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getGridNo() {
            return gridNo;
        }

        public void setGridNo(String gridNo) {
            this.gridNo = gridNo;
        }

        public String getGridType() {
            return gridType;
        }

        public void setGridType(String gridType) {
            this.gridType = gridType;
        }

        public String getGridMian() {
            return gridMian;
        }

        public void setGridMian(String gridMian) {
            this.gridMian = gridMian;
        }

        public String getGridName() {
            return gridName;
        }

        public void setGridName(String gridName) {
            this.gridName = gridName;
        }

        public String getGridZhi() {
            return gridZhi;
        }

        public void setGridZhi(String gridZhi) {
            this.gridZhi = gridZhi;
        }

        public String getGridDan() {
            return gridDan;
        }

        public void setGridDan(String gridDan) {
            this.gridDan = gridDan;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        @Override
        public String toString() {
            return "Properties{" +
                    "ORIG_FID='" + ORIG_FID + '\'' +
                    ", Shape_Area='" + Shape_Area + '\'' +
                    ", Shape_Leng='" + Shape_Leng + '\'' +
                    ", UserID='" + UserID + '\'' +
                    ", phone='" + phone + '\'' +
                    ", gridNo='" + gridNo + '\'' +
                    ", gridType='" + gridType + '\'' +
                    ", gridMian='" + gridMian + '\'' +
                    ", gridName='" + gridName + '\'' +
                    ", gridZhi='" + gridZhi + '\'' +
                    ", gridDan='" + gridDan + '\'' +
                    ", person='" + person + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SheQu{" +
                "geometry=" + geometry +
                ", type='" + type + '\'' +
                ", properties=" + properties +
                ", checked=" + checked +
                '}';
    }
}
