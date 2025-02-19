package com.haohai.platform.fireforestplatform.utils;

/**
 * Created by qc
 * on 2022/10/27.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class DahuaTokenInfoDemo {
    System system;
    String id;
    Params params;

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "DahuaToken{" +
                "system=" + system +
                ", id='" + id + '\'' +
                ", params=" + params +
                '}';
    }

    public static class System{
        String ver;
        String appId;
        String sign;
        long time;
        String nonce;

        public System() {
        }

        public System(String ver, String appId, String sign, long time, String nonce) {
            this.ver = ver;
            this.appId = appId;
            this.sign = sign;
            this.time = time;
            this.nonce = nonce;
        }

        public String getVer() {
            return ver;
        }

        public void setVer(String ver) {
            this.ver = ver;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }

        @Override
        public String toString() {
            return "System{" +
                    "ver='" + ver + '\'' +
                    ", appId='" + appId + '\'' +
                    ", sign='" + sign + '\'' +
                    ", time=" + time +
                    ", nonce='" + nonce + '\'' +
                    '}';
        }
    }
    public static class Params{
        String token;
        int page;
        int pageSize;
        String source;

        public Params() {
        }

        public Params(String token, int page, int pageSize, String source) {
            this.token = token;
            this.page = page;
            this.pageSize = pageSize;
            this.source = source;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        @Override
        public String toString() {
            return "Params{" +
                    "token='" + token + '\'' +
                    ", page=" + page +
                    ", pageSize=" + pageSize +
                    ", source='" + source + '\'' +
                    '}';
        }
    }

}
