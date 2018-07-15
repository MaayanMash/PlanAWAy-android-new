package com.example.maayanmash.finalproject.Model.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

@Entity
public class Tasks {
    @PrimaryKey
    @NonNull
    private String tID;
    private String mID;
    private String uID;
    private String date;
    private List<SubTask> dests;

    public Tasks(String tID,String mID, String uID, String date, List<SubTask> dests) {
        this.tID=tID;
        this.mID = mID;
        this.uID = uID;
        this.date = date;
        this.dests = dests;
    }

    public Tasks(String mID, String uID, String date, List<SubTask> dests) {
        this.mID = mID;
        this.uID = uID;
        this.date = date;
        this.dests = dests;
    }

    public String getmID() {

        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<SubTask> getDests() {
        return dests;
    }

    public void setDests(List<SubTask> dests) {
        this.dests = dests;
    }

    @Override
    public String toString() {
        return "Task{" +
                "mID='" + mID + '\'' +
                ", uID='" + uID + '\'' +
                ", date='" + date + '\'' +
                ", dests=" + dests.toString() +
                '}';
    }
}
