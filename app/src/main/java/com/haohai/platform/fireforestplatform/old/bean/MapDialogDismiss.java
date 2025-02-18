package com.haohai.platform.fireforestplatform.old.bean;

public class MapDialogDismiss {
    public static MapDialogDismiss getInstance() {
        return new MapDialogDismiss();
    }

    private String type;
    private String name;
    private boolean add;
    public MapDialogDismiss() {
    }

    public MapDialogDismiss(String type) {
        this.type = type;
    }
    public MapDialogDismiss(String type,boolean add,String name) {
        this.type = type;
        this.add = add;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
