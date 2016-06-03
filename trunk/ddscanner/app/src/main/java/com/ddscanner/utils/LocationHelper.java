package com.ddscanner.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.events.LocationReadyEvent;

public class LocationHelper implements LocationListener {

    private static final String TAG = LocationHelper.class.getName();

    private static final long MIN_TIME_BETWEEN_UPDATES = 1000L * 60 * 1;
    private static final long MAX_LOCATION_LIFE_PERIOD = 1L * 60 * 60 * 1000; // in millis
    private static final int LOCATION_ACCURACY = 500; // in meters
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    private LocationManager locationManager;
    private Activity context;

    public LocationHelper(Activity context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void checkLocationConditions() throws LocationProvidersNotAvailableException, LocationPPermissionsNotGrantedException {
        LogUtils.i(TAG, "checkLocationConditions start");
        if (!isLocationProvidersAvailable()) {
            LogUtils.i(TAG, "checkLocationConditions isLocationProvidersAvailable = false");
            throw new LocationProvidersNotAvailableException();
        }
        if (!isLocationPermissionsGranted(context)) {
            LogUtils.i(TAG, "checkLocationConditions isLocationPermissionsGranted = false");
            throw new LocationPPermissionsNotGrantedException();
        }
    }

    private boolean isLocationPermissionsGranted(Activity context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isLocationProvidersAvailable() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isLocationProvidersAvailable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return false;
        } else {
            return true;
        }
    }

    public void requestLocation() {
        // GPS location provider
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (isLocationOk(lastKnownLocation)) {
                DDScannerApplication.bus.post(new LocationReadyEvent(lastKnownLocation));
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }
        } else
            // NETWORK location provider
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (isLocationOk(lastKnownLocation)) {
                    DDScannerApplication.bus.post(new LocationReadyEvent(lastKnownLocation));
                } else {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
            }
    }

    private boolean isLocationOk(Location lastKnownLocation) {
        if (lastKnownLocation.hasAccuracy() && lastKnownLocation.getAccuracy() <= LOCATION_ACCURACY && (System.currentTimeMillis() - lastKnownLocation.getTime() <= MAX_LOCATION_LIFE_PERIOD)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isLocationOk(location)) {
            DDScannerApplication.bus.post(new LocationReadyEvent(location));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO Why the fuck do we need to request permission when removing locaion updates listener!?
                return;
            }
            locationManager.removeUpdates(this);
        }
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

//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//
//    }

    public static class LocationProvidersNotAvailableException extends Exception {

    }

    public static class LocationPPermissionsNotGrantedException extends Exception {

    }

}
