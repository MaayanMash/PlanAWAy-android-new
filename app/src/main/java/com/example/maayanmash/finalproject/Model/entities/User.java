package com.example.maayanmash.finalproject.Model.entities;

import android.arch.persistence.room.Entity;

@Entity
public class User {
    private String uID;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Double latitude;
    private Double longitude;
    private String image;
    private String mID;

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public User(String uID, String name,String email, String phone, String address, Double latitude, Double longitude, String image, String mID) {

        this.uID = uID;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.mID = mID;
    }

    @Override
    public String toString() {
        return "User{" +
                "uID='" + uID + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", image='" + image + '\'' +
                ", mID='" + mID + '\'' +
                '}';
    }
}
