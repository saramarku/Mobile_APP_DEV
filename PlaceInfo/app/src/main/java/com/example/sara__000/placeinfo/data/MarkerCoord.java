package com.example.sara__000.placeinfo.data;

import android.media.Image;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sara__000 on 5/20/2017.
 */

public class MarkerCoord extends RealmObject {
    public static String COL_TODO_ID = "markerID";

    //this is for the id in the table
    @PrimaryKey
    private String markerID;
    private double lat;
    private double lng;

    private String username;
    private String description;
    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    private float rating;

    //we need an empty constructor bcs of Realm

    public MarkerCoord(String markerID, double lat, double lng) {
        this.markerID = markerID;
        this.lat = lat;
        this.lng = lng;
    }


    public MarkerCoord( double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public MarkerCoord( double lat, double lng,  String username, String title, String description, String imageUrl, float rating) {
        this.username = username;
        this.description = description;
        this.title= title;
        this.lat = lat;
        this.lng = lng;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }


    public MarkerCoord(){}


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getMarkerID() {
        return markerID;
    }

    public void setMarkerID(String markerID) {
        this.markerID = markerID;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }


}
