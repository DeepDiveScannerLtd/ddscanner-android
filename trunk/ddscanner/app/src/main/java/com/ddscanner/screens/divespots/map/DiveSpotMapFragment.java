package com.ddscanner.screens.divespots.map;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.BaseMapEntity;
import com.ddscanner.entities.MapResponseEntity;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.events.OpenAddDiveSpotActivity;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.screens.divespots.list.DiveSpotsListAdapter;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.ddscanner.ui.views.DiveCenterInfoView;
import com.ddscanner.ui.views.DiveSpotMapInfoView;
import com.ddscanner.utils.Helpers;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class DiveSpotMapFragment extends Fragment implements DiveSpotMapFragmentController {

    private DDScannerRestClient.ResultListener<MapResponseEntity> diveSpotsResultListener = new DDScannerRestClient.ResultListener<MapResponseEntity>() {
        @Override
        public void onSuccess(MapResponseEntity result) {
            clusterManagerNew.updateDiveSpots(result.getAllEntities());
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
    private MapView googleMapView;
    private GoogleMap googleMap;
    private DiveSpotMapInfoView diveSpotMapInfoView;
    private RelativeLayout zoomInMessage;
    private ProgressBar progressView;
    private int diveSpotInfoHeight;
    private FloatingActionButton mapListFAB;
    private FloatingActionButton addDsFab;
    private DiveCenterInfoView diveCenterInfoView;
    private View listView;
    private View mapView;
    private TabLayout tabLayout;
    private boolean isZoomInMessageVisible = false;
    private DiveSpotsMapListAdapter diveSpotsListAdapter;
    private RelativeLayout continueShowingMap;
    private RecyclerView diveSpotsList;
    private RecyclerView diveCentersList;
    private DiveSpotsMapDiveCenterListAdapter diveSpotsMapDiveCenterListAdapter;
    private RelativeLayout noDataAvailable;
    private TextView noDataText;
    private ImageView zoomPlus;
    private ImageView zoomMinus;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maplist, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        googleMapView = view.findViewById(R.id.mapView);
        zoomInMessage= view.findViewById(R.id.toast);
        progressView = view.findViewById(R.id.request_progress);
        diveSpotMapInfoView = view.findViewById(R.id.dive_spot_info_layout);
        diveCenterInfoView = view.findViewById(R.id.dive_center_info_layout);
        diveSpotMapInfoView.setOnClickListener(this::onDivSpotInfoClicked);
        diveCenterInfoView.setOnClickListener(this::onDiveCenterInfoClicked);
        mapListFAB = view.findViewById(R.id.map_list_fab);
        addDsFab = view.findViewById(R.id.add_ds_fab);
        addDsFab.setOnClickListener(this::onAddDsClicked);
        listView = view.findViewById(R.id.list_view);
        mapView = view.findViewById(R.id.map_view);
        tabLayout = view.findViewById(R.id.tab_layout);
        diveSpotInfoHeight = Math.round(Helpers.convertDpToPixel(93, getContext()));
        mapListFAB.setOnClickListener(this::mapListFabClicked);
        diveSpotsListAdapter = new DiveSpotsMapListAdapter(getActivity());
        diveSpotsMapDiveCenterListAdapter = new DiveSpotsMapDiveCenterListAdapter(getActivity());
        diveSpotsList = view.findViewById(R.id.recycler_view);
        diveCentersList = view.findViewById(R.id.dc_recycler_view);
        noDataAvailable = view.findViewById(R.id.no_data);
        noDataText = view.findViewById(R.id.text);
        zoomMinus = view.findViewById(R.id.zoom_minus);
        zoomPlus = view.findViewById(R.id.zoom_plus);
        initLists();
        initMapView();
        initTabLayout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        googleMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        googleMapView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        googleMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        googleMapView.onResume();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        googleMapView.onSaveInstanceState(outState);
    }

    private void initLists() {
        diveSpotsList.setLayoutManager(new LinearLayoutManager(getContext()));
        diveCentersList.setLayoutManager(new LinearLayoutManager(getContext()));
        diveSpotsList.setAdapter(diveSpotsListAdapter);
        diveCentersList.setAdapter(diveSpotsMapDiveCenterListAdapter);
    }

    private void initTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("DIVE SPOTS"));
        tabLayout.addTab(tabLayout.newTab().setText("DIVE CENTERS"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        diveCentersList.setVisibility(View.GONE);
                        diveSpotsList.setVisibility(View.VISIBLE);
                        updateAccordingCountOfDiveSpotItems();
                        break;
                    case 1:
                        diveSpotsList.setVisibility(View.GONE);
                        diveCentersList.setVisibility(View.VISIBLE);
                        updateAccordingCountOfDCsItems();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initMapView() {
        MapsInitializer.initialize(getActivity());
        googleMapView.onCreate(new Bundle());
        googleMapView.getMapAsync(googleMap1 -> {
            this.googleMap = googleMap1;
            clusterManagerNew = new DiveSpotsClusterManagerNew(getActivity(), this.googleMap, this);
            zoomPlus.setOnClickListener(this::onZoomPlusClicked);
            zoomMinus.setOnClickListener(this::onZoomMinusClicked);
        });
    }

    @Override
    public void showDiveSpotInfo(Marker marker, BaseMapEntity diveSpotShort) {
        moveControlsUp();
        diveCenterInfoView.hide(diveSpotInfoHeight);
        diveSpotMapInfoView.show(diveSpotShort, marker);
    }

    @Override
    public void showDiveCenterInfo(Marker marker, BaseMapEntity diveCenter) {
        diveSpotMapInfoView.hide(diveSpotInfoHeight);
        diveCenterInfoView.show(diveCenter, marker);
        moveControlsUp();
    }

    @Override
    public void showProgressView() {
        progressView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showZoomInMessage() {
        zoomInMessage.setVisibility(View.VISIBLE);
        isZoomInMessageVisible = true;
    }

    @Override
    public void hideDiveCenternfo() {
        moveControlsDown();
        diveCenterInfoView.hide(diveSpotInfoHeight);
    }

    @Override
    public void hideDiveSpotInfo() {
        moveControlsDown();
        diveSpotMapInfoView.hide(diveSpotInfoHeight);
    }

    @Override
    public void hideProgressView() {
        progressView.setVisibility(View.GONE);
    }

    @Override
    public void hideZoomInMessage() {
        zoomInMessage.setVisibility(View.GONE);
        isZoomInMessageVisible = false;
    }

    @Override
    public void requestDiveSpots(ArrayList<String> sealifes, DiveSpotsRequestMap diveSpotsRequestMap) {
        DDScannerApplication.getInstance().getDdScannerRestClient(getActivity()).getMapItemsByArea(sealifes, diveSpotsRequestMap, diveSpotsResultListener);
    }
    
    public FloatingActionButton getMapListFAB() {
        return mapListFAB;
    }

    private void moveControlsUp() {
        addDsFab.animate().translationY(-diveSpotInfoHeight);
        mapListFAB.animate().translationY(-diveSpotInfoHeight);
    }

    private void moveControlsDown() {
        mapListFAB.animate().translationY(0);
        addDsFab.animate().translationY(0);
    }

    public void mapListFabClicked(View view) {
        if (mapView.getVisibility() == View.VISIBLE) {
            showListView();
        } else {
            showMapView();
        }
    }

    private void showListView() {
        mapView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        addDsFab.setVisibility(View.GONE);
        mapListFAB.setImageResource(R.drawable.ic_acb_map);
        if (diveCenterInfoView.isShown() || diveSpotMapInfoView.isShown()) {
            mapListFAB.setY(mapListFAB.getY() + diveSpotInfoHeight);
            addDsFab.setY(addDsFab.getY() + diveSpotInfoHeight);
        }
        if (isZoomInMessageVisible) {
            zoomInMessage.setVisibility(View.GONE);
        }
        updateListsState();
    }

    private void showMapView() {
        listView.setVisibility(View.GONE);
        mapView.setVisibility(View.VISIBLE);
        addDsFab.setVisibility(View.VISIBLE);
        mapListFAB.setImageResource(R.drawable.ic_acb_list);
        if (diveCenterInfoView.isShown() || diveSpotMapInfoView.isShown()) {
            mapListFAB.setY(mapListFAB.getY() - diveSpotInfoHeight);
            addDsFab.setY(addDsFab.getY() - diveSpotInfoHeight);
        }
        if (isZoomInMessageVisible) {
            zoomInMessage.setVisibility(View.VISIBLE);
        }
    }

    private void updateListsState() {
        diveSpotsListAdapter.setList(clusterManagerNew.getVisibleMarkersList(false));
        diveSpotsMapDiveCenterListAdapter.setList(clusterManagerNew.getVisibleMarkersList(true));
        switch (tabLayout.getSelectedTabPosition()) {
            case 0:
                updateAccordingCountOfDiveSpotItems();
                break;
            case 1:
                updateAccordingCountOfDCsItems();
                break;
        }

    }

    private void updateAccordingCountOfDiveSpotItems() {
        if (diveSpotsListAdapter.getItemCount() > 0) {
            diveSpotsList.setVisibility(View.VISIBLE);
            noDataAvailable.setVisibility(View.GONE);
        } else {
            diveSpotsList.setVisibility(View.GONE);
            noDataAvailable.setVisibility(View.VISIBLE);
            noDataText.setText(R.string.there_are_no_dive_spots_here);
        }
    }

    private void updateAccordingCountOfDCsItems() {
        if (diveSpotsMapDiveCenterListAdapter.getItemCount() > 0) {
            diveCentersList.setVisibility(View.VISIBLE);
            noDataAvailable.setVisibility(View.GONE);
        } else {
            diveCentersList.setVisibility(View.GONE);
            noDataAvailable.setVisibility(View.VISIBLE);
            noDataText.setText(R.string.there_are_no_dive_centers_here);
        }
    }



    public void onDiveCenterInfoClicked(View view) {
        UserProfileActivity.show(getContext(), diveCenterInfoView.getLastDcId(), 0);
    }

    public void onDivSpotInfoClicked(View view) {
        DiveSpotDetailsActivity.show(getContext(), diveSpotMapInfoView.getLastDiveSpotId(), EventsTracker.SpotViewSource.FROM_MAP);
    }

    public void onAddDsClicked(View view) {
        DDScannerApplication.bus.post(new OpenAddDiveSpotActivity());
    }

    public void onZoomPlusClicked(View view) {
        clusterManagerNew.mapZoomPlus();
    }

    public void onZoomMinusClicked(View view) {
        clusterManagerNew.mapZoomMinus();
    }

}
