package com.example.blooddonationkotli.Model;

public class ReminderModel {
    String title, date, description, key;

    public ReminderModel() {
    }

    public ReminderModel(String title, String date, String description, String key) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
