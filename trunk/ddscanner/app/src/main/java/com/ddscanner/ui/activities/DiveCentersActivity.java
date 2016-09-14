package com.ddscanner.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.events.DiveCenterMarkerClickEvent;
import com.ddscanner.events.LocationReadyEvent;
import com.ddscanner.events.OnMapClickEvent;
import com.ddscanner.events.PutDiveCentersToListEvent;
import com.ddscanner.ui.adapters.DiveCentersListAdapter;
import com.ddscanner.ui.managers.DiveCentersClusterManager;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

public class DiveCentersActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DiveCentersActivity";
    private Toolbar toolbar;
    private LatLng latLng;
    private String dsName;
    private Helpers helpers = new Helpers();
    MapView mMapView;
    private GoogleMap mGoogleMap;

    private RelativeLayout toast;
    private FloatingActionButton mapListFAB;
    private RelativeLayout diveCenterInfo;
    private TextView diveCenterName;
    private LinearLayout rating;
    private TextView diveCenterAddress;
    private ImageView zoomIn;
    private ImageView zoomOut;
    private ImageView goToMyLocation;
    private Long lastDiveSpotId;
    private RelativeLayout mainLayout;
    private TextView object;
    private RelativeLayout mapControlLayout;
    private Marker myLocationMarker;
    private Circle circle;
    private DiveCenter diveCenter;
    private String path = "";
    private ProgressView progressBar;

    private View diveSpotsMapView;
    private View diveSpotsListView;

    private boolean isMapShown = true;

    public BaseAppCompatActivity baseAppCompatActivity;

    // List mode member fields
    private RecyclerView rc;
    private DiveCentersListAdapter diveCentersListAdapter;
    private DiveCentersClusterManager diveCentersClusterManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_centers);
        findViews();
        setMapView(savedInstanceState);
        EventsTracker.trackDiveCentersMapView();
        latLng = getIntent().getParcelableExtra("LATLNG");
        dsName = getIntent().getStringExtra("NAME");
    }

    private void findViews() {
        diveSpotsMapView = findViewById(R.id.map_view);
        diveSpotsListView = findViewById(R.id.list_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.dive_centers);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);

        // Map mode
        diveCenterInfo = (RelativeLayout) findViewById(R.id.dive_spot_info_layout);
        diveCenterName = (TextView) findViewById(R.id.dive_spot_title);
        rating = (LinearLayout) findViewById(R.id.rating);
        zoomIn = (ImageView) findViewById(R.id.zoom_plus);
        zoomOut = (ImageView) findViewById(R.id.zoom_minus);
        object = (TextView) findViewById(R.id.divespot_type);
        goToMyLocation = (ImageView) findViewById(R.id.go_to_my_location);
        mapListFAB = (FloatingActionButton) findViewById(R.id.map_list_fab);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        diveCenterAddress = (TextView) findViewById(R.id.address);
        progressBar = (ProgressView) findViewById(R.id.progressBar);

        // List mode
        rc = (RecyclerView) findViewById(R.id.cv);
        mapListFAB = (FloatingActionButton) findViewById(R.id.map_list_fab);
        mapListFAB.setOnClickListener(this);
        rc.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc.setLayoutManager(linearLayoutManager);
        rc.setItemAnimator(new DefaultItemAnimator());
        
    }

    public static void show(Context context, LatLng latLng, String name) {
        Intent intent = new Intent(context, DiveCentersActivity.class);
        intent.putExtra("LATLNG", latLng);
        intent.putExtra("NAME", name);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "Starting");

    }

    @Override
    protected void onPause() {
        super.onPause();
        DDScannerApplication.activityPaused();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DDScannerApplication.activityResumed();
        if (!helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
        mMapView.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_list_fab:
                if (isMapShown) {
                    if (diveCenterInfo.getVisibility() == View.VISIBLE) {
                        mapListFAB.setY(mapListFAB.getY() + diveCenterInfo.getHeight());
                    }
                    EventsTracker.trackDiveCentersListView();
                    diveSpotsMapView.setVisibility(View.GONE);
                    diveSpotsListView.setVisibility(View.VISIBLE);
                    mapListFAB.setImageResource(R.drawable.ic_acb_map);
                } else {
                    if (diveCenterInfo.getVisibility() == View.VISIBLE) {
                        mapListFAB.setY(mapListFAB.getY() - diveCenterInfo.getHeight());
                    }
                    EventsTracker.trackDiveCentersMapView();
                    diveSpotsMapView.setVisibility(View.VISIBLE);
                    diveSpotsListView.setVisibility(View.GONE);
                    mapListFAB.setImageResource(R.drawable.ic_acb_list);
                }
                isMapShown = !isMapShown;
                break;
            case R.id.zoom_plus:
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.zoom_minus:
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.go_to_my_location:
                goToMyLocation.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                Log.i(TAG, "DiveCentersActivity getLocation");
                this.getLocation(ActivitiesRequestCodes.REQUEST_CODE_DIVE_CENTERS_MAP_GO_TO_CURRENT_LOCATION);
               // baseAppCompatActivity.getLocation(Constants.REQUEST_CODE_MAP_LIST_FRAGMENT_GO_TO_CURRENT_LOCATION);
              //  baseAppCompatActivity.getLocation();
                break;
            case R.id.dive_spot_info_layout:
                DiveCenterDetailsActivity.show(this, diveCenter, path, EventsTracker.SpotViewSource.FROM_MAP);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        mMapView.onLowMemory();
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


    private void setMapView(Bundle savedInstanceState) {
        mMapView = (MapView) findViewById(R.id.mapView);
        LogUtils.i(TAG, "mMapView inited");
        mMapView.onCreate(null);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                LogUtils.i(TAG, "onMapReady, googleMap = " + googleMap);
                mGoogleMap = googleMap;
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        LogUtils.i(TAG, "onMapLoaded");
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(latLng)
                                .zoom(8)
                                .build();
                        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                        diveCentersClusterManager = new DiveCentersClusterManager(DiveCentersActivity.this, mGoogleMap, latLng, dsName);
                        mGoogleMap.setOnInfoWindowClickListener(diveCentersClusterManager);
                        mGoogleMap.setOnMarkerClickListener(diveCentersClusterManager);
                        mGoogleMap.setOnCameraChangeListener(diveCentersClusterManager);
                    }
                });

            }
        });
        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);
        goToMyLocation.setOnClickListener(this);
        diveCenterInfo.setOnClickListener(this);
        mapControlLayout = (RelativeLayout) findViewById(R.id.map_control_layout);
    }

    @Subscribe
    public void fillDiveSpots(PutDiveCentersToListEvent event) {
        if (diveCentersListAdapter == null) {
            diveCentersListAdapter = new DiveCentersListAdapter(event.getDiveCenters(), event.getPath(), this);
            rc.setAdapter(diveCentersListAdapter);
        } else {
            diveCentersListAdapter.setDiveCenters(event.getDiveCenters(), event.getPath());
        }

        if (!event.getDiveCenters().isEmpty()) {
            rc.setVisibility(View.VISIBLE);
//            please.setVisibility(View.GONE);
        } else {
            rc.setVisibility(View.GONE);
          //  please.setVisibility(View.VISIBLE);
        }

    }

    @Subscribe
    public void hidediveCenterInfo(OnMapClickEvent event) {
        // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//                event.getMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
        if (event.getMarker() != null) {
            try {
                event.getMarker().setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pin_dc)));
            } catch (NullPointerException e) {

            } catch (IllegalArgumentException e) {

            }
        }
        mapControlLayout.animate().translationY(0);
        mapListFAB.animate().translationY(0);
        diveCenterInfo.animate()
                .translationY(diveCenterInfo.getHeight())
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        diveCenterInfo.setVisibility(View.GONE);
                    }
                });
    }

    @UiThread
    @Subscribe
    public void getdiveCenterInfow(DiveCenterMarkerClickEvent event) {
        path = event.getPath();
        diveCenter = event.getDiveCenter();
        mapControlLayout.animate().translationY(-diveCenterInfo.getHeight());
        mapListFAB.animate().translationY(-diveCenterInfo.getHeight());
        diveCenterInfo.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        diveCenterInfo.setVisibility(View.VISIBLE);
                    }
                });
        diveCenterName.setText(event.getDiveCenter().getName());
        if (event.getDiveCenter().getAddress() != null && !event.getDiveCenter().getAddress().equals("")) {
            diveCenterAddress.setVisibility(View.VISIBLE);
            diveCenterAddress.setText(event.getDiveCenter().getAddress());
        } else {
            diveCenterAddress.setVisibility(View.GONE);
        }
        rating.removeAllViews();
        for (int k = 0; k < Math.round(event.getDiveCenter().getRating()); k++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_iw_star_full);
            iv.setPadding(0, 0, 5, 0);
            rating.addView(iv);
        }
        for (int k = 0; k < 5 - Math.round(event.getDiveCenter().getRating()); k++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_iw_star_empty);
            iv.setPadding(0, 0, 5, 0);
            rating.addView(iv);
        }
    }

    @Subscribe
    public void onLocationReady(LocationReadyEvent event) {
        LogUtils.i(TAG, "location check: onLocationReady, request codes = " + event.getRequestCodes());
        for (Integer code : event.getRequestCodes()) {
            switch (code) {
                case ActivitiesRequestCodes.REQUEST_CODE_DIVE_CENTERS_MAP_GO_TO_CURRENT_LOCATION:
                    LatLng myLocation = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(myLocation)
                            .zoom(12)
                            .build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                    goToMyLocation.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    if (circle == null) {
                        // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//                myLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
//                        .position(myLocation)
//                        .anchor(0.5f, 0.5f)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_me)));
                        myLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
                                .position(myLocation)
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_pin_me))));
                        CircleOptions circleOptions = new CircleOptions()
                                .center(myLocation)
                                .radius(200)
                                .strokeColor(android.R.color.transparent)
                                .fillColor(Color.parseColor("#1A0668a1"));
                        circle = mGoogleMap.addCircle(circleOptions);
                        diveCentersClusterManager.setUserCurrentLocationMarker(myLocationMarker);
                    } else {
                        circle.setCenter(myLocation);
                        myLocationMarker.setPosition(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()));
                        diveCentersClusterManager.setUserCurrentLocationMarker(myLocationMarker);
                    }
                    break;

                case ActivitiesRequestCodes.REQUEST_CODE_MAP_LIST_FRAGMENT_GET_LOCATION_ON_FRAGMENT_START:
                    LogUtils.i(TAG, "location check: GET_LOCATION_ON_FRAGMENT_START: event.getLocation() = " + event.getLocation() + " diveSpotsClusterManager = " + diveCentersClusterManager);
                    if (diveCentersClusterManager == null) {
                        // this means map has not yet been initialized. we need to remember location.
                     //   userLocationOnFragmentStart = event.getLocation();
                    } else {
                        // this means map has already been initialized.
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
                                new LatLng(event.getLocation().getLatitude() - 1, event.getLocation().getLongitude() - 1),
                                new LatLng(event.getLocation().getLatitude() + 1, event.getLocation().getLongitude() + 1)
                        ), 0));
                    }
                    break;
            }
        }
    }


}
