package com.example.maayanmash.finalproject.Model.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    public String uid;
    public String name;
    public String email;
    public String phone;
    public String address;
    public Double latitude;
    public Double longitude;
    public String image;
    public String mid;
    public boolean manager;

    public User(){}

    @NonNull
    public String getUid() {return uid;}

    public void setUid(@NonNull String uid) {this.uid = uid;}

    public String getMid() {return mid;}

    public void setMid(String mid) {this.mid = mid;}

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

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public User(String uID, String name,String email, String phone, String address, Double latitude, Double longitude, String image, String mID, boolean manager) {

        this.uid = uID;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.mid = mID;
        this.manager=false;
        this.manager=manager;
    }

    @Override
    public String toString() {
        return "User{" +
                "uID='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", image='" + image + '\'' +
                ", mID='" + mid + '\'' +
                ", manager='" + manager + '\'' +
                '}';
    }
}
