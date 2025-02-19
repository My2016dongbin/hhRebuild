package com.haohai.platform.fireforestplatform.ui.multitype;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class MainFgMenu {
    private String id;
    private String code;
    private String title;
    private String image;
    private int res;
    private String route;
    private int index;

    public MainFgMenu() {
    }

    public MainFgMenu(String id, String code, String title, String image, int res, String route, int index) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.image = image;
        this.res = res;
        this.route = route;
        this.index = index;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "MainFgMenu{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", res=" + res +
                ", route='" + route + '\'' +
                ", index=" + index +
                '}';
    }
}
