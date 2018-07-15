package com.example.maayanmash.finalproject.Model.entities;

import android.arch.persistence.room.Entity;

@Entity
public class SubTask {
    private String dID;
    private boolean isDone;

    public SubTask(String dID, boolean isDone) {
        this.dID = dID;
        this.isDone = isDone;
    }



    public String getdID() {
        return dID;
    }

    public void setdID(String dID) {
        this.dID = dID;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "dID='" + dID + '\'' +
                ", isDone=" + isDone +
                '}';
    }
}
