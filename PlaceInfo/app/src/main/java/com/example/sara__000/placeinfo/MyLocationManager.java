package com.example.sara__000.placeinfo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by sara__000 on 5/20/2017.
 */

public class MyLocationManager implements LocationListener {

    public interface OnNewLocationAvailable {
        public void onNewLocation(Location location);
    }

    private OnNewLocationAvailable onNewLocationAvailable;
    private LocationManager locationManager;

    public MyLocationManager(OnNewLocationAvailable onNewLocationAvailable) {
        this.onNewLocationAvailable = onNewLocationAvailable;
    }

    public void startLocationMonitoring(Context ctx) throws SecurityException {
        locationManager = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);


        // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
        //         0, 0, this);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, this);
    }

    public void stopLocationMonitorinig() throws SecurityException {
        locationManager.removeUpdates(this);
    }


    @Override
    public void onLocationChanged(Location location) {
        onNewLocationAvailable.onNewLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
