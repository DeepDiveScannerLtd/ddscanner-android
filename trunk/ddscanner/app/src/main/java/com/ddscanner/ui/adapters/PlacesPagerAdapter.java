package com.ddscanner.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ddscanner.entities.DiveSpot;
import com.ddscanner.ui.fragments.ProductListFragment;
import com.ddscanner.ui.managers.DiveSpotsClusterManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PlacesPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = PlacesPagerAdapter.class.getName();
    private Context context;
    private LatLng latLng;
    private MapFragment mapFragment;
    private ProductListFragment productListFragment;
    private DiveSpotsClusterManager diveSpotsClusterManager;

    public PlacesPagerAdapter(Context context, FragmentManager fm, LatLng latLng) {
        super(fm);
        this.context = context;
        this.latLng = latLng;
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
                        if (latLng.longitude == 0 && latLng.latitude == 0) {
                            latLng = new LatLng(0,0);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 1.0f));
                        } else {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 1.0f));
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
