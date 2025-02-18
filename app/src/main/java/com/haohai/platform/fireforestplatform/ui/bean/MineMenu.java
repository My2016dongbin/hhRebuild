package com.haohai.platform.fireforestplatform.ui.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by qc
 * on 2023/6/2.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class MineMenu {
    private Drawable resId;//"@drawable/icon_manage"
    private String title;
    private String content;
    private boolean showLeft;
    private boolean showRight;

    public MineMenu() {
    }

    public MineMenu(Drawable resId, String title, String content, boolean showLeft, boolean showRight) {
        this.resId = resId;
        this.title = title;
        this.content = content;
        this.showLeft = showLeft;
        this.showRight = showRight;
    }

    public Drawable getResId() {
        return resId;
    }

    public void setResId(Drawable resId) {
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isShowLeft() {
        return showLeft;
    }

    public void setShowLeft(boolean showLeft) {
        this.showLeft = showLeft;
    }

    public boolean isShowRight() {
        return showRight;
    }

    public void setShowRight(boolean showRight) {
        this.showRight = showRight;
    }

    @Override
    public String toString() {
        return "MineMenu{" +
                "resId=" + resId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", showLeft=" + showLeft +
                ", showRight=" + showRight +
                '}';
    }
}
