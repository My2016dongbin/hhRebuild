package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/2/22.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class TypeBean {
    private String id;
    private String title;
    private String code;
    private boolean checked;

    public TypeBean(String title) {
        this.title = title;
    }

    public TypeBean(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
