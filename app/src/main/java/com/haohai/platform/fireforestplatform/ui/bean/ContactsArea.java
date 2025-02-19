package com.haohai.platform.fireforestplatform.ui.bean;

import java.util.List;

/**
 * Created by qc
 * on 2024/6/19.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class ContactsArea {

    /**
     * no : 66128685-1
     * label : 市北区人民政府
     * flag : unit
     * gridNo : 370203
     * disabled : false
     * remark : 市北区人民政府
     */

    private boolean selected;
    private String no;
    private String label;
    private String flag;
    private String gridNo;
    private boolean disabled;
    private String remark;
    private List<ContactsArea> children;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<ContactsArea> getChildren() {
        return children;
    }

    public void setChildren(List<ContactsArea> children) {
        this.children = children;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
