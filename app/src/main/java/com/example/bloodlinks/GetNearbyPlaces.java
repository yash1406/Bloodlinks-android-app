package com.example.bloodlinks;


import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces  extends AsyncTask<Object,  String,String> {

    private String googlePlacesData, url;
    private GoogleMap mMap;

    @Override
    protected String doInBackground(Object... objects) {

        mMap=(GoogleMap)objects[0];
        url=(String)objects[1];

        DownloadUrl downloadUrl=new DownloadUrl();
        try {
            googlePlacesData=downloadUrl.ReadTheURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlacesData;

    }


    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearbyplaces= null;
        DataParser dataParser=new DataParser();
        nearbyplaces=dataParser.parse(s);

        displayNearbyPlaces(nearbyplaces);
    }

    private void displayNearbyPlaces(List<HashMap<String, String>> nearbyplaces){


        for(int i=0; i<nearbyplaces.size();i++){

            MarkerOptions markerOptions=new MarkerOptions();

            HashMap<String, String> googleNearbyPlaces=nearbyplaces.get(i);

            String nameofPlace=googleNearbyPlaces.get("place_name");
            String vicinity=googleNearbyPlaces.get("vicinity");
            double lat=Double.parseDouble(googleNearbyPlaces.get("lat"));
            double lng=Double.parseDouble(googleNearbyPlaces.get("lng"));


            LatLng latLng=new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(nameofPlace+" : "+vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(markerOptions);

            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomBy(-1));



        }
    }
}
