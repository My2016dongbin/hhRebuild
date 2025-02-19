package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/3/8.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class ExamineBean {
    private String incidentId;
    private String auditResult;
    private String auditOpinion;

    public ExamineBean(String incidentId, String auditResult, String auditOpinion) {
        this.incidentId = incidentId;
        this.auditResult = auditResult;
        this.auditOpinion = auditOpinion;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }
}
