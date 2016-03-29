package com.ddscanner.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.ui.fragments.ProductListFragment;
import com.ddscanner.ui.managers.DiveSpotsClusterManager;
import com.ddscanner.utils.EventTrackerHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.HashMap;

public class PlacesPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = PlacesPagerAdapter.class.getName();
    private Context context;
    private LatLng latLng;
    private MapFragment mapFragment;
    private ProductListFragment productListFragment;
    private DiveSpotsClusterManager diveSpotsClusterManager;
    private LatLngBounds latLngBounds;

    public PlacesPagerAdapter(Context context, FragmentManager fm, LatLng latLng, LatLngBounds latLngBounds) {
        super(fm);
        this.context = context;
        this.latLng = latLng;
        this.latLngBounds = latLngBounds;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                mapFragment = new MapFragment();
                fragment = mapFragment;
                ((MapFragment) fragment).getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        AppsFlyerLib.getInstance().trackEvent(context,
                                EventTrackerHelper.EVENT_DIVE_SITES_MAP_OPENED, new HashMap<String, Object>());
                        if (latLng.longitude == 0 && latLng.latitude == 0) {
                            latLng = new LatLng(0,0);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 1.0f));
                        } else {
                            if (latLngBounds != null) {
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
                            } else {
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                            }

                        }
                        diveSpotsClusterManager = new DiveSpotsClusterManager(context, googleMap, PlacesPagerAdapter.this);
                        googleMap.setOnInfoWindowClickListener(diveSpotsClusterManager);
                        googleMap.getUiSettings().setMapToolbarEnabled(false);
                       // googleMap.setMyLocationEnabled(true);
                        googleMap.setOnMarkerClickListener(diveSpotsClusterManager);
                        googleMap.setOnCameraChangeListener(diveSpotsClusterManager);
                        googleMap.setInfoWindowAdapter(diveSpotsClusterManager.getMarkerManager());
                    }
                });
                break;
            case 1:
                productListFragment = new ProductListFragment();
                fragment = productListFragment;
                break;
        }
        return fragment;
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

    public void populateDiveSpotsList(ArrayList<DiveSpot> diveSpots) {
        productListFragment.fillDiveSpots(diveSpots);
    }

    public void requestDiveSpots(String currents, String level, String object, int rating, String visibility) {
        diveSpotsClusterManager.updateFilter(currents, level, object, rating, visibility);
        diveSpotsClusterManager.requestCityProducts();
    }
}