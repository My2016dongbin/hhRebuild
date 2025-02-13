package com.haohai.platform.fireforestplatform.ui.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class SnapModel {
    private JSONObject geometry;
    private String type;
    private Properties properties;

    public SnapModel() {
    }

    public SnapModel(JSONObject geometry, String type, Properties properties) {
        this.geometry = geometry;
        this.type = type;
        this.properties = properties;
    }

    public JSONObject getGeometry() {
        return geometry;
    }

    public void setGeometry(JSONObject geometry) {
        this.geometry = geometry;
    }

    /*public JSONObject getProperties() {
        return properties;
    }

    public void setProperties(JSONObject properties) {
        this.properties = properties;
    }*/
    /*public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }*/

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
        private JSONArray coordinates;
        //private List<List<Double>> coordinates;
        //private List<Double> coordinates;
        private String type;

        public Geometry() {
        }

        public JSONArray getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(JSONArray coordinates) {
            this.coordinates = coordinates;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
    public static class Properties{
        private String styleType;//line
        private String name;
        private Style style;
        private String id;
        private String type;

        public Properties() {
        }

        public String getStyleType() {
            return styleType;
        }

        public void setStyleType(String styleType) {
            this.styleType = styleType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Style getStyle() {
            return style;
        }

        public void setStyle(Style style) {
            this.style = style;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Style{
        private String color;
        private String materialType;
        private String width;
        private String clampToGround;

        //point
        private String image;
        private String verticalOrigin;
        private String horizontalOrigin;
        private Label label;
        private String opacity;

        public Style() {
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getVerticalOrigin() {
            return verticalOrigin;
        }

        public void setVerticalOrigin(String verticalOrigin) {
            this.verticalOrigin = verticalOrigin;
        }

        public String getHorizontalOrigin() {
            return horizontalOrigin;
        }

        public void setHorizontalOrigin(String horizontalOrigin) {
            this.horizontalOrigin = horizontalOrigin;
        }

        public Label getLabel() {
            return label;
        }

        public void setLabel(Label label) {
            this.label = label;
        }

        public String getOpacity() {
            return opacity;
        }

        public void setOpacity(String opacity) {
            this.opacity = opacity;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getMaterialType() {
            return materialType;
        }

        public void setMaterialType(String materialType) {
            this.materialType = materialType;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getClampToGround() {
            return clampToGround;
        }

        public void setClampToGround(String clampToGround) {
            this.clampToGround = clampToGround;
        }
    }

    public static class Label{
        private String outline;
        private String color;
        private String font_size;
        private String outlineColor;
        private String text;
        private String pixelOffsetY;

        public Label() {
        }

        public String getOutline() {
            return outline;
        }

        public void setOutline(String outline) {
            this.outline = outline;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getFont_size() {
            return font_size;
        }

        public void setFont_size(String font_size) {
            this.font_size = font_size;
        }

        public String getOutlineColor() {
            return outlineColor;
        }

        public void setOutlineColor(String outlineColor) {
            this.outlineColor = outlineColor;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getPixelOffsetY() {
            return pixelOffsetY;
        }

        public void setPixelOffsetY(String pixelOffsetY) {
            this.pixelOffsetY = pixelOffsetY;
        }
    }
}
