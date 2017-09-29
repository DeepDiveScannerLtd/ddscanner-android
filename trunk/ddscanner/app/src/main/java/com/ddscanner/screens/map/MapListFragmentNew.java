package com.ddscanner.screens.map;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.events.OpenAddDiveSpotActivity;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespots.list.DiveSpotsListAdapter;
import com.ddscanner.ui.views.DiveSpotMapInfoViewNew;
import com.ddscanner.utils.Helpers;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;

public class MapListFragmentNew extends Fragment implements MapFragmentContract.View {

    private DDScannerRestClient.ResultListener<List<DiveSpotShort>> diveSpotsResultListener = new DDScannerRestClient.ResultListener<List<DiveSpotShort>>() {
        @Override
        public void onSuccess(List<DiveSpotShort> result) {
            mapFragmentManager.updateDiveSpots((ArrayList<DiveSpotShort>) result);
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }

        @Override
        public void onInternetConnectionClosed() {

        }
    };

    MapView mapView;
    DiveSpotMapInfoViewNew diveSpotMapInfoView;
    MapFragmentManager mapFragmentManager;
    private int diveSpotInfoHeight;
    ProgressBar progressBar;
    RelativeLayout message;
    FloatingActionButton mapListFab;
    FloatingActionButton addDiveSpotFab;
    RelativeLayout relativeMapView;
    RelativeLayout listView;
    RecyclerView diveSpotsList;
    Button continueShowMap;
    RelativeLayout pleaseContinueMap;
    DiveSpotsListAdapter diveSpotsListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_map, container, false);
        mapView = view.findViewById(R.id.map_view);
        diveSpotMapInfoView = view.findViewById(R.id.dive_spot_map_info);
        progressBar = view.findViewById(R.id.request_progress);
        message = view.findViewById(R.id.toast);
        mapListFab = view.findViewById(R.id.map_list_fab);
        addDiveSpotFab = view.findViewById(R.id.add_ds_fab);
        relativeMapView = view.findViewById(R.id.map_view_relative);
        listView = view.findViewById(R.id.list_view);
        diveSpotInfoHeight = Math.round(Helpers.convertDpToPixel(93, getContext()));
        diveSpotMapInfoView.hide(diveSpotInfoHeight);
        pleaseContinueMap = view.findViewById(R.id.please);
        diveSpotsList = view.findViewById(R.id.dive_spots_list);
        continueShowMap = view.findViewById(R.id.showMapContinue);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::setupMap);
        mapListFab.setOnClickListener(this::updateViewState);
        continueShowMap.setOnClickListener(view1 -> showMapView());
        diveSpotsList.setLayoutManager(new LinearLayoutManager(getContext()));
        diveSpotsListAdapter = new DiveSpotsListAdapter(getActivity());
        addDiveSpotFab.setOnClickListener(view1 -> DDScannerApplication.bus.post(new OpenAddDiveSpotActivity()));
        diveSpotsList.setAdapter(diveSpotsListAdapter);
        return view;
    }

    private void setupMap(MapboxMap mapboxMapNew) {
        mapFragmentManager = new MapFragmentManager(mapboxMapNew, this, getContext(), true);
    }

    @Override
    public void markerClicked(DiveSpotShort diveSpotShort) {
        diveSpotMapInfoView.show(diveSpotShort);
        mapListFab.animate().translationY(-diveSpotInfoHeight);
        addDiveSpotFab.animate().translationY(-diveSpotInfoHeight);
    }

    @Override
    public void loadData(DiveSpotsRequestMap diveSpotsRequestMap, ArrayList<String> sealifes) {
        DDScannerApplication.getInstance().getDdScannerRestClient(getActivity()).getDiveSpotsByArea(sealifes, diveSpotsRequestMap, diveSpotsResultListener);
    }

    @Override
    public void hideDiveSpotInfo() {
        diveSpotMapInfoView.hide(diveSpotInfoHeight);
        mapListFab.animate().translationY(0);
        addDiveSpotFab.animate().translationY(0);
    }

    @Override
    public void showErrorMessage() {
        message.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideErrorMessage() {
        message.setVisibility(View.GONE);
    }

    @Override
    public void showPogressView() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePogressView() {
        progressBar.setVisibility(View.GONE);
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

    private void updateViewState(View view) {
        if (relativeMapView.getVisibility() == View.VISIBLE) {
            showListView();
            return;
        }
        showMapView();
    }

    private void showListView() {
        relativeMapView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        mapListFab.setVisibility(View.VISIBLE);
        addDiveSpotFab.setVisibility(View.GONE);
        mapListFab.setTranslationY(0);
        mapListFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_acb_map));
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
        mapListFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_acb_list));
        if (diveSpotMapInfoView.isShown()) {
            mapListFab.setTranslationY(-diveSpotInfoHeight);
        }
        relativeMapView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        addDiveSpotFab.setVisibility(View.VISIBLE);
    }


    public void moveCameraToBounds(LatLngBounds latLngBounds) {
        mapFragmentManager.moveCameraToPosition(latLngBounds);
    }

    public void diveSpotAdded(LatLng latLng) {
        mapFragmentManager.diveSpotAdded(latLng);
    }

}
