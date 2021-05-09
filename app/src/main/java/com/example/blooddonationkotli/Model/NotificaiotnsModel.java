package com.example.blooddonationkotli.Model;

public class NotificaiotnsModel {
    String type, from, seen;

    public NotificaiotnsModel() {
    }

    public NotificaiotnsModel(String type, String from, String seen) {
        this.type = type;
        this.from = from;
        this.seen = seen;
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
}


