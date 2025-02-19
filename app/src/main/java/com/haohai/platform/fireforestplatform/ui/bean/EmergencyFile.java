package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/3/20.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class EmergencyFile {

    public EmergencyFile() {
    }

    public EmergencyFile(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public EmergencyFile(String name, String storageName, String url) {
        this.name = name;
        this.storageName = storageName;
        this.url = url;
    }

    /**
     * delFlag : 0
     * createTime : 2024-03-15 10:20:17
     * updateTime : 2024-03-15 10:20:17
     * id : 1768462190475608064
     * name : 半吨兄弟.png
     * storageName : 半吨兄弟.png
     * url : http://117.132.5.139:16011/sdc-cloud-sso/parana/default/file/2024/03/15/ee303145fff34cbebbb42deaf61ffe26/半吨兄弟.png
     * incidentId : 1768462189020184576
     * incidentVersionId : 1768462189020184577
     */

    private String delFlag;
    private String createTime;
    private String updateTime;
    private String id;
    private String name;
    private String storageName;
    private String url;
    private String incidentId;
    private String incidentVersionId;

    private String fileName;
    private String fileStorageName;
    private String fileUrl;
    private String tag;

    public EmergencyFile(String fileName, String fileStorageName, String fileUrl, String tag) {
        this.fileName = fileName;
        this.fileStorageName = fileStorageName;
        this.fileUrl = fileUrl;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getIncidentVersionId() {
        return incidentVersionId;
    }

    public void setIncidentVersionId(String incidentVersionId) {
        this.incidentVersionId = incidentVersionId;
    }
}
