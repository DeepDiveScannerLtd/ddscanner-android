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
import android.util.Log;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.entities.request.UpdateLocationRequest;
import com.ddscanner.events.LocationReadyEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashSet;

public class LocationHelper implements LocationListener {

    private static final String TAG = LocationHelper.class.getName();

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final long MIN_TIME_BETWEEN_UPDATES = 1000L * 60 * 1;
    private static final long MAX_LOCATION_LIFE_PERIOD = 1L * 60 * 60 * 1000; // in millis
    private static final int LOCATION_ACCURACY = 2000; // in meters
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    private LocationManager locationManager;
    private Activity context;
    private HashSet<Integer> requestCodes = new HashSet<>();

    private DDScannerRestClient.ResultListener<Void> updateLocationResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {

        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }

        @Override
        public void onInternetConnectionClosed() {

        }
    };

    public LocationHelper(Activity context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void checkLocationConditions() throws LocationProvidersNotAvailableException, LocationPPermissionsNotGrantedException {
        Log.i(TAG, "checkLocationConditions start");
        if (!isLocationPermissionsGranted(context)) {
            Log.i(TAG, "checkLocationConditions isLocationPermissionsGranted = false");
            throw new LocationPPermissionsNotGrantedException();
        }
        if (!isLocationProvidersAvailable()) {
            Log.i(TAG, "checkLocationConditions isLocationProvidersAvailable = false");
            throw new LocationProvidersNotAvailableException();
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

    public void requestLocation(HashSet<Integer> requestCodes) {
        Log.i(TAG, "location check: requestLocation codes = " + requestCodes);
        this.requestCodes.addAll(requestCodes);
        Location lastKnownLocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (isLocationOk(lastKnownLocationGps) && isLocationOk(lastKnownLocationNetwork)) {
            if (isBetterLocation(lastKnownLocationNetwork, lastKnownLocationGps)) {
                DDScannerApplication.bus.post(new LocationReadyEvent(lastKnownLocationNetwork, this.requestCodes));
                sendUpdateLocationRequest(new LatLng(lastKnownLocationNetwork.getLatitude(), lastKnownLocationNetwork.getLongitude()));
                this.requestCodes.clear();
                return;
            } else {
                DDScannerApplication.bus.post(new LocationReadyEvent(lastKnownLocationGps, this.requestCodes));
                sendUpdateLocationRequest(new LatLng(lastKnownLocationGps.getLatitude(), lastKnownLocationGps.getLongitude()));
                this.requestCodes.clear();
                return;
            }
        } else if (isLocationOk(lastKnownLocationGps)) {
            DDScannerApplication.bus.post(new LocationReadyEvent(lastKnownLocationGps, this.requestCodes));
            sendUpdateLocationRequest(new LatLng(lastKnownLocationGps.getLatitude(), lastKnownLocationGps.getLongitude()));
            this.requestCodes.clear();
            return;
        } else if (isLocationOk(lastKnownLocationNetwork)) {
            DDScannerApplication.bus.post(new LocationReadyEvent(lastKnownLocationNetwork, this.requestCodes));
            sendUpdateLocationRequest(new LatLng(lastKnownLocationNetwork.getLatitude(), lastKnownLocationNetwork.getLongitude()));
            this.requestCodes.clear();
            return;
        }

        // NETWORK location provider
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }
        // GPS location provider
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }

    }

    private void sendUpdateLocationRequest(LatLng latLng) {
        DDScannerApplication.getInstance().getSharedPreferenceHelper().setUserLocation(latLng);
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
            DDScannerApplication.getInstance().getDdScannerRestClient(null).postUpdateUserLocation(updateLocationResultListener, new UpdateLocationRequest(FirebaseInstanceId.getInstance().getId(), String.valueOf(latLng.latitude), String.valueOf(latLng.longitude), 2));
        }
    }

    private boolean isLocationOk(Location lastKnownLocation) {
        if (lastKnownLocation != null && lastKnownLocation.hasAccuracy() && lastKnownLocation.getAccuracy() <= LOCATION_ACCURACY && (System.currentTimeMillis() - lastKnownLocation.getTime() <= MAX_LOCATION_LIFE_PERIOD)) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isLocationOk(location)) {
            Log.i(TAG, "Found good location, sending bus event.");
            DDScannerApplication.bus.post(new LocationReadyEvent(location, requestCodes));
            requestCodes.clear();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO Why the fuck do we need to request permission when removing location updates listener!?
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
