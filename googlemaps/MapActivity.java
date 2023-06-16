package com.example.googlemaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is Ready ");
        mMap = googleMap;
        if (mlocationpermissiongranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private static final  String TAG = "MainActivity";
    private static final String Fine = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String Cors = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int Locationrequestcode = 1234;
    private Boolean mlocationpermissiongranted =false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mfusedLocationClient;
    private ImageView mgps ;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mgps = (ImageView) findViewById(R.id.ic_gps);

        getlocationpermession();
        mgps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked Gps Icon");
                getDeviceLocation();
            }
        });
    }
    public void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the device current location");
        mfusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mlocationpermissiongranted){
                Task location = mfusedLocationClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: Find Location");
                            Location currentLocation =(Location) task.getResult();
                            if (currentLocation!= null){
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,"My Location");
                            }else {
                            Log.d(TAG, "onComplete: Curret location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                            Toast.makeText(MapActivity.this, "please activate location to get your position", Toast.LENGTH_SHORT).show();
                        }
                    }
                    }
                });
            }

        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: Security Exeption" + e.getMessage());
        }
    }
    public void moveCamera(LatLng latLng,float zoom,String title){
        Log.d(TAG, "moveCamera: moving the camera to:lat:"+latLng.latitude +",lng :"+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions Options = new MarkerOptions().position(latLng).title(title);
        mMap.addMarker(Options);
    }


    public void initemap(){
        Log.d(TAG, "initemap: Initialazing Map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);

    }


    private void getlocationpermession(){
        Log.d(TAG, "getlocationpermession: Getting Location Permession");
        String[] permessions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Fine)== PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Cors)== PackageManager.PERMISSION_GRANTED){
                mlocationpermissiongranted=true;
                initemap();
            }else {
                ActivityCompat.requestPermissions(this,permessions,Locationrequestcode);
            }
        }else {
            ActivityCompat.requestPermissions(this,permessions,Locationrequestcode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: Called");
        mlocationpermissiongranted = false;
        switch (requestCode) {
            case Locationrequestcode:{
                if (grantResults.length>0){
                    for (int i = 0 ; i<grantResults.length;i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mlocationpermissiongranted=false;
                            Log.d(TAG, "onRequestPermissionsResult: Permession failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: Permission granted");
                 mlocationpermissiongranted=true;
                    initemap();
                }
            }
        }
    }


}
