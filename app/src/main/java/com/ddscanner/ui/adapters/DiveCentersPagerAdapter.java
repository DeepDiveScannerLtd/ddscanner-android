package com.ddscanner.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.entities.DiveCentersResponseEntity;
import com.ddscanner.ui.fragments.DiveCenterListFragment;
import com.ddscanner.ui.managers.DiveCentersClusterManager;
import com.ddscanner.utils.EventTrackerHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lashket on 4.2.16.
 */
public class DiveCentersPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private DiveCentersResponseEntity diveCentersResponseEntity;
    private LatLng diveSpotLatLng;
    private String diveSpotName;

    public DiveCentersPagerAdapter(Context context, FragmentManager fm, DiveCentersResponseEntity diveCenters, LatLng diveSpotLatLng, String diveSpotName) {
        super(fm);
        this.context = context;
        this.diveCentersResponseEntity = diveCenters;
        this.diveSpotLatLng = diveSpotLatLng;
        this.diveSpotName = diveSpotName;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                fragment = new MapFragment();
                ((MapFragment) fragment).getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(diveSpotLatLng, 8.0f));
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        DiveCentersClusterManager diveCentersClusterManager = new DiveCentersClusterManager(context, googleMap, diveCentersResponseEntity.getDivecenters(), diveSpotLatLng, diveSpotName, diveCentersResponseEntity.getLogoPath());
                        googleMap.setOnInfoWindowClickListener(diveCentersClusterManager);
                        googleMap.getUiSettings().setMapToolbarEnabled(true);
                        googleMap.getUiSettings().setZoomControlsEnabled(false);
                        googleMap.setOnMarkerClickListener(diveCentersClusterManager);
                        googleMap.setOnCameraChangeListener(diveCentersClusterManager);
                        googleMap.setInfoWindowAdapter(diveCentersClusterManager.getMarkerManager());
                    }
                });
                break;
            case 1:
                fragment = new DiveCenterListFragment();
                args.putParcelableArrayList("DIVESPOTS", (ArrayList<? extends Parcelable>) diveCentersResponseEntity.getDivecenters());
                args.putString("LOGOPATH", diveCentersResponseEntity.getLogoPath());
                break;
        }
        fragment.setArguments(args);
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

}
