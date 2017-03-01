package com.ddscanner.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
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
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.entities.DiveCentersResponseEntity;
import com.ddscanner.events.DiveCenterMarkerClickEvent;
import com.ddscanner.events.LocationReadyEvent;
import com.ddscanner.events.OnMapClickEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.DiveCentersListAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.ui.managers.DiveCentersClusterManager;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DiveCentersActivity extends BaseAppCompatActivity implements View.OnClickListener, DialogClosedListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = "DiveCentersActivity";
    private LatLng diveSpotLatLng;
    private String diveSpotName;
    MapView mMapView;
    private GoogleMap mGoogleMap;

    private FloatingActionButton mapListFAB;
    private RelativeLayout diveCenterInfo;
    private TextView diveCenterName;
    private LinearLayout rating;
    private TextView diveCenterAddress;
    private ImageView zoomIn;
    private ImageView zoomOut;
    private ImageView goToMyLocation;
    private RelativeLayout mapControlLayout;
    private Marker myLocationMarker;
    private Circle circle;
    private DiveCenter diveCenter;
    private String path = "";
    private ProgressView progressBar;

    private View diveSpotsMapView;
    private View diveSpotsListView;

    private boolean isMapShown = true;

    // List mode member fields
    private RecyclerView rc;
    private DiveCentersListAdapter diveCentersListAdapter;
    private DiveCentersClusterManager diveCentersClusterManager;

    private HashMap<LatLng, DiveCenter> diveCentersMap = new HashMap<>();
    private ArrayList<DiveCenter> diveCenters;
    private Marker lastClickedMarker;
    private Marker diveSpotMarker;
    private String logoPath;

    private DDScannerRestClient.ResultListener<DiveCentersResponseEntity> diveCentersResponseEntityResultListener = new DDScannerRestClient.ResultListener<DiveCentersResponseEntity>() {
        @Override
        public void onSuccess(DiveCentersResponseEntity result) {
            logoPath = result.getLogoPath();
            diveCenters = result.getDivecenters();
            drawMarkers();
            fillDiveSpotsList();
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_DIVE_CENTERS_CLUSTER_MANAGER_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            EventsTracker.trackUnknownServerError(url, errorMessage);
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_DIVE_CENTERS_CLUSTER_MANAGER_UNEXPECTED_ERROR, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_centers);
        findViews();
        setMapView();
        EventsTracker.trackDiveCentersMapView();
        diveSpotLatLng = getIntent().getParcelableExtra("LATLNG");
        diveSpotName = getIntent().getStringExtra("NAME");
        toggleMapListView();

        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveCenters(diveSpotLatLng, diveCentersResponseEntityResultListener);
    }

    private void findViews() {
        diveSpotsMapView = findViewById(R.id.map_view);
        diveSpotsListView = findViewById(R.id.list_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        goToMyLocation = (ImageView) findViewById(R.id.go_to_my_location);
        mapListFAB = (FloatingActionButton) findViewById(R.id.map_list_fab);
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
        if (!Helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
        mMapView.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_list_fab:
                toggleMapListView();
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

    private void toggleMapListView() {
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

    @Override
    public void onInfoWindowClick(final Marker marker) {
        if (!marker.getPosition().equals(diveSpotMarker.getPosition())) {
            DiveCenterDetailsActivity.show(this, diveCentersMap.get(marker.getPosition()), logoPath, EventsTracker.SpotViewSource.FROM_MAP);
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (diveCentersClusterManager.onMarkerClick(marker) || marker.equals(diveSpotMarker)) {
            return true;
        }
        if (lastClickedMarker != null) {
            try {
                lastClickedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_dc));
            } catch (IllegalStateException e) {

            } catch (IllegalArgumentException e) {

            }
        }
        lastClickedMarker = marker;
        // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds_selected));
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dc_selected));
        if (diveCentersMap.get(marker.getPosition()) != null) {
            DDScannerApplication.bus.post(new DiveCenterMarkerClickEvent(diveCentersMap.get(marker.getPosition()), logoPath));
        }
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (lastClickedMarker != null) {
            // lastClickedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
            DDScannerApplication.bus.post(new OnMapClickEvent(lastClickedMarker, false));
            lastClickedMarker = null;
        } else {
            DDScannerApplication.bus.post(new OnMapClickEvent(lastClickedMarker, false));
        }
    }

    private void setMapView() {
        mMapView = (MapView) findViewById(R.id.mapView);
        Log.i(TAG, "mMapView inited");
        mMapView.onCreate(null);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                Log.i(TAG, "onMapReady, googleMap = " + googleMap);
                mGoogleMap = googleMap;
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        Log.i(TAG, "onMapLoaded");
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(diveSpotLatLng)
                                .zoom(8)
                                .build();
                        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                        diveCentersClusterManager = new DiveCentersClusterManager(DiveCentersActivity.this, mGoogleMap);
                        mGoogleMap.setOnInfoWindowClickListener(DiveCentersActivity.this);
                        mGoogleMap.setOnMarkerClickListener(DiveCentersActivity.this);
                        mGoogleMap.setOnMapClickListener(DiveCentersActivity.this);
                        mGoogleMap.setOnCameraChangeListener(diveCentersClusterManager);
                        diveSpotMarker = mGoogleMap.addMarker(new MarkerOptions().position(diveSpotLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds)).title(diveSpotName));
                        if (diveCenters != null) {
                            // This means we have already received dive centers
                            drawMarkers();
                        }
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

    private void drawMarkers() {
        if (diveCentersClusterManager != null) {
            for (DiveCenter diveCenter : diveCenters) {
                diveCentersClusterManager.addItem(diveCenter);
                diveCentersMap.put(diveCenter.getPosition(), diveCenter);
            }
            diveCentersClusterManager.cluster();
        } else {
            // This means map has not yet been initialized
        }
    }

    public void fillDiveSpotsList() {
        Collections.sort(diveCenters);
        diveCentersListAdapter = new DiveCentersListAdapter(diveCenters, logoPath, this);
        rc.setAdapter(diveCentersListAdapter);

        if (!diveCenters.isEmpty()) {
            rc.setVisibility(View.VISIBLE);
//            please.setVisibility(View.GONE);
        } else {
            rc.setVisibility(View.GONE);
            //  please.setVisibility(View.VISIBLE);
        }

    }

    @Subscribe
    public void hideDiveCenterInfo(OnMapClickEvent event) {
        if (event.getMarker() != null) {
            try {
                event.getMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_dc));
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
        Log.i(TAG, "location check: onLocationReady, request codes = " + event.getRequestCodes());
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
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_me)));
                        CircleOptions circleOptions = new CircleOptions()
                                .center(myLocation)
                                .radius(200)
                                .strokeColor(android.R.color.transparent)
                                .fillColor(Color.parseColor("#1A0668a1"));
                        circle = mGoogleMap.addCircle(circleOptions);
                    } else {
                        circle.setCenter(myLocation);
                        myLocationMarker.setPosition(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()));
                    }
                    break;

                case ActivitiesRequestCodes.REQUEST_CODE_MAP_LIST_FRAGMENT_GET_LOCATION_ON_FRAGMENT_START:
                    Log.i(TAG, "location check: GET_LOCATION_ON_FRAGMENT_START: event.getLocation() = " + event.getLocation() + " diveSpotsClusterManager = " + diveCentersClusterManager);
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

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_DIVE_CENTERS_CLUSTER_MANAGER_UNEXPECTED_ERROR:
            case DialogsRequestCodes.DRC_DIVE_CENTERS_CLUSTER_MANAGER_FAILED_TO_CONNECT:
                finish();
                break;
        }
    }
}
