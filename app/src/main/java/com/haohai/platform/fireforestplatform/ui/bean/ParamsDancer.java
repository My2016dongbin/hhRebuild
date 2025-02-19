package com.haohai.platform.fireforestplatform.ui.bean;

import java.util.List;

/**
 * Created by qc
 * on 2024/3/8.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class ParamsDancer {
    private List<String> ids;
    private String riskType;

    public ParamsDancer(List<String> ids, String riskType) {
        this.ids = ids;
        this.riskType = riskType;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }
}
