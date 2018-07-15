//package com.example.maayanmash.finalproject;
//
//import android.arch.lifecycle.LiveData;
//import android.arch.lifecycle.ViewModel;
//
//import com.example.maayanmash.finalproject.Model.Model;
//import com.example.maayanmash.finalproject.Model.entities.TaskRow;
//import com.example.maayanmash.finalproject.Model.entities.User;
//
//import java.util.List;
//
//public class TaskListViewModel extends ViewModel {
//    LiveData<List<TaskRow>> data;
//
//    public LiveData<List<TaskRow>> getDataUser(){
//        data = Model.instance.getAllTaskUser();
//        return data;
//    }
//
//    public LiveData<List<TaskRow>> getDataManager() {
//        return data;
//    }
//}
