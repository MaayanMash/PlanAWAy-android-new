package com.example.maayanmash.finalproject;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.maayanmash.finalproject.Model.Model;
import com.example.maayanmash.finalproject.Model.entities.Destination;
import com.example.maayanmash.finalproject.Model.entities.TaskRow;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManagerFragment extends Fragment implements OnMapReadyCallback{

    private View rootView;
    private GoogleMap mMap;
    private MapView mMapView;
    private static final int MY_LOCATION_REQUEST_CODE = 500;
    private String uID;

    public ManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle =this.getArguments();
        uID = bundle.getString("uid");
        try {
            rootView = inflater.inflate(R.layout.fragment_manager, container, false);
            MapsInitializer.initialize(this.getActivity());
            mMapView = (MapView) rootView.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);

        } catch (InflateException e) {
            Log.d("TAG", "Inflate exception");
        }
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        float zoomLevel = 7.0f;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //get my location
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);

        LatLng israel = new LatLng(31.046051, 34.851611999999996);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(israel, zoomLevel));
        getTodayLocation();
    }

    private void getTodayLocation(){
        final Double[] zoom_lat = new Double[1];
        final Double[] zoom_long = new Double[1];
        Model.instance.getDestinationsBymID(uID, new MainActivity.GetDestinationsForUserIDCallback() {
            @Override
            public void onDestination(ArrayList<Destination> destinations, String taskID, List<TaskRow> taskRowList) {
                Double sum_lat = 0.0;
                Double sum_long = 0.0;
                float zoomLevel = 10;
                for (Destination dest : destinations) {
                    LatLng latLng = new LatLng(dest.getLatitude(), dest.getLongitude());
                    MarkerOptions markerOptions= new MarkerOptions().position(latLng).title(dest.getName());
                    mMap.addMarker(markerOptions);
                    sum_lat += dest.getLatitude();
                    sum_long += dest.getLongitude();
                }
                zoom_lat[0] = sum_lat / destinations.size();
                zoom_long[0] = sum_long / destinations.size();
                LatLng new_zoom = new LatLng(zoom_lat[0], zoom_long[0]);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new_zoom, zoomLevel));

            }
        });
    }



    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //Menu
    @SuppressLint("ResourceType")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        getActivity().getMenuInflater().inflate(R.menu.manager_manu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        FragmentTransaction tran = getActivity().getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.MyDrivers:
                UserListFragment fragment =new UserListFragment();
                tran.replace(R.id.main_container, fragment);
                tran.addToBackStack("");
                tran.commit();
                return true;
            case R.id.DailyDestinations:
                MyTaskListFragment task_fragment = MyTaskListFragment.newInstance(uID,true);
                tran.replace(R.id.main_container, task_fragment);
                tran.addToBackStack("tag");
                tran.commit();
                return true;
            case R.id.EditMyAccount:
                EditMyAccountFragment account_fragment = new EditMyAccountFragment();
                tran.replace(R.id.main_container, account_fragment);
                tran.addToBackStack("tag");
                tran.commit();
                return true;
            case R.id.Logout:
                Model.instance.cleanData();
                //stopService(new Intent(getBaseContext(), MyService.class));
                Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
