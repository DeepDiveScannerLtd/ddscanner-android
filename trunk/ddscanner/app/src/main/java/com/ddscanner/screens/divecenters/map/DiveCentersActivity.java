package com.ddscanner.screens.divecenters.map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.constraint.ConstraintLayout;
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
import com.ddscanner.events.OnMapClickEvent;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.adapters.DiveCentersListAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.ui.managers.DiveCentersClusterManager;
import com.ddscanner.ui.views.DiveCenterInfoView;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DiveCentersActivity extends BaseAppCompatActivity implements View.OnClickListener, DialogClosedListener, DiveCentersMapContract {
    private static final String TAG = "DiveCentersActivity";
    private String diveSpotId;
    private LatLng diveSpotLatLng;
    private String diveSpotName;
    MapView mMapView;
    private MapboxMap mapboxMap;

    private FloatingActionButton mapListFAB;
    private DiveCenterInfoView diveCenterInfoView;
    private ImageView zoomIn;
    private ImageView zoomOut;
    private ImageView goToMyLocation;
    private RelativeLayout mapControlLayout;
    private DiveCenter diveCenter;
    private ProgressView progressBar;

    private View diveSpotsMapView;
    private View diveSpotsListView;

    private boolean isMapShown = true;

    // List mode member fields
    private RecyclerView rc;
    private DiveCentersListAdapter diveCentersListAdapter;
    private ArrayList<DiveCenter> diveCenters;
    private int infoWindowHeight;
    private RelativeLayout mainLayout;
    private ConstraintLayout no_dive_centers_layout;
    DiveCentersMapManager diveCentersMapManager;

    private DDScannerRestClient.ResultListener<ArrayList<DiveCenter>> diveCentersResponseEntityResultListener = new DDScannerRestClient.ResultListener<ArrayList<DiveCenter>>() {
        @Override
        public void onSuccess(ArrayList<DiveCenter> result) {
            diveCenters = result;
            if (diveCentersMapManager != null && !diveCentersMapManager.isDiveCentersDrawed) {
                diveCentersMapManager.drawMarkers(result);
            }
            fillDiveSpotsList();
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_DIVE_CENTERS_CLUSTER_MANAGER_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
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
        infoWindowHeight = Math.round(Helpers.convertDpToPixel(110, this));
        findViews();
        if (!getIntent().getBooleanExtra("is_someone_working_here", false)) {
            no_dive_centers_layout.setVisibility(View.VISIBLE);
            return;
        }
        mainLayout.setVisibility(View.VISIBLE);
        setMapView();
        EventsTracker.trackDiveCentersMapView();
        diveSpotId = getIntent().getStringExtra("id");
        diveSpotLatLng = getIntent().getParcelableExtra("LATLNG");
        diveSpotName = getIntent().getStringExtra("NAME");
        EventsTracker.trackDiveCentersListView();
        toggleMapListView();

        DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenters(diveSpotId, diveCentersResponseEntityResultListener);
    }

    private void findViews() {
        diveSpotsMapView = findViewById(R.id.map_view);
        diveSpotsListView = findViewById(R.id.list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.dive_centers);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);

        // Map mode
        no_dive_centers_layout = findViewById(R.id.no_contacts_layout);
        mainLayout = findViewById(R.id.main_layout_view);
        diveCenterInfoView = findViewById(R.id.dive_spot_info_layout);
        diveCenterInfoView.hide(infoWindowHeight);
        zoomIn = findViewById(R.id.zoom_plus);
        zoomOut = findViewById(R.id.zoom_minus);
        goToMyLocation = findViewById(R.id.go_to_my_location);
        mapListFAB = findViewById(R.id.map_list_fab);
        progressBar = findViewById(R.id.progressBar);

        // List mode
        rc = findViewById(R.id.cv);
        mapListFAB = findViewById(R.id.map_list_fab);
        mapListFAB.setOnClickListener(this);
        rc.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc.setLayoutManager(linearLayoutManager);
        rc.setItemAnimator(new DefaultItemAnimator());

    }

    public static void show(Context context, LatLng latLng, String name, String id, boolean isSomebodyWorkingHere) {
        Intent intent = new Intent(context, DiveCentersActivity.class);
        intent.putExtra("LATLNG", latLng);
        intent.putExtra("NAME", name);
        intent.putExtra("id", id);
        intent.putExtra("is_someone_working_here", isSomebodyWorkingHere);
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
        mMapView.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_list_fab:
                toggleMapListView();
                break;
            case R.id.zoom_plus:
                mapboxMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.zoom_minus:
                mapboxMap.animateCamera(CameraUpdateFactory.zoomOut());
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
                UserProfileActivity.show(this, diveCenter.getId(), 0);
                break;
        }
    }

    private void toggleMapListView() {
        if (isMapShown) {
            EventsTracker.trackDiveCentersListView();
            diveSpotsMapView.setVisibility(View.GONE);
            diveSpotsListView.setVisibility(View.VISIBLE);
            mapListFAB.setImageResource(R.drawable.ic_acb_map);
        } else {
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
        mMapView.onStart();
//        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
//        DDScannerApplication.bus.unregister(this);
    }


    private void setMapView() {
        mMapView = findViewById(R.id.mapView);
        Log.i(TAG, "mMapView inited");
        mMapView.onCreate(null);
        mMapView.getMapAsync(this::initMap);
        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);
        goToMyLocation.setOnClickListener(this);
        mapControlLayout = findViewById(R.id.map_control_layout);
    }

    private void initMap(MapboxMap mapboxMap) {
        diveCentersMapManager = new DiveCentersMapManager(mapboxMap, this, this);
        if (diveCenters != null && !diveCentersMapManager.isDiveCentersDrawed) {
            diveCentersMapManager.drawMarkers(diveCenters);
        }
        mapboxMap.addMarker(new MarkerViewOptions().icon(IconFactory.getInstance(this).fromResource(R.drawable.ic_ds)).position(diveSpotLatLng));
    }

    private void drawMarkers() {

    }

    public void fillDiveSpotsList() {
        Collections.sort(diveCenters);
        diveCentersListAdapter = new DiveCentersListAdapter(diveCenters, this);
        rc.setAdapter(diveCentersListAdapter);

        if (!diveCenters.isEmpty()) {
            rc.setVisibility(View.VISIBLE);
//            please.setVisibility(View.GONE);
        } else {
            rc.setVisibility(View.GONE);
            //  please.setVisibility(View.VISIBLE);
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

    @Override
    public void showInfoWindow(DiveCenter diveCenter) {
        mapListFAB.animate().translationY(-infoWindowHeight);
        diveCenterInfoView.show(diveCenter);
    }

    @Override
    public void hideInfoWindow() {
        mapListFAB.animate().translationY(0);
        diveCenterInfoView.hide(infoWindowHeight);
    }

}
