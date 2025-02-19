package com.haohai.platform.fireforestplatform.ui.bean;

import java.util.List;

/**
 * Created by qc
 * on 2024/3/12.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class LiveUpload {

    /**
     * taskName : ceshi2
     * taskStartTime : 2024-02-01 16:57:59
     * taskEndTime : 2024-02-02 16:57:59
     * incidentId : 1727344469096923136
     * taskStatus : wait
     * taskDesc : miaoshu2
     */

    private String taskName;
    private String taskId;
    private String taskStartTime;
    private String taskEndTime;
    private String incidentId;
    private String taskStatus;
    private String taskDesc;
    private List<PersonParam> personDTOList;
    private List<File> reportFileList;

    public List<PersonParam> getPersonDTOList() {
        return personDTOList;
    }

    public void setPersonDTOList(List<PersonParam> personDTOList) {
        this.personDTOList = personDTOList;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<File> getReportFileList() {
        return reportFileList;
    }

    public void setReportFileList(List<File> reportFileList) {
        this.reportFileList = reportFileList;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(String taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public String getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(String taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }
}
