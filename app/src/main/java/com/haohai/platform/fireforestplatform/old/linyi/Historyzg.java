package com.haohai.platform.fireforestplatform.old.linyi;

public class Historyzg {
    public String description;
    public String imgurl;
    public String id;

    public int cishu;
    public Historyzg() {
    }

    public Historyzg(String description, String imgurl, String id, int cishu) {
        this.description = description;
        this.imgurl = imgurl;
        this.id = id;
        this.cishu = cishu;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
    public int getCishu() {
        return cishu;
    }

    public void setCishu(int cishu) {
        this.cishu = cishu;
    }
}
