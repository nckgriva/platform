package com.gracelogic.platform.notification.firebase;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.HashMap;
import java.util.Map;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FcmMessage {

    private final String to;

    private final String priority = "high";

    private boolean contentAvailable = true;

    private boolean mutableContent = true;

    private final Map<String, Object> data = new HashMap<>();

    private FcmNotification notification;

//	@SerializedName("android")
//	private AndroidConfig android;

    private Long time_to_live = 0L;

    private FcmMessage(String to) {
        super();
        this.to = to;
    }

    public static FcmMessage to(String to) {
        return new FcmMessage(to);
    }

    public FcmMessage data(Map<String, ?> data) {
        if (data != null) {
            for (Map.Entry<String, ?> entry : data.entrySet()) {
                data(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public FcmMessage data(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public String getTo() {
        return to;
    }

    public String getPriority() {
        return priority;
    }

    public boolean isContentAvailable() {
        return contentAvailable;
    }

    public void setContentAvailable(boolean contentAvailable) {
        this.contentAvailable = contentAvailable;
    }

    public boolean isMutableContent() {
        return mutableContent;
    }

    public void setMutableContent(boolean mutableContent) {
        this.mutableContent = mutableContent;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public FcmNotification getNotification() {
        return notification;
    }

    public void setNotification(FcmNotification notification) {
        this.notification = notification;
    }

    public Long getTime_to_live() {
        return time_to_live;
    }

    public void setTime_to_live(Long time_to_live) {
        this.time_to_live = time_to_live;
    }
}