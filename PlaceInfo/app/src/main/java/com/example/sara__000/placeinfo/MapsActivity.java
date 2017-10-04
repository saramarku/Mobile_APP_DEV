package com.example.sara__000.placeinfo;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.location.Criteria;

import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AlertDialog;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.sara__000.placeinfo.adapter.MarkerAdapter;
import com.example.sara__000.placeinfo.data.MarkerCoord;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

import java.io.ByteArrayOutputStream;

import java.util.HashMap;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MyLocationManager.OnNewLocationAvailable {
    private Context context;
    private GoogleMap mMap;
    private MyLocationManager myLocationManager = new MyLocationManager(this);
    private HashMap<Double, MarkerCoord> eventMarkerMap;
    private double latitude;
    private double longtitude;
    private LatLng position;
    MarkerAdapter markerAdapter;
    List<MarkerCoord> markerList;
    String username;
    public static final int REQUEST_CODE_TAKE_PHOTO = 101;
    String mImageCaptureUri;
   String selectedImagePath;
    Bitmap imgBitmap;
    ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ((MyApplication)getApplication()).openRealm();
        username = getIntent().getStringExtra("username");
        markerAdapter = new MarkerAdapter(this, ((MyApplication)getApplication()).getRealm());
        requestNeededPermission();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        markerList = markerAdapter.getMarkerList();
        //refresh the view with the existing markers
        refreshView();

        //set the search bar for the location
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 13));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(place.getLatLng())      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            @Override
            public void onError(Status status) {
               Toast.makeText(MapsActivity.this, "Empty Input", Toast.LENGTH_SHORT).show();
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            // Use default InfoWindow frame
            private final View window = getLayoutInflater().inflate(R.layout.info_window_layout, null);
            @Override
            public View getInfoWindow(Marker marker) {
                double key = marker.getPosition().latitude + marker.getPosition().longitude;
               MarkerCoord markInfo = eventMarkerMap.get(key);

                TextView txtTitle = ((TextView) window.findViewById(R.id.txtInfoWindowTitle));
                TextView txtInfoTitle = ((TextView) window.findViewById(R.id.txtInfoTitle));
                ImageView photo = (ImageView) window.findViewById(R.id.ivInfoWindowMain);
                RatingBar rating = (RatingBar) window.findViewById(R.id.rating);
                txtTitle.setText(markInfo.getUsername());
                String title = txtTitle.getText().toString();
                if (title != null) {
                    // Spannable string allows us to edit the formatting of the text.
                    SpannableString titleText = new SpannableString(title);
                    titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                    txtTitle.setText(titleText);
                } else {
                    txtTitle.setText("");
                }
                if(!TextUtils.isEmpty((markInfo.getTitle().toString())))
                    txtInfoTitle.setText(markInfo.getTitle().toString());
                TextView txtType = ((TextView) window.findViewById(R.id.txtInfoWindowDescription));
                if(!TextUtils.isEmpty((markInfo.getDescription())))
                txtType.setText(markInfo.getDescription().toString());
                if(!TextUtils.isEmpty(markInfo.getImageUrl())){
                    Glide.with(MapsActivity.this).load(markInfo.getImageUrl()).into(photo);
                }
                rating.setRating(markInfo.getRating());
                rating.setIsIndicator(true);
                return window;
            }
            @Override
            public View getInfoContents(Marker marker) {
                //this method is not called if getInfoWindow(Marker) does not return null
                return null;
            }
        });
        // when long click on the marker, delete it
        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                MarkerCoord markerRemoved = new MarkerCoord(marker.getPosition().latitude, marker.getPosition().longitude);
                markerAdapter.remove(markerRemoved);
                marker.remove();

            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                position = point;
                showAddItemDialogue(position);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

    }

    public void refreshView() {
        //create a HashMap to save the coordinates when we click on the marker for the infowindow
        eventMarkerMap = new HashMap<Double, MarkerCoord>();
        for (int i = 0; i < markerList.size(); i++) {
            double lat = markerList.get(i).getLat();
            double lng = markerList.get(i).getLng();
            double key = lat +  lng;
            Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
            eventMarkerMap.put(key, markerList.get(i));
        }
    }


    public void showAddItemDialogue(LatLng point) {
       final  AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.marker_details_layout, null);
        builder.setView(dialogView);
        final EditText etMarkerName = (EditText) dialogView.findViewById(R.id.title_name);
        final EditText etMarkerDescription = (EditText) dialogView.findViewById(R.id.description);
        final Button photoBtn = (Button) dialogView.findViewById(R.id.photoBtn);
        img = (ImageView) dialogView.findViewById(R.id.imgAttach);
        final RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.rating);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                Toast.makeText(getApplicationContext(),"Your Selected Ratings  : " + String.valueOf(rating),Toast.LENGTH_LONG).show();

            }
        });

        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                //101 is the request code
                startActivityForResult(intentTakePhoto,  REQUEST_CODE_TAKE_PHOTO);



            }
        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Marker marker = mMap.addMarker(new MarkerOptions().position(
                        new LatLng(position.latitude, position.longitude))
                        );
                MarkerCoord newMark = new MarkerCoord(position.latitude,position.longitude, username, etMarkerName.getText().toString(),
                        etMarkerDescription.getText().toString(), selectedImagePath, ratingBar.getRating());
                double key = marker.getPosition().latitude + marker.getPosition().longitude;
                eventMarkerMap.put(key, newMark);

                markerAdapter.addMarker(position.latitude,position.longitude, username,
                        etMarkerName.getText().toString(), etMarkerDescription.getText().toString(), selectedImagePath, ratingBar.getRating());

                if(!TextUtils.isEmpty(selectedImagePath) && selectedImagePath!= null){
                   // Glide.with(MapsActivity.this).load(selectedImagePath).into(img);
                }

            }
        });
        builder.show();

    }




    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }



    private void requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Toast...
            }

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    101);
        } else {

            myLocationManager.startLocationMonitoring(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                // do our job
                myLocationManager.startLocationMonitoring(this);
            } else {
                Toast.makeText(this, "Permission not granted :(", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onNewLocation(Location location) {

        latitude = location.getLatitude();
        longtitude = location.getLongitude();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MyApplication)getApplication()).closeRealm();
    }


}
