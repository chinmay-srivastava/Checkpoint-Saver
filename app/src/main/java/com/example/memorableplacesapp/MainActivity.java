package com.example.memorableplacesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static ArrayList<String> locationNames;
    static ArrayList<LatLng> newLoc;
    static ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.memorableplacesapp;", Context.MODE_PRIVATE);
        ListView listView = (ListView)findViewById(R.id.ListView);


        ArrayList<String> latitudes=new ArrayList<>();
        ArrayList<String> longitudes=new ArrayList<>();
        locationNames= new ArrayList<String>();
        newLoc= new ArrayList<LatLng>();
        longitudes.clear();
        latitudes.clear();
        locationNames.clear();

        try{
            locationNames=(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<>())));
            latitudes=(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lats",ObjectSerializer.serialize(new ArrayList<>())));
            longitudes=(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longs",ObjectSerializer.serialize(new ArrayList<>())));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if(locationNames.size()>0&& longitudes.size()>0&& latitudes.size()>0){
            if(locationNames.size()==latitudes.size() && locationNames.size()==longitudes.size()){
                for (int i=0;i<latitudes.size();i++) {
                    newLoc.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble((longitudes.get(i)))));
                }
            }
        }
        else {
            locationNames.add("Add a new Location ...");
            newLoc.add(new LatLng(0,0));
        }


        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_activated_1  ,locationNames);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("place number",i);
                startActivity(intent);
            }
        });

    }
}