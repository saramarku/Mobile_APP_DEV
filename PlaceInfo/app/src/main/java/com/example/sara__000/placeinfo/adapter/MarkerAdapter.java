package com.example.sara__000.placeinfo.adapter;

/**
 * Created by sara__000 on 5/16/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.example.sara__000.placeinfo.R;
import com.example.sara__000.placeinfo.data.MarkerCoord;
import com.example.sara__000.placeinfo.data.Post;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sara__000 on 4/20/2017.
 */

public class MarkerAdapter  {



    private List<MarkerCoord> markerList;
    private Context context;
    private Realm realmTodo;
    private DatabaseReference markerRef;


    public MarkerAdapter (Context context, Realm realm) {
        this.context = context;

        //initialize realmTodo  create database
        //changed it to realm that we get from application class
        realmTodo = realm;
        //we will query the todo objects from database and sor it
        RealmResults<MarkerCoord> markerResult = realmTodo.where(MarkerCoord.class).findAll();
        //we save it in arraylist
        markerList = new ArrayList<MarkerCoord>();

        for (int i = 0; i <markerResult.size() ; i++) {
            markerList.add(markerResult.get(i));

           // markerRef = FirebaseDatabase.getInstance().getReference("markers");


        }
    }
public void remove(MarkerCoord marker){
    realmTodo.beginTransaction();
    MarkerCoord marker1 = realmTodo.where(MarkerCoord.class).equalTo("lat", marker.getLat()).equalTo("lng", marker.getLng()).findFirst();
    marker1.deleteFromRealm();
    //markerList.remove(marker);
    realmTodo.commitTransaction();
}

    public List<MarkerCoord> getMarkerList() {
        return markerList;
    }


//adding to the tree of database

    //inflating the layout

    public void addMarker(double lat, double lng, String username, String title, String description, String imageUrl, float rating) {
        //start a transaction first everything is saved there
        realmTodo.beginTransaction();
        MarkerCoord newMarker = realmTodo.createObject(MarkerCoord.class, UUID.randomUUID().toString());
        newMarker.setDescription(description);
        newMarker.setUsername(username);
        newMarker.setTitle(title);
        newMarker.setLat(lat);
        newMarker.setLng(lng);
        newMarker.setImageUrl(imageUrl);
        newMarker.setRating(rating);
        realmTodo.commitTransaction();


    }

    //removing when i delete my post
    public void removePost(int index) {
        //you remove it from the tree: postRef a branch and child has all the keys with the posts
        //postsRef.child(postKeys.get(index)).removeValue();
        //postList.remove(index);
        //postKeys.remove(index);
        //notifyItemRemoved(index);
    }

    //removin when someone else reomves the post.
    /**
    public void removePostByKey(String key) {
        int index = postKeys.indexOf(key);
        if (index != -1) {
            postList.remove(index);
            postKeys.remove(index);
            notifyItemRemoved(index);
        }
    }
*/


}