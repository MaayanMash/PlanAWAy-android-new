package com.example.maayanmash.finalproject.Model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.maayanmash.finalproject.Model.entities.Tasks;
import com.example.maayanmash.finalproject.Model.entities.TaskRow;
import com.example.maayanmash.finalproject.Model.entities.User;

import java.util.List;

@Dao
public interface TasksDao {
    @Query("select * from Tasks")
    List<Tasks> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Tasks... tasks);

    @Delete
    void delete(Tasks task);
}
