package com.example.memorableplacesapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {
LocationManager locationManager;
LocationListener locationListener;
    private GoogleMap mMap;


    public void centerMapLocation(Location location,String Title){
        LatLng userLocation=new LatLng(location.getLatitude(),location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(userLocation).title(Title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,12));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent=getIntent();
        mMap.setOnMapLongClickListener(this);
       if(intent.getIntExtra("place number",0)==0){
           locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
           locationListener=new LocationListener() {
               @Override
               public void onLocationChanged(Location location) {
                   centerMapLocation(location,"Your Current Location");
               }

               @Override
               public void onStatusChanged(String s, int i, Bundle bundle) {

               }

               @Override
               public void onProviderEnabled(String s) {

               }

               @Override
               public void onProviderDisabled(String s) {

               }
           };
           if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
               locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
               Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
               centerMapLocation(lastKnownLocation,"Your Current Location");

           }
           else {
               ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
           }

       }
       else {
           Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
           placeLocation.setLatitude(MainActivity.newLoc.get(intent.getIntExtra("place number",0)).latitude);
           placeLocation.setLongitude(MainActivity.newLoc.get(intent.getIntExtra("place number",0)).longitude);
           centerMapLocation(placeLocation,MainActivity.locationNames.get(intent.getIntExtra("place number",0)));
       }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = " ";

        try {
            List<Address> addresses= geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(addresses!=null && addresses.size()>0){
                if(addresses.get(0).getThoroughfare()!=null){
                    if(addresses.get(0).getSubThoroughfare()!=null) {
                    address+=addresses.get(0).getSubThoroughfare()+" ";
                    }
                    address+=addresses.get(0).getThoroughfare()+" ";
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if (address.equals(" ")){
            SimpleDateFormat date=new SimpleDateFormat("HH:mm yyyy-MM-dd");
            address+=date.format(new Date());
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    MainActivity.locationNames.add(address);
    MainActivity.newLoc.add(latLng);
    MainActivity.arrayAdapter.notifyDataSetChanged();
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.memorableplacesapp;", Context.MODE_PRIVATE);
        try {
            ArrayList<String> latitudes=new ArrayList<>();
            ArrayList<String> longitudes=new ArrayList<>();
            for(LatLng coord: MainActivity.newLoc){
                latitudes.add((Double.toString(coord.latitude)));
                longitudes.add((Double.toString(coord.longitude)));
            }

            sharedPreferences.edit().putString("places",ObjectSerializer.serialize(MainActivity.locationNames)).apply();
            sharedPreferences.edit().putString("lats",ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString("longs",ObjectSerializer.serialize(longitudes)).apply();

        } catch (IOException e) {
            e.printStackTrace();
        }
    Toast.makeText(this,"Location Saved!",Toast.LENGTH_SHORT).show();
    }
}