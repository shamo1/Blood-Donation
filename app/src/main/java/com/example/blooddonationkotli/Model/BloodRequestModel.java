package com.example.blooddonationkotli.Model;

public class BloodRequestModel {
    String name, blood_for, date, address, phone, description, requestId, bloodGroup, lat, lang, city, uId;

    public BloodRequestModel() {
    }

    public BloodRequestModel(String name, String blood_for, String date, String address, String phone, String description, String requestId, String bloodGroup, String lat, String lang, String city, String uId) {
        this.name = name;
        this.blood_for = blood_for;
        this.date = date;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.requestId = requestId;
        this.bloodGroup = bloodGroup;
        this.lat = lat;
        this.lang = lang;
        this.city = city;
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBlood_for() {
        return blood_for;
    }

    public void setBlood_for(String blood_for) {
        this.blood_for = blood_for;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
