package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/3/12.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class File {

    /**
     * remark :
     * gridNo :
     * groupNo :
     * deptNo :
     * delFlag : 0
     * createBy :
     * createTime :
     * updateBy :
     * updateTime :
     * params :
     * id : 1
     * taskId : 1
     * reportId :
     * fileType : create
     * fileName : 自然灾害.png
     * fileStorageName : 自然灾害.png
     * fileUrl : http://117.132.5.139:16011/sdc-cloud-sso/parana/default/file/2023/11/08/961047be143e4d15a9bc541be1b28abb/自然灾害.png
     */

    private String remark;
    private String gridNo;
    private String groupNo;
    private String deptNo;
    private String delFlag;
    private String createBy;
    private String createTime;
    private String updateBy;
    private String updateTime;
    private String params;
    private String id;
    private String taskId;
    private String reportId;
    private String fileType;
    private String fileName;
    private String fileStorageName;
    private String fileUrl;

    public File(String taskId, String fileName, String fileStorageName, String fileUrl) {
        this.taskId = taskId;
        this.fileName = fileName;
        this.fileStorageName = fileStorageName;
        this.fileUrl = fileUrl;
    }

    public File() {
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileStorageName() {
        return fileStorageName;
    }

    public void setFileStorageName(String fileStorageName) {
        this.fileStorageName = fileStorageName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
