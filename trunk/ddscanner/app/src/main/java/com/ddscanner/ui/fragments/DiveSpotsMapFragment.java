package com.ddscanner.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.MarkerClickEvent;
import com.ddscanner.events.OnMapClickEvent;
import com.ddscanner.ui.activities.AddDiveSpotActivity;
import com.ddscanner.ui.activities.DiveSpotDetailsActivity;
import com.ddscanner.ui.adapters.MapListPagerAdapter;
import com.ddscanner.ui.managers.DiveSpotsClusterManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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
    private FloatingActionButton addDsFab;
    private RelativeLayout diveSpotInfo;
    private TextView diveSpotName;
    private LinearLayout rating;
    private TextView diveSpotType;
    private ImageView zoomIn;
    private ImageView zoomOut;
    private ImageView goToMyLocation;
    private Long lastDiveSpotId;

    private MapListPagerAdapter mapListPagerAdapter;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(R.layout.dive_sites_map_fragment, container, false);
        diveSpotInfo = (RelativeLayout) v.findViewById(R.id.dive_spot_info_layout);
        diveSpotName = (TextView) v.findViewById(R.id.dive_spot_title);
        rating = (LinearLayout) v.findViewById(R.id.stars);
        zoomIn = (ImageView) v.findViewById(R.id.zoom_plus);
        zoomOut = (ImageView) v.findViewById(R.id.zoom_minus);
        goToMyLocation = (ImageView) v.findViewById(R.id.go_to_my_location);
        mapListFAB = (FloatingActionButton) v.findViewById(R.id.map_list_fab);
        addDsFab = (FloatingActionButton) v.findViewById(R.id.add_ds_fab);
        addDsFab.setOnClickListener(this);
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
        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);
        goToMyLocation.setOnClickListener(this);
        diveSpotInfo.setOnClickListener(this);
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
    public void onStart() {
        super.onStart();
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_list_fab:
                mapListViewPager.setCurrentItem(1, false);
                break;
            case R.id.zoom_plus:
                diveSpotsClusterManager.mapZoomPlus();
                break;
            case R.id.zoom_minus:
                diveSpotsClusterManager.mapZoomMinus();
                break;
            case R.id.go_to_my_location:
                diveSpotsClusterManager.goToMyLocation();
                break;
            case R.id.dive_spot_info_layout:
                DiveSpotDetailsActivity.show(getActivity(), String.valueOf(lastDiveSpotId));
                break;
            case  R.id.add_ds_fab:
                AddDiveSpotActivity.show(getActivity());
                break;
        }
    }

    @Subscribe
    public void getDiveSpotInfow(MarkerClickEvent event) {
        diveSpotInfo.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        diveSpotInfo.setVisibility(View.VISIBLE);
                    }
                });
        diveSpotName.setText(event.getDiveSpot().getName());
        lastDiveSpotId = event.getDiveSpot().getId();
//        rating.removeAllViews();
    /*    for (int k = 0; k < event.getDiveSpot().getRating(); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_iw_star_full);
            iv.setPadding(0,0,5,0);
            rating.addView(iv);
        }
        for (int k = 0; k < 5 - event.getDiveSpot().getRating(); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_iw_star_empty);
            iv.setPadding(0,0,5,0);
            rating.addView(iv);
        }*/
    }

    @Subscribe
    public void hideDiveSpotinfo(OnMapClickEvent event) {
        event.getMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
        diveSpotInfo.animate()
                .translationY(diveSpotInfo.getHeight())
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        diveSpotInfo.setVisibility(View.GONE);
                    }
                });
    }

    public void setUI(RelativeLayout toast, ProgressBar progressBar, Context context, MapListPagerAdapter mapListPagerAdapter, ViewPager mapListViewPager) {
        this.mapListPagerAdapter = mapListPagerAdapter;
        this.toast = toast;
        this.progressBar = progressBar;
        this.context = context;
        this.mapListViewPager = mapListViewPager;
    }

}
