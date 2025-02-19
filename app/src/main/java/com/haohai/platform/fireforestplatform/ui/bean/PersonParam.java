package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/3/12.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class PersonParam {

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

    public PersonParam(String id,String taskId, String personId, String personName) {
        this.id = id;
        this.taskId = taskId;
        this.personId = personId;
        this.personName = personName;
    }

    public PersonParam() {
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
