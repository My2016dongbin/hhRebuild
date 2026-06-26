package com.haohai.platform.fireforestplatform.ui.multitype;

/**
 * Created by qc
 * on 2026/6/26.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class TutorialItem {
    public static final int TYPE_TITLE = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_IMAGE = 3;

    private int type;
    private String content;
    private int imageRes;

    public TutorialItem(int type, String content, int imageRes) {
        this.type = type;
        this.content = content;
        this.imageRes = imageRes;
    }

    public static TutorialItem title(String content) {
        return new TutorialItem(TYPE_TITLE, content, 0);
    }

    public static TutorialItem text(String content) {
        return new TutorialItem(TYPE_TEXT, content, 0);
    }

    public static TutorialItem image(int imageRes) {
        return new TutorialItem(TYPE_IMAGE, "", imageRes);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }
}
