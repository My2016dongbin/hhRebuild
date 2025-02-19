package com.haohai.platform.fireforestplatform.ui.bean.request;

/**
 * Created by qc
 * on 2024/1/19.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class LoginBean {
    private String code;
    private String password;
    private String username;

    public LoginBean(String code, String password, String username) {
        this.code = code;
        this.password = password;
        this.username = username;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
