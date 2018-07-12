package com.example.maayanmash.finalproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.maayanmash.finalproject.Model.Model;
import com.example.maayanmash.finalproject.Model.ModelFirebase;
import com.example.maayanmash.finalproject.Model.entities.Destination;
import com.example.maayanmash.finalproject.Model.entities.TaskRow;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final String LOCATINS = "LOCATINS";

    private View rootView;
    private GoogleMap mMap;
    private MapView mMapView;
    private static final int MY_LOCATION_REQUEST_CODE = 500;
    private JSONArray jsonArray = new JSONArray();
    private String uid;

    private Double zoom_lat;
    private Double zoom_long;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","onCreateView");
        Bundle bundle =this.getArguments();
        uid = bundle.getString("uid");

        try {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
            MapsInitializer.initialize(this.getActivity());
            mMapView = (MapView) rootView.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);

            ImageView addLocation = rootView.findViewById(R.id.add);

            addLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   addLocationToMap();
                }
            });

        } catch (InflateException e) {
            Log.d("TAG", "Inflate exception");
        }

        return rootView;
    }

    private void addAllLocations(List<String> list){
        Log.d("TAG","addAllLocation");
        Log.d("TAG",list.toString());
        Double sum_lat = 0.0;
        Double sum_long = 0.0;
        float zoomLevel = 10;
        for (int i=0; i<list.size(); i++) {
            try {
                JSONObject json = new JSONObject(list.get(i));
                String address= json.get("address").toString();
                String[] temp=address.split(", ");
                LatLng latLng = new LatLng(Double.parseDouble(temp[0]),Double.parseDouble(temp[1]));
                MarkerOptions markerOptions= new MarkerOptions().position(latLng);
                mMap.addMarker(markerOptions);
                sum_lat += latLng.latitude;
                sum_long += latLng.longitude;
                JSONObject jsonObject = new JSONObject();
                try {
                    String latLng_str = latLng.latitude + ", " + latLng.longitude;
                    jsonObject.put("address", latLng_str);
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        zoom_lat = sum_lat / list.size();
        zoom_long = sum_long / list.size();
        LatLng new_zoom = new LatLng(zoom_lat, zoom_long);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new_zoom, zoomLevel));
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

    @Override
    public void  onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putStringArray(LOCATINS, new String[]{jsonArray.toString()});
        mMapView.onSaveInstanceState(bundle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("TAG","OnMapReady");
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

        Bundle bundle =this.getArguments();
        if (bundle.get(LOCATINS)!=null) {
            List<String> temp = (List<String>) bundle.get(LOCATINS);
            addAllLocations(temp);
        }else {
            getTodayLocation();
        }
    }

    private void getTodayLocation(){
        Model.instance.getMyDestinationsByID(uid, new MainActivity.GetDestinationsForUserIDCallback() {
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
                    JSONObject jsonObject = new JSONObject();
                    try {
                        String latLng_str = dest.getLatitude() + ", " + dest.getLongitude();
                        jsonObject.put("address", latLng_str);
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                zoom_lat = sum_lat / destinations.size();
                zoom_long = sum_long / destinations.size();
                LatLng new_zoom = new LatLng(zoom_lat, zoom_long);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new_zoom, zoomLevel));

            }
        });
    }

    public void addLocationToMap() {
        float zoomLevel = 12.0f;
        EditText locationSearch = rootView.findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> address = null;
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(getContext());
            try {
                address = geocoder.getFromLocationName(location, 1);
                Log.d("ADDRESS", address.toString());
                if (!address.isEmpty()) {
                    LatLng latLng = new LatLng(address.get(0).getLatitude(), address.get(0).getLongitude());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("address", address.get(0).getLatitude() + ", " + address.get(0).getLongitude());
                    this.jsonArray.put(jsonObject);

                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(location);
                    mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                }
                else
                    Toast.makeText(getContext().getApplicationContext(), "Location Not Found", Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            locationSearch.setText("");
        }
    }

    //Menu
    @SuppressLint("ResourceType")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        getActivity().getMenuInflater().inflate(R.menu.main_manu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        FragmentTransaction tran = getActivity().getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.MyTasks:
                saveAll();
                MyTaskListFragment task_fragment = new MyTaskListFragment();
                tran.replace(R.id.main_container, task_fragment);
                tran.addToBackStack("tag");
                tran.commit();
                return true;
            case R.id.EditMyAccount:
                saveAll();
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

    public void saveAll(){
        Bundle bundle = new Bundle();
        bundle.putString("uid",uid);
        List<String> locations= new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++){
            try {
                locations.add(jsonArray.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        bundle.putStringArrayList(LOCATINS, (ArrayList<String>) locations);
        this.setArguments(bundle);
    }




}
