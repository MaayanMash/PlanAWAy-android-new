//package com.example.maayanmash.finalproject.Model;
//
//import android.os.AsyncTask;
//
//import com.example.maayanmash.finalproject.Model.entities.Tasks;
//import com.example.maayanmash.finalproject.Model.entities.TaskRow;
//import com.example.maayanmash.finalproject.Model.entities.User;
//
//import java.util.List;
//
//public class TaskAsynchDao {
//    interface TaskAsynchDaoListener<T>{
//        void onComplete(T data);
//    }
//    static public void getAll(final TaskAsynchDao.TaskAsynchDaoListener<List<Tasks>> listener) {
//        class MyAsynchTask extends AsyncTask<String,String,List<Tasks>>{
//            @Override
//            protected List<Tasks> doInBackground(String... strings) {
//                List<Tasks> stList = AppLocalDb.db.taskDao().getAll();
//                return stList;
//            }
//
//            @Override
//            protected void onPostExecute(List<Tasks> tasks) {
//                super.onPostExecute(tasks);
//                listener.onComplete(tasks);
//            }
//        }
//        MyAsynchTask task = new MyAsynchTask();
//        task.execute();
//    }
//
//
//    static void insertAll(final List<Tasks> tasks, final TaskAsynchDao.TaskAsynchDaoListener<Boolean> listener){
//        class MyAsynchTask extends AsyncTask<List<Tasks>,String,Boolean> {
//            @Override
//            protected Boolean doInBackground(List<Tasks>... tasks) {
//                for (Tasks ts:tasks[0]) {
//                    AppLocalDb.db.taskDao().insertAll(ts);
//                }
//                return true;
//            }
//
//            @Override
//            protected void onPostExecute(Boolean success) {
//                super.onPostExecute(success);
//                listener.onComplete(success);
//            }
//        }
//        MyAsynchTask task = new MyAsynchTask();
//        task.execute(tasks);
//    }
//
//
//}
