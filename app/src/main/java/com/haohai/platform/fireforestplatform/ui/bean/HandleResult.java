package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/5/17.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class HandleResult {
    private boolean isReal;
    private String status;
    private String remark;

    public HandleResult() {
    }

    public HandleResult(boolean isReal, String status, String remark) {
        this.isReal = isReal;
        this.status = status;
        this.remark = remark;
    }

    public boolean isReal() {
        return isReal;
    }

    public void setReal(boolean real) {
        isReal = real;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
