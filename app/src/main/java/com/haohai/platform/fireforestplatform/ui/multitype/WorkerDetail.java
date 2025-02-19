package com.haohai.platform.fireforestplatform.ui.multitype;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qc
 * on 2024/1/29.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class WorkerDetail {
    private String date;
    private String id;
    private List<Worker> list;
    private String gridName;
    private String gridNo;
    private String groupId;

    public void appendList(Worker worker){
        if(list == null) {
            list = new ArrayList<>();
        }
        list.add(worker);
    }

    public WorkerDetail() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Worker> getList() {
        return list;
    }

    public void setList(List<Worker> list) {
        this.list = list;
    }

    public String getGridName() {
        return gridName;
    }

    public void setGridName(String gridName) {
        this.gridName = gridName;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public static class Worker{
        private String id;
        private String gridName;
        private String gridNo;
        private String groupNo;

        private String dutyDate;
        private String postId;
        private String classManagentId;
        private String arrangeName;
        private String arrangePhone;
        private String staffId;

        public Worker() {
        }

        public String getDutyDate() {
            return dutyDate;
        }

        public void setDutyDate(String dutyDate) {
            this.dutyDate = dutyDate;
        }

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public String getClassManagentId() {
            return classManagentId;
        }

        public void setClassManagentId(String classManagentId) {
            this.classManagentId = classManagentId;
        }

        public String getArrangeName() {
            return arrangeName;
        }

        public void setArrangeName(String arrangeName) {
            this.arrangeName = arrangeName;
        }

        public String getArrangePhone() {
            return arrangePhone;
        }

        public void setArrangePhone(String arrangePhone) {
            this.arrangePhone = arrangePhone;
        }

        public String getStaffId() {
            return staffId;
        }

        public void setStaffId(String staffId) {
            this.staffId = staffId;
        }

        public String getGroupNo() {
            return groupNo;
        }

        public void setGroupNo(String groupNo) {
            this.groupNo = groupNo;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGridName() {
            return gridName;
        }

        public void setGridName(String gridName) {
            this.gridName = gridName;
        }

        public String getGridNo() {
            return gridNo;
        }

        public void setGridNo(String gridNo) {
            this.gridNo = gridNo;
        }

        @Override
        public String toString() {
            return "Worker{" +
                    "id='" + id +
                    ", dutyDate='[" + dutyDate + "]'" +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "WorkerDetail{" +
                ", id='" + id  +
                ", list=" + list +
                '}';
    }
}
