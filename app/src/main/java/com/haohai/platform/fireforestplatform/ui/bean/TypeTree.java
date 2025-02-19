package com.haohai.platform.fireforestplatform.ui.bean;

import java.util.List;

/**
 * Created by qc
 * on 2024/3/22.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class TypeTree {

    /**
     * remark :
     * gridNo : 370203
     * groupNo : 7a338510-9968_a011a3b1-70f2
     * deptNo :
     * delFlag : 0
     * createBy : haohai
     * createTime : 2024-03-11 14:03:20
     * updateBy : haohai
     * updateTime : 2024-03-11 14:08:37
     * params :
     * id : 1767068768946749440
     * parentId : 1721766875651768320
     * root : false
     * typeName : 干旱灾害
     * typeValue : droughtDisasters
     * typeCode : 0101
     * typeLevel : 0
     * sort : 1
     * judgeCode : Common
     * judgeName : 通用
     * children : null
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
    private String parentId;
    private String root;
    private String typeName;
    private String typeValue;
    private String typeCode;
    private String typeLevel;
    private String sort;
    private String judgeCode;
    private String judgeName;
    private List<TypeTree> children;

    private boolean checked;

    public TypeTree() {
    }

    public TypeTree(String id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeLevel() {
        return typeLevel;
    }

    public void setTypeLevel(String typeLevel) {
        this.typeLevel = typeLevel;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getJudgeCode() {
        return judgeCode;
    }

    public void setJudgeCode(String judgeCode) {
        this.judgeCode = judgeCode;
    }

    public String getJudgeName() {
        return judgeName;
    }

    public void setJudgeName(String judgeName) {
        this.judgeName = judgeName;
    }

    public List<TypeTree> getChildren() {
        return children;
    }

    public void setChildren(List<TypeTree> children) {
        this.children = children;
    }
}
