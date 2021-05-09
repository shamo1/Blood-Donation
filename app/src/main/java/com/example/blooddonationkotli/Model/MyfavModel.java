package com.example.blooddonationkotli.Model;

public class MyfavModel {
    String uId, pushId;

    public MyfavModel() {
    }

    public MyfavModel(String uId, String pushId) {
        this.uId = uId;
        this.pushId = pushId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}
