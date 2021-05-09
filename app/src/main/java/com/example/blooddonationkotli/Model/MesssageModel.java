package com.example.blooddonationkotli.Model;

public class MesssageModel {
    String message, type, from, seen;
    Long time;

    public MesssageModel() {
    }

    public MesssageModel(String message, String type, String from, String seen, Long time) {
        this.message = message;
        this.type = type;
        this.from = from;
        this.seen = seen;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
