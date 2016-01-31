package travel.ilave.deepdivescanner.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.DiveSpot;
import travel.ilave.deepdivescanner.entities.DivespotsWrapper;
import travel.ilave.deepdivescanner.entities.Filters;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.ui.fragments.ProductListFragment;
import travel.ilave.deepdivescanner.ui.fragments.TouchableMapFragment;
import travel.ilave.deepdivescanner.ui.views.OnMapTouchedListener;
import travel.ilave.deepdivescanner.utils.LogUtils;


public class PlacesPagerAdapter extends FragmentStatePagerAdapter implements GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener, OnMapTouchedListener {

    private static final String TAG = PlacesPagerAdapter.class.getName();

    private Context context;
    private LatLng latLng;
    private FragmentManager fm;
    private PlacesPagerAdapter placesPagerAdapter;
    private DivespotsWrapper divespotsWrapper;
    private static ArrayList<DiveSpot> divespots;
    private static GoogleMap gMap;
    private Filters filters;
    private TouchableMapFragment touchableMapFragment;

    public PlacesPagerAdapter(Context context, FragmentManager fm, ArrayList<DiveSpot> divespots, LatLng latLng, Filters filters) {
        super(fm);
        this.fm = fm;
        this.context = context;
        this.divespots = divespots;
        this.latLng = latLng;
        this.filters = filters;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                touchableMapFragment = new TouchableMapFragment();
                touchableMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        gMap = googleMap;
                        gMap.setOnMarkerClickListener(PlacesPagerAdapter.this);
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8.0f));
                        touchableMapFragment.setOnMapTouchedListener(PlacesPagerAdapter.this);
                        final double radiusMax = radius(gMap.getCameraPosition().target, gMap.getProjection().getVisibleRegion().latLngBounds.northeast);
                        requestCityProducts(gMap.getCameraPosition().target, radiusMax);
                        gMap.setOnCameraChangeListener(PlacesPagerAdapter.this);
                    }
                });
                return touchableMapFragment;
            case 1:
                return new ProductListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "MAP";
            case 1:
                return "LIST";
            default:
                return "";
        }
    }


    public double radius(LatLng latCenter, LatLng topRightCorner) {
        double maxRadius = 0;

        maxRadius = topRightCorner.latitude - latCenter.latitude;

        if ((topRightCorner.longitude - latCenter.longitude) > maxRadius) {
            maxRadius = topRightCorner.longitude - latCenter.longitude;
        }
        return maxRadius;
    }

    @Override
    public void onMapTouchedDown() {
        LogUtils.i(TAG, "onMapTouchedDown");
        gMap.setOnCameraChangeListener(PlacesPagerAdapter.this);
    }

    @Override
    public void onMapTouchedUp() {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        LogUtils.i(TAG, "onCameraChange");
        gMap.clear();
        InfoWindowAdapter infoWindowAdapter = new InfoWindowAdapter(context, divespots, gMap);
        gMap.setInfoWindowAdapter(infoWindowAdapter);
        final double radiusMax = radius(gMap.getCameraPosition().target, gMap.getProjection().getVisibleRegion().latLngBounds.northeast);
        requestCityProducts(gMap.getCameraPosition().target, radiusMax);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LogUtils.i(TAG, "onMarkerClick");
        gMap.setOnCameraChangeListener(null);
        LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        gMap.animateCamera(cameraUpdate);

        marker.showInfoWindow();
        return true;
    }

    public void requestCityProducts(final LatLng center, Double radius) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("lat", String.valueOf(center.latitude));
        map.put("lng", String.valueOf(center.longitude));
        map.put("radius", String.valueOf(radius));
        RestClient.getServiceInstance().getDivespots(map, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                System.out.println(response.getBody());
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                LogUtils.i("response code is " + s.getStatus());
                LogUtils.i("response body is " + responseString);
                divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                divespots = (ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots();
                placesPagerAdapter = new PlacesPagerAdapter(context, fm, (ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots(), center, filters);
                gMap.setInfoWindowAdapter(new InfoWindowAdapter(context, (ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots(), gMap));
                ProductListFragment.setadapter((ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("PLACES", error.getResponse().getBody().toString());
                Log.i("PLACES", error.getResponse().getReason());
                Log.i("PLACES", error.getResponse().getUrl());
                System.out.println(error.getResponse().getBody());
                if (error.getCause() instanceof SocketTimeoutException) {
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {

                    } else if (error.getKind().equals(RetrofitError.Kind.HTTP)) {

                    }
                }

            }
        });
    }

    public static LatLng getLastLatlng() {
        return gMap.getCameraPosition().target;
    }
}
