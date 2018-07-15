package com.example.maayanmash.finalproject.Model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.example.maayanmash.finalproject.Model.entities.Destination;
import com.example.maayanmash.finalproject.Model.entities.User;
import com.example.maayanmash.finalproject.MyApplication;

@Database(entities = {User.class}, version = 1)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract UserDao userDao();
}

public class AppLocalDb{
    static public AppLocalDbRepository db = Room.databaseBuilder(MyApplication.context,
            AppLocalDbRepository.class,
            "dbFileName.db").fallbackToDestructiveMigration().build();
}
