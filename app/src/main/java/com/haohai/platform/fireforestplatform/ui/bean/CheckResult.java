package com.haohai.platform.fireforestplatform.ui.bean;

import android.net.Uri;

import java.util.List;

/**
 * Created by qc
 * on 2024/5/17.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class CheckResult {
    private List<FileModel> fileList;
    private String remark;

    public CheckResult() {
    }

    public CheckResult(List<FileModel> fileList, String remark) {
        this.fileList = fileList;
        this.remark = remark;
    }

    public List<FileModel> getFileList() {
        return fileList;
    }

    public void setFileList(List<FileModel> fileList) {
        this.fileList = fileList;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static class FileModel{
        private String url;
        private Uri uri;

        public FileModel() {
        }

        public FileModel(String url, Uri uri) {
            this.url = url;
            this.uri = uri;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Uri getUri() {
            return uri;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }
    }
}
