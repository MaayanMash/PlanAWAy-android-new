package com.example.maayanmash.finalproject.Model;

import android.os.AsyncTask;
import com.example.maayanmash.finalproject.Model.entities.User;
import java.util.List;

public class UserAsynchDao {

    interface UserAsynchDaoListener<T>{
        void onComplete(T data);
    }
    static public void getAll(final UserAsynchDaoListener<List<User>> listener) {
        class MyAsynchTask extends AsyncTask<String,String,List<User>>{
            @Override
            protected List<User> doInBackground(String... strings) {
                List<User> stList = AppLocalDb.db.userDao().getAll();
                return stList;
            }

            @Override
            protected void onPostExecute(List<User> users) {
                super.onPostExecute(users);
                listener.onComplete(users);
            }
        }
        MyAsynchTask task = new MyAsynchTask();
        task.execute();
    }


    static void insertAll(final List<User> users, final UserAsynchDaoListener<Boolean> listener){
        class MyAsynchTask extends AsyncTask<List<User>,String,Boolean>{
            @Override
            protected Boolean doInBackground(List<User>... users) {
                for (User st:users[0]) {
                    AppLocalDb.db.userDao().insertAll(st);
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                listener.onComplete(success);
            }
        }
        MyAsynchTask task = new MyAsynchTask();
        task.execute(users);
    }


}
