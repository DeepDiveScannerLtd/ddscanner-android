package com.ddscanner.screens.divecenter.spots;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.events.OpenAddDiveSpotActivity;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespots.list.DiveSpotsListAdapter;
import com.ddscanner.screens.map.MapFragmentContract;
import com.ddscanner.screens.map.MapFragmentManager;
import com.ddscanner.screens.profile.divecenter.DiveCenterSpotsActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.ui.views.DiveSpotMapInfoViewNew;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;

public class DCSpotsActivity extends BaseAppCompatActivity implements MapFragmentContract.View {

    MapView mapView;
    DiveSpotMapInfoViewNew diveSpotMapInfoView;
    RelativeLayout relativeMapView;
    RelativeLayout listView;
    FloatingActionButton mapListFab;
    FloatingActionButton addDiveSpotFab;
    RecyclerView diveSpotsList;
    Button continueShowMap;
    RelativeLayout pleaseContinueMap;
    DiveSpotsListAdapter diveSpotsListAdapter;
    int diveSpotInfoHeight;
    MapFragmentManager mapFragmentManager;
    MaterialDialog materialDialog;
    LatLng diveCenterLocation;
    String diveSpotId;

    private DDScannerRestClient.ResultListener<ArrayList<DiveSpotShort>> diveSpotsResultListener = new DDScannerRestClient.ResultListener<ArrayList<DiveSpotShort>>() {
        @Override
        public void onSuccess(ArrayList<DiveSpotShort> result) {
            materialDialog.dismiss();
            mapFragmentManager.updateDiveSpots(result);
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_DIVE_CENTER_SPOTS_ACTIVITY_HIDE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_DIVE_CENTER_SPOTS_ACTIVITY_HIDE, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_DIVE_CENTER_SPOTS_ACTIVITY_HIDE, false);
        }

    };

    public static void show(Context context, String id, LatLng latLng) {
        Intent intent = new Intent(context, DCSpotsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("place", latLng);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dc_spots);
        diveCenterLocation = getIntent().getParcelableExtra("place");
        diveSpotId = getIntent().getStringExtra("id");
        findViews(savedInstanceState);
        setupToolbar(R.string.diespors, R.id.toolbar);
    }

    private void findViews(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        diveSpotMapInfoView = findViewById(R.id.dive_spot_map_info);
        relativeMapView = findViewById(R.id.map_view_relative);
        listView = findViewById(R.id.list_view);
        mapListFab = findViewById(R.id.map_list_fab);
        addDiveSpotFab = findViewById(R.id.add_ds_fab);
        addDiveSpotFab.setVisibility(View.GONE);
        diveSpotInfoHeight = Math.round(Helpers.convertDpToPixel(93, this));
        diveSpotMapInfoView.hide(diveSpotInfoHeight);
        pleaseContinueMap = findViewById(R.id.please);
        diveSpotsList = findViewById(R.id.dive_spots_list);
        continueShowMap = findViewById(R.id.showMapContinue);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::setupMap);
        mapListFab.setOnClickListener(this::updateViewState);
        continueShowMap.setOnClickListener(view1 -> showMapView());
        diveSpotsList.setLayoutManager(new LinearLayoutManager(this));
        diveSpotsListAdapter = new DiveSpotsListAdapter(this);
        addDiveSpotFab.setOnClickListener(view1 -> DDScannerApplication.bus.post(new OpenAddDiveSpotActivity()));
        diveSpotsList.setAdapter(diveSpotsListAdapter);
        materialDialog = Helpers.getMaterialDialog(this);
        materialDialog.show();
    }

    private void setupMap(MapboxMap mapboxMapNew) {
        mapFragmentManager = new MapFragmentManager(mapboxMapNew, this, this, false);
        mapboxMapNew.addMarker(new MarkerViewOptions().icon(IconFactory.getInstance(this).fromResource(R.drawable.ic_dc)).position(diveCenterLocation));
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenterDiveSpotsList(diveSpotsResultListener, diveSpotId);
        mapboxMapNew.moveCamera(CameraUpdateFactory.newLatLngZoom(diveCenterLocation, 7));
    }

    @Override
    public void hidePogressView() {

    }

    @Override
    public void hideDiveSpotInfo() {
        mapListFab.animate().translationY(0);
        diveSpotMapInfoView.hide(diveSpotInfoHeight);
    }

    @Override
    public void hideErrorMessage() {

    }

    @Override
    public void showPogressView() {

    }

    @Override
    public void showErrorMessage() {

    }

    @Override
    public void loadData(DiveSpotsRequestMap diveSpotsRequestMap, ArrayList<String> sealifes) {

    }

    @Override
    public void markerClicked(DiveSpotShort diveSpotShort) {
        mapListFab.animate().translationY(-diveSpotInfoHeight);
        diveSpotMapInfoView.show(diveSpotShort);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        mapView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();

        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void showListView() {
        relativeMapView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        mapListFab.setVisibility(View.VISIBLE);
        addDiveSpotFab.setVisibility(View.GONE);
        mapListFab.setTranslationY(0);
        mapListFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_acb_map));
        if (mapFragmentManager.getVisibleSpotsList().size() > 0) {
            pleaseContinueMap.setVisibility(View.GONE);
            diveSpotsList.setVisibility(View.VISIBLE);
            diveSpotsListAdapter.setData(mapFragmentManager.getVisibleSpotsList());
            mapListFab.setVisibility(View.VISIBLE);
            return;
        }
        pleaseContinueMap.setVisibility(View.VISIBLE);
        diveSpotsList.setVisibility(View.GONE);
        mapListFab.setVisibility(View.GONE);
    }

    private void showMapView() {
        mapListFab.setVisibility(View.VISIBLE);
        mapListFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_acb_list));
        if (diveSpotMapInfoView.isShown()) {
            mapListFab.setTranslationY(-diveSpotInfoHeight);
        }
        relativeMapView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        addDiveSpotFab.setVisibility(View.VISIBLE);
    }

    private void updateViewState(View view) {
        if (relativeMapView.getVisibility() == View.VISIBLE) {
            showListView();
            return;
        }
        showMapView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
