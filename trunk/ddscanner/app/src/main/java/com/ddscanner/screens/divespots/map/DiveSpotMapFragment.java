package com.ddscanner.screens.divespots.map;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.views.DiveSpotMapInfoView;
import com.ddscanner.utils.Helpers;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.Marker;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

public class DiveSpotMapFragment extends Fragment implements DiveSpotMapFragmentController {

    private DDScannerRestClient.ResultListener<List<DiveSpotShort>> diveSpotsResultListener = new DDScannerRestClient.ResultListener<List<DiveSpotShort>>() {
        @Override
        public void onSuccess(List<DiveSpotShort> result) {
            clusterManagerNew.updateDiveSpots((ArrayList<DiveSpotShort>) result);
            hideProgressView();
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

    private DiveSpotsClusterManagerNew clusterManagerNew;
    private MapView mapView;
    private GoogleMap googleMap;
    private DiveSpotMapInfoView diveSpotMapInfoView;
    private RelativeLayout zoomInMessage;
    private ProgressBar progressView;
    private int diveSpotInfoHeight;
    private FloatingActionButton mapListFAB;
    private FloatingActionButton addDsFab;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maplist, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapView);
        zoomInMessage= view.findViewById(R.id.toast);
        progressView = view.findViewById(R.id.request_progress);
        diveSpotInfoHeight = Math.round(Helpers.convertDpToPixel(93, getContext()));
        initMapView();
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
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
    }

    private void initMapView() {
        MapsInitializer.initialize(getActivity());
        mapView.onCreate(new Bundle());
        mapView.getMapAsync(googleMap1 -> {
            this.googleMap = googleMap1;
            clusterManagerNew = new DiveSpotsClusterManagerNew(getActivity(), this.googleMap, this);
        });
    }

    @Override
    public void showDiveSpotInfo(Marker marker, DiveSpotShort diveSpotShort) {
        diveSpotMapInfoView.show(diveSpotShort, marker);
    }

    @Override
    public void showDiveCenterInfo(Marker marker, DiveCenter diveCenter) {

    }

    @Override
    public void showProgressView() {
        progressView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showZoomInMessage() {
        zoomInMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDiveCenternfo() {

    }

    @Override
    public void hideDiveSpotInfo() {
        diveSpotMapInfoView.hide(diveSpotInfoHeight);
    }

    @Override
    public void hideProgressView() {
        progressView.setVisibility(View.GONE);
    }

    @Override
    public void hideZoomInMessage() {
        zoomInMessage.setVisibility(View.GONE);
    }

    @Override
    public void requestDiveSpots(ArrayList<String> sealifes, DiveSpotsRequestMap diveSpotsRequestMap) {
        DDScannerApplication.getInstance().getDdScannerRestClient(getActivity()).getDiveSpotsByArea(sealifes, diveSpotsRequestMap, diveSpotsResultListener);
    }
    
    
    
}
