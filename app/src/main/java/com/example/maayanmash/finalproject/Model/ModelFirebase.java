package com.example.maayanmash.finalproject.Model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.maayanmash.finalproject.MainActivity;
import com.example.maayanmash.finalproject.Model.entities.Destination;
import com.example.maayanmash.finalproject.Model.entities.SubTask;
import com.example.maayanmash.finalproject.Model.entities.Task;
import com.example.maayanmash.finalproject.Model.entities.TaskRow;
import com.example.maayanmash.finalproject.Model.entities.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.sql.Ref;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModelFirebase {
    private String uID;
    public static String todayTaskID;
    ValueEventListener eventListener;

    public void cancellGetAllUsers() {
        DatabaseReference stRef = FirebaseDatabase.getInstance().getReference().child("users");
        stRef.removeEventListener(eventListener);
    }

    interface GetAllUsersListener{
        public void onSuccess(List<User> userslist);
    }

    public void getAllUsers(final GetAllUsersListener listener) {

        DatabaseReference stRef = FirebaseDatabase.getInstance().getReference().child("users");
        eventListener = stRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> userList = new LinkedList<>();
                for (DataSnapshot stSnapshot: dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) stSnapshot.getValue();
                    String uid= stSnapshot.getKey();
                    String mid = (String) map.get("mid");
                    String name = (String) map.get("name");
                    String email = (String)map.get("email");
                    String phone = (String) map.get("phone");
                    String address = (String) map.get("address");
                    Double latitude = (Double) map.get("latitude");
                    Double longitude = (Double) map.get("longitude");
                    String image = (String) map.get("image");
                    User user = new User(uid, name, email, phone, address, latitude, longitude, image, mid,false);
                    Log.d("TAG","user: "+name+" "+address);
                    userList.add(user);
                }
                listener.onSuccess(userList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG","onCancelled");
            }
        });
    }

    public User getMyUserDetails(final String uID, final MainActivity.GetUserDetailsCallback callback) {
        this.uID = uID;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    String mid = (String) map.get("mid");
                    String name = (String) map.get("name");
                    String email = (String)map.get("email");
                    String phone = (String) map.get("phone");
                    String address = (String) map.get("address");
                    Double latitude = (Double) map.get("latitude");
                    Double longitude = (Double) map.get("longitude");
                    String image = (String) map.get("image");
                    Boolean manager= (boolean)map.get("manager");
                    User user = new User(uID, name, email, phone, address, latitude, longitude, image, mid,manager);
                    callback.onComplete(user);
                } else callback.onFailure();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });

        return null;
    }

    public boolean DateIsToday(String date) {

        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        String strDate = dateFormat.format(currentDate);
        String[] holder = strDate.split("-");
        String currentTime = holder[0] + "-" + Integer.parseInt(holder[1]) + "-" + Integer.parseInt(holder[2]);
        //String currentTime = "2018-6-3";
        return date.equals(currentTime);
    }

    public String getuID() {
        return uID;
    }

    public void updateMyLocation(Double latitude, Double longitude) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("users").child(uID).child("latitude").setValue(latitude);
        firebaseDatabase.getReference("users").child(uID).child("longitude").setValue(longitude);
    }

    public void updateDestinationArrivalForTask(final String dID, final boolean isDone) {
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        Query query = FirebaseDatabase.getInstance().getReference("tasks/" + todayTaskID).child("destinations");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dest : dataSnapshot.getChildren()) {
                    Map<String, Object> subTask = (Map<String, Object>) dest.getValue();

                    if (subTask.isEmpty() || subTask == null)
                        return;

                    if (((String) subTask.get("did")).equals(dID)) {
                        firebaseDatabase.getReference("tasks/" + todayTaskID).child("destinations").child("" + dest.getKey().toString()).child("isDone").setValue(isDone);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void cleanData(){
        uID = null;
        todayTaskID = null;
    }

    public void getMyDestinationsByID(final String uID, final MainActivity.GetDestinationsForUserIDCallback callback) {
        this.uID = uID;
        final ArrayList<Task> tasks = new ArrayList<Task>();
        final ArrayList<Destination> destsToDay = new ArrayList<Destination>();
        final ArrayList<TaskRow> tasksRowList = new ArrayList<>();

        Query query = FirebaseDatabase.getInstance().getReference().child("tasks").orderByChild("uid").equalTo(uID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final StringBuilder builder = new StringBuilder("");
                boolean enteredOnce = false;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot task : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) task.getValue();
                        if (DateIsToday((String) map.get("date"))) {
                            if (!enteredOnce) {
                                todayTaskID = task.getKey();
                                enteredOnce = true;
                                builder.append(task.getKey());
                            }
                            String mid = (String) map.get("mid");
                            String date = (String) map.get("date");
                            ArrayList<SubTask> substasks = new ArrayList<>();

                            Object dests = map.get("destinations");
                            if (dests instanceof ArrayList) {
                                ArrayList<Map> destsMap = (ArrayList<Map>) dests;
                                for (Map dest : destsMap) {
                                    String did = (String) dest.get("did");
                                    boolean isDone = (boolean) dest.get("isDone");
                                    substasks.add(new SubTask(did, isDone));
                                }

                            } else if (dests instanceof Map) {
                                Map<String, Map> destsMap = (Map<String, Map>) dests;
                                int size = destsMap.size() - 1;
                                for (int i = 0; i < size; i++) {
                                    String index = "" + i;
                                    Map<String, Object> tempMap = destsMap.get(index);
                                    String did = (String) tempMap.get("did");
                                    boolean isDone = (boolean) tempMap.get("isDone");
                                    substasks.add(new SubTask(did, isDone));
                                }
                            }
                            Task newTask = new Task(mid, uID, date, substasks);
                            tasks.add(newTask);
                        }
                    }
                }
                if (tasks.size() > 0) {
                    final Task myTask = tasks.get(0);
                    Query query = FirebaseDatabase.getInstance().getReference().child("destinations").orderByChild("mid").equalTo(myTask.getmID());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot dest : dataSnapshot.getChildren()) {
                                    for (SubTask st : myTask.getDests()) {
                                        if (dest.getKey().equals(st.getdID())) {
                                            Map<String, Object> map = (Map<String, Object>) dest.getValue();
                                            String did = dest.getKey();
                                            String mid = (String) map.get("mid");
                                            String name = (String) map.get("name");
                                            String address = (String) map.get("address");
                                            Double latitude = (Double) map.get("latitude");
                                            Double longitude = (Double) map.get("longitude");
                                            Destination newDest = new Destination(did, mid, name, address, latitude, longitude);
                                            destsToDay.add(newDest);
                                            tasksRowList.add(new TaskRow(address, st.isDone(), did, name));

                                        }
                                    }
                                }
                            }
                            callback.onDestination(destsToDay, builder.toString(), tasksRowList);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("TAG", "failed to get task");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG", "failed to get task");
            }
        });
    }


    public void updateUserDetails(User user){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("TAG",user.toString());
        mDatabase.child("users").child(uID).setValue(user);
    }

         ///////////////////////////////////////////////////////
        //                Image Files                        //
       ///////////////////////////////////////////////////////


    public void saveImage(Bitmap imageBitmap,String name, final Model.SaveImageListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        Date d = new Date();
        String new_name = ""+ d.getTime();
        StorageReference imagesRef = storage.getReference().child("images").child(new_name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.onComplete(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTesting") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                listener.onComplete(downloadUrl.toString());
                }
            });
    }

    public void getImage(String url, final Model.GetImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(3* ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                Log.d("TAG","get image from firebase success");
                listener.onDone(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d("TAG",exception.getMessage());
                Log.d("TAG","get image from firebase Failed");
                listener.onDone(null);
            }
        });
    }

}
