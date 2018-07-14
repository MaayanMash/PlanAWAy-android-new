package com.example.maayanmash.finalproject;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;


import com.example.maayanmash.finalproject.Model.Model;
import com.example.maayanmash.finalproject.Model.entities.User;

import java.util.List;

public class UserListViewModel extends ViewModel {
    LiveData<List<User>> data;

    public LiveData<List<User>> getData(){
        data = Model.instance.getAllUsers();
        return data;
    }


}
