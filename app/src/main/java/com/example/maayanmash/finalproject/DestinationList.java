package com.example.maayanmash.finalproject;

import com.example.maayanmash.finalproject.Model.Model;
import com.example.maayanmash.finalproject.Model.entities.Destination;

import java.util.ArrayList;
import java.util.List;

public class DestinationList {
    public static DestinationList instance = new DestinationList();
    private List<Destination> data= new ArrayList<>();

    private DestinationList(){}

    public void addAll (List<Destination> list){
        data.addAll(list);
    }

    public List<Destination> getData() {
        return data;
    }

    public void addDest (Destination dest){
        data.add(dest);
    }

    public void clearAll(){
        data.clear();
    }
}
