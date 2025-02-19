package com.haohai.platform.fireforestplatform.ui.bean;

import java.util.List;

public class AddZhengzhi {

    public String regulation;
    public List<ImgsFirejd> imgs;
    public String planResourceId;
    public String userName;
    public int userType;
    public AddZhengzhi() {
    }

    public AddZhengzhi(String regulation, List<ImgsFirejd> imgs, String planResourceId) {
        this.regulation = regulation;
        this.imgs = imgs;
        this.planResourceId = planResourceId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getRegulation() {
        return regulation;
    }

    public void setRegulation(String regulation) {
        this.regulation = regulation;
    }

    public List<ImgsFirejd> getImgs() {
        return imgs;
    }

    public void setImgs(List<ImgsFirejd> imgs) {
        this.imgs = imgs;
    }
    public String getPlanResourceId() {
        return planResourceId;
    }

    public void setPlanResourceId(String planResourceId) {
        this.planResourceId = planResourceId;
    }
    public static class ImgsFirejd {
        public ImgsFirejd(String img, int type) {
            this.img = img;
            this.type = type;
        }

        public String img;
        public int type;
    }
}
