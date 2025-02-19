package com.haohai.platform.fireforestplatform.ui.multitype;

import java.util.List;

/**
 * Created by qc
 * on 2023/8/1.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class TaskList {

    /**
     * remark :
     * gridNo :
     * groupNo :
     * deptNo :
     * delFlag : 0
     * createBy : haohai
     * createTime : 2024-02-01 17:00:24
     * updateBy : haohai
     * updateTime : 2024-02-01 17:00:27
     * params :
     * id : 1
     * taskName : ceshi1
     * taskStartTime : 2024-02-01 16:57:59
     * taskEndTime : 2024-02-02 16:57:59
     * incidentId : 1727344469096923136
     * taskStatus : wait
     * taskDesc : miaoshu
     * longtitude :
     * latitude :
     * incidentType :
     * incidentTypeName :
     * taskFileList : null
     * reportDTOList : null
     * personDTOList : [{"remark":"","gridNo":"","groupNo":"","deptNo":"","delFlag":"","createBy":"","createTime":"","updateBy":"","updateTime":"","params":"","id":"1","taskId":"1","personId":"1717355458860257282","personName":"凌寒希"}]
     * ids : null
     * sortField :
     * sortOrder :
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
    private String taskName;
    private String taskStartTime;
    private String taskEndTime;
    private String incidentId;
    private String taskStatus;
    private String taskDesc;
    private String longtitude;
    private String latitude;
    private String incidentType;
    private String incidentTypeName;
    private List<TaskFileListBean> taskFileList;
    private List<ReportDTOListBean> reportDTOList;
    private Object ids;
    private String sortField;
    private String sortOrder;
    private List<PersonDTOListBean> personDTOList;

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

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public String getIncidentTypeName() {
        return incidentTypeName;
    }

    public void setIncidentTypeName(String incidentTypeName) {
        this.incidentTypeName = incidentTypeName;
    }

    public List<TaskFileListBean> getTaskFileList() {
        return taskFileList;
    }

    public void setTaskFileList(List<TaskFileListBean> taskFileList) {
        this.taskFileList = taskFileList;
    }

    public List<ReportDTOListBean> getReportDTOList() {
        return reportDTOList;
    }

    public void setReportDTOList(List<ReportDTOListBean> reportDTOList) {
        this.reportDTOList = reportDTOList;
    }

    public Object getIds() {
        return ids;
    }

    public void setIds(Object ids) {
        this.ids = ids;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<PersonDTOListBean> getPersonDTOList() {
        return personDTOList;
    }

    public void setPersonDTOList(List<PersonDTOListBean> personDTOList) {
        this.personDTOList = personDTOList;
    }

    public static class TaskFileListBean {
        private String fileName;
        private String fileStorageName;
        private String fileType;
        private String id;
        private String taskId;
        private String reportId;
        private String fileUrl;

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

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
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

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }
    public static class ReportDTOListBean {
        private String reportPersonName;
        private String reportTime;
        private String reportOrder;
        private String reportDesc;
        private List<TaskFileListBean> reportFileList;

        public String getReportPersonName() {
            return reportPersonName;
        }

        public void setReportPersonName(String reportPersonName) {
            this.reportPersonName = reportPersonName;
        }

        public String getReportTime() {
            return reportTime;
        }

        public void setReportTime(String reportTime) {
            this.reportTime = reportTime;
        }

        public String getReportOrder() {
            return reportOrder;
        }

        public void setReportOrder(String reportOrder) {
            this.reportOrder = reportOrder;
        }

        public String getReportDesc() {
            return reportDesc;
        }

        public void setReportDesc(String reportDesc) {
            this.reportDesc = reportDesc;
        }

        public List<TaskFileListBean> getReportFileList() {
            return reportFileList;
        }

        public void setReportFileList(List<TaskFileListBean> reportFileList) {
            this.reportFileList = reportFileList;
        }
    }
    public static class PersonDTOListBean {
        /**
         * remark :
         * gridNo :
         * groupNo :
         * deptNo :
         * delFlag :
         * createBy :
         * createTime :
         * updateBy :
         * updateTime :
         * params :
         * id : 1
         * taskId : 1
         * personId : 1717355458860257282
         * personName : 凌寒希
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
        private String personId;
        private String personName;

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

        public String getPersonId() {
            return personId;
        }

        public void setPersonId(String personId) {
            this.personId = personId;
        }

        public String getPersonName() {
            return personName;
        }

        public void setPersonName(String personName) {
            this.personName = personName;
        }
    }
}
