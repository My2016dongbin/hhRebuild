package com.haohai.platform.fireforestplatform.ui.multitype;

/**
 * Created by Codex.
 */
public class XhUploadRecord {
    private String id;
    private String eventName;
    private String eventTime;
    private String eventAddress;
    private String imageUrl;
    private String createUser;

    public XhUploadRecord(String id, String eventName, String eventTime, String eventAddress, String imageUrl, String createUser) {
        this.id = id;
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.eventAddress = eventAddress;
        this.imageUrl = imageUrl;
        this.createUser = createUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}
