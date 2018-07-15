package com.example.maayanmash.finalproject.Model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.maayanmash.finalproject.MainActivity;
import com.example.maayanmash.finalproject.Model.entities.Destination;
import com.example.maayanmash.finalproject.Model.entities.Tasks;
import com.example.maayanmash.finalproject.Model.entities.TaskRow;
import com.example.maayanmash.finalproject.Model.entities.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Model {
    public static Model instance = new Model();
    ModelFirebase modelFirebase;
    UserListData userListData = new UserListData();
    //TaskListData taskListData = new TaskListData();

    private Model() {
        modelFirebase = new ModelFirebase();
    }

    public void cancellGetAllUsers() {
    modelFirebase.cancellGetAllUsers();
}

//    public void cancellGetAllTasks() {
//        modelFirebase.cancellGetAllTasks();
//    }

    public User getMyUserDetails(final String uID, final MainActivity.GetUserDetailsCallback callback) {
        return modelFirebase.getMyUserDetails(uID, callback);
    }

    public void getDestinationOfDrivers (final MainActivity.GetDestinationsForUserIDCallback callback){
        modelFirebase.getDestinationOfDrivers(callback);
    }

    public String getuID() {
        return modelFirebase.getuID();
    }

    public void updateDestinationArrivalForTask(final String dID, final boolean isDone) {
        modelFirebase.updateDestinationArrivalForTask(dID, isDone);
    }

    public void updateMyLocation(Double latitude, Double longitude) {
        modelFirebase.updateMyLocation(latitude, longitude);
    }

    public void cleanData() {
        modelFirebase.cleanData();
    }

    public void getMyDestinationsByID(final String uID, final MainActivity.GetDestinationsForUserIDCallback callback) {
        modelFirebase.getMyDestinationsByID(uID, callback);
    }


    public void getDestinationsBymID(final String mID, final MainActivity.GetDestinationsForUserIDCallback callback) {
        modelFirebase.getDestinationsBymID(mID, callback);
    }

    public void updateUserDetails(User user) {
        modelFirebase.updateUserDetails(user);
    }

    public LiveData<List<User>> getAllUsers(){
        return userListData;
    }

//    public LiveData<List<TaskRow>> getAllTaskUser(){
//        return taskListData;
//    }

    class UserListData extends  MutableLiveData<List<User>>{

        @Override
        protected void onActive() {
            super.onActive();
            // 1. get the students list from the local DB
            UserAsynchDao.getAll(new UserAsynchDao.UserAsynchDaoListener<List<User>>() {
                @Override
                public void onComplete(List<User> data) {
                    // 2. update the live data with the new student list
                    setValue(data);
                    Log.d("TAG","got students from local DB " + data.size());

                    // 3. get the student list from firebase
                    modelFirebase.getAllUsers(new ModelFirebase.GetAllUsersListener() {
                        @Override
                        public void onSuccess(List<User> userslist) {
                            // 4. update the live data with the new student list
                            setValue(userslist);
                            Log.d("TAG","got students from firebase " + userslist.size());

                            // 5. update the local DB
                            UserAsynchDao.insertAll(userslist, new UserAsynchDao.UserAsynchDaoListener<Boolean>() {
                                @Override
                                public void onComplete(Boolean data) {
                                    Log.d("TAG","onComplete");
                                }
                            });
                        }
                    });
                }
            });
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            modelFirebase.cancellGetAllUsers();
            Log.d("TAG","cancellGetAllStudents");
        }

        public UserListData() {
            super();
            //setValue(AppLocalDb.db.userDao().getAll());
            setValue(new LinkedList<User>());
        }
    }


//    class TaskListData extends  MutableLiveData<List<TaskRow>>{
//
//        @Override
//        protected void onActive() {
//            super.onActive();
//            // 1. get the tasks list from the local DB
//            TaskAsynchDao.getAll(new TaskAsynchDao.TaskAsynchDaoListener<List<TaskRow>>() {
//                @Override
//                public void onComplete(List<TaskRow> data) {
//                    // 2. update the live data with the new task list
//                    setValue(data);
//                    Log.d("TAG","got tasks from local DB " + data.size());
//
//                    // 3. get the task list from firebase
//                    modelFirebase.getMyDestinationsByID(getuID(), new MainActivity.GetDestinationsForUserIDCallback() {
//                        @Override
//                        public void onDestination(ArrayList<Destination> destinations, String taskID, List<TaskRow> taskRowList) {
//                            // 4. update the live data with the new student list
//                            setValue(taskRowList);
//                            Log.d("TAG","got tasks from firebase " + taskRowList.size());
//
//                            // 5. update the local DB
//                            TaskAsynchDao.insertAll(taskRowList, new TaskAsynchDao.TaskAsynchDaoListener<Boolean>() {
//                                @Override
//                                public void onComplete(Boolean data) {
//                                    Log.d("TAG","onComplete");
//                                }
//                            });
//                        }
//                    });
//
//                }
//            });
//        }
//
//        @Override
//        protected void onInactive() {
//            super.onInactive();
//            modelFirebase.cancellGetAllUsers();
//            Log.d("TAG","cancellGetAllStudents");
//        }
//
//        public TaskListData() {
//            super();
//            //setValue(AppLocalDb.db.userDao().getAll());
//            setValue(new LinkedList<TaskRow>());
//        }
//    }




     ///////////////////////////////////////////////////////
    //                Image Files                        //
   ///////////////////////////////////////////////////////


    public void saveImage(final Bitmap imageBmp, final String name, final SaveImageListener listener) {
        //1. save the image remotly
        modelFirebase.saveImage(imageBmp, name, new SaveImageListener() {
            @Override
            public void onComplete(String url) {
                // 2. saving the file localy
                String nameURL = URLUtil.guessFileName(url, null, null);
                saveImageToFile(imageBmp, nameURL); // synchronously save image locally
                listener.onComplete(url);
            }

            @Override
            public void fail() {
                listener.fail();
            }
        });
    }

    public interface SaveImageListener {
        void onComplete(String url);
        void fail();
    }

    private void saveImageToFile(Bitmap imageBitmap, String imageFileName) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir, imageFileName);
            imageFile.createNewFile();
            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            //addPicureToGallery(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface GetImageListener{
        void onDone(Bitmap imageBitmap);
    }
    public void getImage(final String url, final GetImageListener listener ){
        String localFileName = URLUtil.guessFileName(url, null, null);
        final Bitmap image = loadImageFromFile(localFileName);
        if (image == null) {                                      //if image not found - try downloading it from parse
            modelFirebase.getImage(url, new GetImageListener() {
                @Override
                public void onDone(Bitmap imageBitmap) {
                    if (imageBitmap == null) {
                        listener.onDone(null);
                    }else {
                        //2.  save the image localy
                        String localFileName = URLUtil.guessFileName(url, null, null);
                        Log.d("TAG", "save image to cache: " + localFileName);
                        saveImageToFile(imageBitmap, localFileName);
                        //3. return the image using the listener
                        listener.onDone(imageBitmap);
                    }
                }
            });
        }else {
            listener.onDone(image);
        }
    }

    private Bitmap loadImageFromFile(String imageFileName){
        Bitmap bitmap = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir,imageFileName);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}



