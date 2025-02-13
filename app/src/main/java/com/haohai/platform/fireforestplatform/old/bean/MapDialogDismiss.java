package com.haohai.platform.fireforestplatform.old.bean;

public class MapDialogDismiss {
    public static MapDialogDismiss getInstance() {
        return new MapDialogDismiss();
    }

    private String type;
    public MapDialogDismiss() {
    }

    public MapDialogDismiss(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
