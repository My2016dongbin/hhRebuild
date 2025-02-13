package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2023/7/23.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class Dto {
    public int isReal;

    public Dto(int isReal) {
        this.isReal = isReal;
    }

    public int isReal() {
        return isReal;
    }

    public void setReal(int real) {
        isReal = real;
    }

    @Override
    public String toString() {
        return "{" +
                "isReal:" + isReal +
                '}';
    }
}
