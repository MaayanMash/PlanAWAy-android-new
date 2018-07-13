package com.example.maayanmash.finalproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.maayanmash.finalproject.Model.Constants;
import com.example.maayanmash.finalproject.Model.Model;
import com.example.maayanmash.finalproject.Model.entities.AddressHolder;
import com.example.maayanmash.finalproject.Model.entities.Destination;
import com.example.maayanmash.finalproject.Model.entities.TaskRow;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final String LOCATINS = "LOCATINS";

    private Location myLocation;
    private List<AddressHolder> addressResponse = new ArrayList<>();
    private List<MarkerOptions> markerOptionsList= new ArrayList<>();
    private Map<String, String> mapDestination = new HashMap<>();
    private Intent serviceIntent = null;
    private View view;
    private GoogleMap mMap;
    private MapView mMapView;
    private static final int MY_LOCATION_REQUEST_CODE = 500;
    private JSONArray jsonArray = new JSONArray();
    private String uid;

    private Double zoom_lat;
    private Double zoom_long;

    private LoadToast lt;

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
            view = inflater.inflate(R.layout.fragment_map, container, false);
            MapsInitializer.initialize(this.getActivity());
            mMapView = (MapView) view.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);

            lt = new LoadToast(getContext())
                    .setText("")
                    .setTranslationY(250);

            ImageView addLocation = view.findViewById(R.id.map_addLocation);
            addLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {addLocationToMap();}
            });

            ImageView sendDestination = view.findViewById(R.id.map_send_btn);
            sendDestination.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToServer();
                }
            });

            final Button startGoogleMapsBtn = view.findViewById(R.id.map_start_btn);
            startGoogleMapsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startGoogleMaps();
                }
            });

        } catch (InflateException e) {
            Log.d("TAG", "Inflate exception");
        }
        return view;
    }

    private void addAllLocations(List<String> list){
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

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (myLocation != null) {
            JSONObject jsonObject = new JSONObject();
            Model.instance.updateMyLocation(myLocation.getLatitude(), myLocation.getLongitude());
            try {
                jsonObject.put("address", myLocation.getLatitude() + ", " + myLocation.getLongitude());
                this.jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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
        EditText locationSearch = view.findViewById(R.id.map_editText);
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

    public void checkMyLocation() {
        if (myLocation == null) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity().getApplicationContext(), "Your location is not turned on", Toast.LENGTH_SHORT).show();
                return;
            }
            myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            mMap.setMyLocationEnabled(true);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("address", myLocation.getLatitude() + ", " + myLocation.getLongitude());
                this.jsonArray.put(jsonObject);
            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Your location is not turned on", Toast.LENGTH_SHORT).show();
            }
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
                getActivity().stopService(new Intent(getActivity().getBaseContext(), MyService.class));
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

    ////////////// server //////////////////

    public void sendToServer() {
        lt.show();
        Server server = new Server();
        server.execute("");
    }

    public void getDirection() {
        String url = getRequestUrl();
        mMap.clear();
        addAllmarkerToMapAgain();
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections(this.mMap, lt);
        taskRequestDirections.execute(url);
    }

    private void addAllmarkerToMapAgain(){
        for (MarkerOptions marker:markerOptionsList) {
            mMap.addMarker(marker);
        }
    }

    private String getRequestUrl() {
        LatLng origin;
        LatLng dest;
        int size = this.addressResponse.size();

        //my location in an origin
        origin = new LatLng(addressResponse.get(0).latitude, addressResponse.get(0).longitude);

        //the last address in a destination
        dest = new LatLng(addressResponse.get(size - 1).latitude, addressResponse.get(size - 1).longitude);

        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String str_waypoints = "";

        //there is wayPoints
        if (size >= 3) {
            //LatLng waypoints=null;
            str_waypoints = "waypoints=optimize:false|";

            LatLng waypoints = new LatLng(addressResponse.get(1).latitude, addressResponse.get(1).longitude);
            str_waypoints += waypoints.latitude + "," + waypoints.longitude;

            for (int i = 2; i < size - 1; i++) {
                waypoints = new LatLng(addressResponse.get(i).latitude, addressResponse.get(i).longitude);
                str_waypoints += "|" + waypoints.latitude + "," + waypoints.longitude;
            }
        }
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String param = str_org + "&" + str_dest + "&" + str_waypoints + "&" + sensor + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    public void startLocationService(List<AddressHolder> locationsList) {
        StringBuilder builder = new StringBuilder("");
        int size = locationsList.size();
        for (int i = 1; i < size - 1; i++) {
            builder.append(mapDestination.get(locationsList.get(i).latitude + ", " + locationsList.get(i).longitude) + ",");
            builder.append(locationsList.get(i).latitude + "," + locationsList.get(i).longitude + "->");

        }
        builder.append(mapDestination.get(locationsList.get(size - 1).latitude + ", " + locationsList.get(size - 1).longitude) + ",");
        builder.append(locationsList.get(size - 1).latitude + "," + locationsList.get(size - 1).longitude);

        Intent intent = new Intent(getActivity().getBaseContext(), MyService.class);
        intent.putExtra("locations", builder.toString());
        serviceIntent = intent;
        getActivity().startService(intent);
    }

    public void startGoogleMaps() {
        if (addressResponse != null && addressResponse.size() > 0) {
            int size = this.addressResponse.size();
            String str_url = "http://www.google.com/maps/dir/?api=1&origin=";
            str_url += myLocation.getLatitude() + "," + myLocation.getLongitude() + "&";
            LatLng dest = dest = new LatLng(addressResponse.get(size - 1).latitude, addressResponse.get(size - 1).longitude);
            str_url += "destination=" + dest.latitude + "," + dest.longitude;

            String str_waypoints = "";

            //there is wayPoints
            if (size >= 3) {
                //LatLng waypoints=null;
                str_waypoints = "&waypoints=";

                LatLng waypoints = new LatLng(addressResponse.get(1).latitude, addressResponse.get(1).longitude);
                str_waypoints += waypoints.latitude + "," + waypoints.longitude;

                for (int i = 2; i < size - 1; i++) {
                    waypoints = new LatLng(addressResponse.get(i).latitude, addressResponse.get(i).longitude);
                    str_waypoints += "|" + waypoints.latitude + "," + waypoints.longitude;
                }

                str_url += str_waypoints;
            }
            str_url += "&travelmode=driving";

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str_url));
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.googlemaps"));
                startActivity(intent);
            }
        } else
            Toast.makeText(getActivity().getApplicationContext(), "You need to planAway in order to start navigation", Toast.LENGTH_SHORT).show();

    }

    public class Server extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(Constants.SEARCHING_SERVER_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("locations", jsonArray);
                if (myLocation != null)
                    jsonParam.put("source", myLocation.getLatitude() + ", " + myLocation.getLongitude());
                else
                    checkMyLocation();

                jsonParam.put("destination", "empty");

                Log.d("server", jsonParam.toString());

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                int status = conn.getResponseCode();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG", conn.getResponseMessage());

                String str_res = "error response";

                if (status == 200 || status == 201) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    str_res = sb.toString();
                }

                conn.disconnect();
                return str_res;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "Could not connect to server";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("server", "onPostExecute- " + s);
            if (!s.equals("error response") && !s.equals("Could not connect to server")) {
                try {
                    addressResponse.clear();
                    JSONObject myResponse = new JSONObject(s);
                    JSONArray res = (JSONArray) myResponse.get("SortedLocations");

                    for (int i = 0; i < res.length(); i++) {
                        String str = (String) res.get(i);
                        String[] parts = str.split(", ");
                        addressResponse.add(new AddressHolder(Double.parseDouble(parts[0]), Double.parseDouble(parts[1])));
                    }

                    Log.d("server", addressResponse.toString());
                    getDirection();


                    startLocationService(addressResponse);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                lt.error();
                Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }
    }




}
