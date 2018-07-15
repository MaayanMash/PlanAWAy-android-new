package com.example.maayanmash.finalproject.Model.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class TaskRow {
    @PrimaryKey
    @NonNull
    private String trID;
    private String address;
    private boolean isDone;
    private String did;
    private String name;


    public TaskRow(String trID,String address, boolean isDone, String did, String name){
        this.trID=trID;
        this.address = address;
        this.isDone = isDone;
        this.did = did;
        this.name = name;
    }

    public TaskRow(String address, boolean isDone, String did, String name) {
        this.address = address;
        this.isDone = isDone;
        this.did = did;
        this.name = name;
    }

    @NonNull
    public String getTrID() {
        return trID;
    }

    public void setTrID(@NonNull String trID) {
        this.trID = trID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TaskRow{" +
                "address='" + address + '\'' +
                ", isDone=" + isDone +
                ", did='" + did + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
