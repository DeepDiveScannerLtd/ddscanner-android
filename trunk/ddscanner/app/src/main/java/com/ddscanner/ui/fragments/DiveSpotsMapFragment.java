package com.ddscanner.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.ui.adapters.MapListPagerAdapter;
import com.ddscanner.ui.managers.DiveSpotsClusterManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * Created by lashket on 19.4.16.
 */
public class DiveSpotsMapFragment extends Fragment implements View.OnClickListener {

    private GoogleMap mGoogleMap;
    private DiveSpotsClusterManager diveSpotsClusterManager;
    MapView mMapView;

    private Context context;
    private RelativeLayout toast;
    private ProgressBar progressBar;
    private ViewPager mapListViewPager;
    private FloatingActionButton mapListFAB;

    private MapListPagerAdapter mapListPagerAdapter;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(R.layout.dive_sites_map_fragment, container, false);
        mapListFAB = (FloatingActionButton) v.findViewById(R.id.map_list_fab);
        mapListFAB.setOnClickListener(this);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                diveSpotsClusterManager = new DiveSpotsClusterManager(context, mGoogleMap, mapListPagerAdapter ,toast, progressBar);
                mGoogleMap.setOnMarkerClickListener(diveSpotsClusterManager);
                mGoogleMap.setOnCameraChangeListener(diveSpotsClusterManager);

            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_list_fab:
                mapListViewPager.setCurrentItem(1, false);
                break;
        }
    }

    public void setUI(RelativeLayout toast, ProgressBar progressBar, Context context, MapListPagerAdapter mapListPagerAdapter, ViewPager mapListViewPager) {
        this.mapListPagerAdapter = mapListPagerAdapter;
        this.toast = toast;
        this.progressBar = progressBar;
        this.context = context;
        this.mapListViewPager = mapListViewPager;
    }

}
