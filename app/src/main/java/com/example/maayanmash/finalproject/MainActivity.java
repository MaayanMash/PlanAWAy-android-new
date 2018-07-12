package com.example.maayanmash.finalproject;

import android.content.pm.PackageManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.maayanmash.finalproject.Model.entities.Destination;
import com.example.maayanmash.finalproject.Model.entities.TaskRow;
import com.example.maayanmash.finalproject.Model.entities.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String uID;
    final int REQUEST_WRITE_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uID = getIntent().getExtras().getString("uid");

        if (savedInstanceState == null) {
            MapFragment fragment = new MapFragment();
            Bundle bundle = new Bundle();
            bundle.putString("uid", uID);
            fragment.setArguments(bundle);


            FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
            tran.replace(R.id.main_container,fragment);
            tran.addToBackStack("");
            tran.commit();
        }
    }

    public String getuID(){
        return this.uID;
    }

    //FireBase
    public interface GetUserDetailsCallback {
        void onComplete(User user);
        void onFailure();
    }

    public interface GetDestinationsForUserIDCallback {
        void onDestination(ArrayList<Destination> destinations, String taskID, List<TaskRow> taskRowList);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }



}
