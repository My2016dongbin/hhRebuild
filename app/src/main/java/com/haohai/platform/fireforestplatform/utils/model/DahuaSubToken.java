package com.haohai.platform.fireforestplatform.utils.model;

/**
 * Created by qc
 * on 2022/10/27.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class DahuaSubToken {
    System system;
    String id;
    Params params;

    public DahuaSubToken() {
    }

    public DahuaSubToken(System system, String id, Params params) {
        this.system = system;
        this.id = id;
        this.params = params;
    }

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
        return "DahuaTokenInfo{" +
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
        String openid;

        public Params() {
        }

        public Params(String token, String openid) {
            this.token = token;
            this.openid = openid;
        }

        @Override
        public String toString() {
            return "Params{" +
                    "token='" + token + '\'' +
                    ", openid='" + openid + '\'' +
                    '}';
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }
    }

}
