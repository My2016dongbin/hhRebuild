package com.haohai.platform.fireforestplatform.ui.bean;

import java.util.List;

/**
 * Created by qc
 * on 2023/8/2.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class CommonParams {
    private String appMessageId;
    private int isRead;
    private String id;
    private String content;
    private String feedback;
    private String ext;
    private String ext2;
    private String groupId;
    private String status;
    private String oldPasswd;
    private String newPasswd;
    private String roomId;
    private String type;
    private List<Double> doubleList;
    private List<String> ids;
    private String attendanceStatus;

    private String vhost;
    private String app;
    private String stream;
    private String params;
    private String mediaServerId;
    private String port;
    private String schema;
    private String ip;
    private String startTime;
    private String endTime;
    /*{
        "limit": 20,
                "page": 1,
                "dto": {
            "startTime": "2023-12-01 00:00:00",
                    "endTime": "2023-12-31 23:59:59",
                    "ids": [],
            "attendanceStatus": ""
        }
    };*/
    private int limit;
    private int page;
    private Dto dto;

    public static class Dto{
        private String startTime;
        private String endTime;
        private List<String> ids;
        private String attendanceStatus;

        public Dto(String startTime, String endTime, List<String> ids, String attendanceStatus) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.ids = ids;
            this.attendanceStatus = attendanceStatus;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public List<String> getIds() {
            return ids;
        }

        public void setIds(List<String> ids) {
            this.ids = ids;
        }

        public String getAttendanceStatus() {
            return attendanceStatus;
        }

        public void setAttendanceStatus(String attendanceStatus) {
            this.attendanceStatus = attendanceStatus;
        }
    }

    public CommonParams(int limit, int page, Dto dto) {
        this.limit = limit;
        this.page = page;
        this.dto = dto;
    }

    public CommonParams(String startTime, String endTime, List<String> ids, String attendanceStatus) {
        this.ids = ids;
        this.attendanceStatus = attendanceStatus;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public CommonParams(String id, String vhost, String app, String stream, String params, String mediaServerId, String port, String schema, String ip, String startTime, String endTime) {
        this.id = id;
        this.vhost = vhost;
        this.app = app;
        this.stream = stream;
        this.params = params;
        this.mediaServerId = mediaServerId;
        this.port = port;
        this.schema = schema;
        this.ip = ip;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public CommonParams(String id, String vhost, String app, String stream, String params, String mediaServerId, String port, String schema, String ip) {
        this.id = id;
        this.vhost = vhost;
        this.app = app;
        this.stream = stream;
        this.params = params;
        this.mediaServerId = mediaServerId;
        this.port = port;
        this.schema = schema;
        this.ip = ip;
    }

    public CommonParams(String appMessageId) {
        this.appMessageId = appMessageId;
    }

    public CommonParams(String id, String groupId) {
        this.id = id;
        this.groupId = groupId;
    }

    public CommonParams(String type, List<Double> doubleList) {
        this.type = type;
        this.doubleList = doubleList;
    }

    public CommonParams() {
    }

    public CommonParams(int isRead, String ext, String ext2, String roomId) {
        this.isRead = isRead;
        this.ext = ext;
        this.ext2 = ext2;
        this.roomId = roomId;
    }

    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getMediaServerId() {
        return mediaServerId;
    }

    public void setMediaServerId(String mediaServerId) {
        this.mediaServerId = mediaServerId;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getDoubleList() {
        return doubleList;
    }

    public void setDoubleList(List<Double> doubleList) {
        this.doubleList = doubleList;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public CommonParams(String groupId, String oldPasswd, String newPasswd) {
        this.groupId = groupId;
        this.oldPasswd = oldPasswd;
        this.newPasswd = newPasswd;
    }

    public CommonParams(String appMessageId,String id, String groupId, String type, List<Double> doubleList) {
        this.appMessageId = appMessageId;
        this.id = id;
        this.groupId = groupId;
        this.type = type;
        this.doubleList = doubleList;
    }

    public CommonParams(String id, String feedback, String ext, String ext2) {
        this.id = id;
        this.feedback = feedback;
        this.ext = ext;
        this.ext2 = ext2;
    }

    public CommonParams(int isRead) {
        this.isRead = isRead;
    }

    public CommonParams(int isRead, String id) {
        this.isRead = isRead;
        this.id = id;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getOldPasswd() {
        return oldPasswd;
    }

    public void setOldPasswd(String oldPasswd) {
        this.oldPasswd = oldPasswd;
    }

    public String getNewPasswd() {
        return newPasswd;
    }

    public void setNewPasswd(String newPasswd) {
        this.newPasswd = newPasswd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAppMessageId() {
        return appMessageId;
    }

    public void setAppMessageId(String appMessageId) {
        this.appMessageId = appMessageId;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
