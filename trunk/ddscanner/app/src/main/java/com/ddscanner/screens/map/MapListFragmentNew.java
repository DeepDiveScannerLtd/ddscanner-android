package com.ddscanner.screens.map;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.views.DiveSpotMapInfoViewNew;
import com.ddscanner.utils.Helpers;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
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
    MapboxMap mapboxMap;
    DiveSpotMapInfoViewNew diveSpotMapInfoView;
    MapFragmentManager mapFragmentManager;
    private int diveSpotInfoHeight;
    ProgressBar progressBar;
    RelativeLayout message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_map, container, false);
        mapView = view.findViewById(R.id.map_view);
        diveSpotMapInfoView = view.findViewById(R.id.dive_spot_map_info);
        progressBar = view.findViewById(R.id.request_progress);
        message = view.findViewById(R.id.toast);
        diveSpotInfoHeight = Math.round(Helpers.convertDpToPixel(93, getContext()));
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::setupMap);
        return view;
    }

    private void setupMap(MapboxMap mapboxMapNew) {
        mapFragmentManager = new MapFragmentManager(mapboxMapNew, this);
    }

    @Override
    public void markerClicked(Marker marker, DiveSpotShort diveSpotShort) {
        diveSpotMapInfoView.show(diveSpotShort, marker);
    }

    @Override
    public void loadData(DiveSpotsRequestMap diveSpotsRequestMap, ArrayList<String> sealifes) {
        DDScannerApplication.getInstance().getDdScannerRestClient(getActivity()).getDiveSpotsByArea(sealifes, diveSpotsRequestMap, diveSpotsResultListener);
    }

    @Override
    public void hideDiveSpotInfo() {
        diveSpotMapInfoView.hide(diveSpotInfoHeight);
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

}
