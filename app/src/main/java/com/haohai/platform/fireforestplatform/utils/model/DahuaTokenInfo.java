package com.haohai.platform.fireforestplatform.utils.model;

import java.util.List;

/**
 * Created by qc
 * on 2022/10/27.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class DahuaTokenInfo {
    System system;
    String id;
    Params params;

    public DahuaTokenInfo() {
    }

    public DahuaTokenInfo(System system, String id, Params params) {
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
        List<Device> deviceList;
        boolean onlyDevice;
        String token;

        public Params() {
        }

        public Params(List<Device> deviceList, boolean onlyDevice, String token) {
            this.deviceList = deviceList;
            this.onlyDevice = onlyDevice;
            this.token = token;
        }

        public List<Device> getDeviceList() {
            return deviceList;
        }

        public void setDeviceList(List<Device> deviceList) {
            this.deviceList = deviceList;
        }

        public boolean isOnlyDevice() {
            return onlyDevice;
        }

        public void setOnlyDevice(boolean onlyDevice) {
            this.onlyDevice = onlyDevice;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "Params{" +
                    "deviceList=" + deviceList +
                    ", onlyDevice=" + onlyDevice +
                    ", token='" + token + '\'' +
                    '}';
        }

        public static class Device{
            String deviceId;
            List<String> channelId;

            public Device(String deviceId, List<String> channelId) {
                this.deviceId = deviceId;
                this.channelId = channelId;
            }

            public String getDeviceId() {
                return deviceId;
            }

            public void setDeviceId(String deviceId) {
                this.deviceId = deviceId;
            }

            public List<String> getChannelId() {
                return channelId;
            }

            public void setChannelId(List<String> channelId) {
                this.channelId = channelId;
            }

            @Override
            public String toString() {
                return "Device{" +
                        "deviceId='" + deviceId + '\'' +
                        ", channelId='" + channelId + '\'' +
                        '}';
            }
        }
    }

}
