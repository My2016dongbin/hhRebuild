package com.haohai.platform.fireforestplatform.event;

/**
 * Created by qc
 * on 2022/12/13.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class Update {
    private String apkUrl;
    private long fileSize;
    private int prodVersionCode;
    private String prodVersionName;
    private String md5Check;
    private int forceUpdateFlag;
    private String updateLog;

    public Update(String apkUrl, long fileSize, int prodVersionCode, String prodVersionName, String md5Check, int forceUpdateFlag, String updateLog) {
        this.apkUrl = apkUrl;
        this.fileSize = fileSize;
        this.prodVersionCode = prodVersionCode;
        this.prodVersionName = prodVersionName;
        this.md5Check = md5Check;
        this.forceUpdateFlag = forceUpdateFlag;
        this.updateLog = updateLog;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getProdVersionCode() {
        return prodVersionCode;
    }

    public void setProdVersionCode(int prodVersionCode) {
        this.prodVersionCode = prodVersionCode;
    }

    public String getProdVersionName() {
        return prodVersionName;
    }

    public void setProdVersionName(String prodVersionName) {
        this.prodVersionName = prodVersionName;
    }

    public String getMd5Check() {
        return md5Check;
    }

    public void setMd5Check(String md5Check) {
        this.md5Check = md5Check;
    }

    public int getForceUpdateFlag() {
        return forceUpdateFlag;
    }

    public void setForceUpdateFlag(int forceUpdateFlag) {
        this.forceUpdateFlag = forceUpdateFlag;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }
}
