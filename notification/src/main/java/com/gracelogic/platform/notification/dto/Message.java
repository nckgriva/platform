package com.gracelogic.platform.notification.dto;

import java.util.HashMap;
import java.util.Map;

public class Message {

    private String from;

    private String[] toArr;

    private String text;

    private String subject;

    private Map<String, String> specificParams = new HashMap<String, String>();

    public Message(String to, String text) {
        setTo(to);
        this.text = text;
    }

    public Message(String from, String to, String subject, String text) {
        this.from = from;
        setTo(to);
        this.subject = subject;
        this.text = text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return toArr.length > 0 ? toArr[0] : null;
    }

    public void setTo(String to) {
        toArr = new String[] {to};
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getSpecificParams() {
        return specificParams;
    }

    private void setSpecificParams(Map<String, String> specificParams) {
        this.specificParams = specificParams;
    }

    public void addSpecificParam(String key, String value) {
        specificParams.put(key, value);
    }

    public void removeSpecificParam(String key) {
        specificParams.remove(key);
    }

    public String getSpecificParam(String key) {
        return specificParams.get(key);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String[] getToArr() {
        return toArr;
    }

    public void setToArr(String[] toArr) {
        this.toArr = toArr;
    }
}
