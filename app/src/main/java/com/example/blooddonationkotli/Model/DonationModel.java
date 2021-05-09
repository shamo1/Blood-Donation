package com.example.blooddonationkotli.Model;

public class DonationModel {
    String name, phone, address, lat, lang, descriptions, userKey, bloodGoup, city, pushKey;

    public DonationModel() {
    }

    public DonationModel(String name, String phone, String address, String lat, String lang, String descriptions, String bloodGoup, String userKey, String city, String pushKey) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.lat = lat;
        this.lang = lang;
        this.descriptions = descriptions;
        this.userKey = userKey;
        this.bloodGoup = bloodGoup;
        this.city = city;
        this.pushKey = pushKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getBloodGoup() {
        return bloodGoup;
    }

    public void setBloodGoup(String bloodGoup) {
        this.bloodGoup = bloodGoup;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }
}
