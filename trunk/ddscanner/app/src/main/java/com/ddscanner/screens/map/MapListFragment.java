package com.ddscanner.screens.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.events.CloseInfoWindowEvent;
import com.ddscanner.events.CloseListEvent;
import com.ddscanner.events.InfowWindowOpenedEvent;
import com.ddscanner.events.ListOpenedEvent;
import com.ddscanner.events.LocationReadyEvent;
import com.ddscanner.events.MapViewInitializedEvent;
import com.ddscanner.events.MarkerClickEvent;
import com.ddscanner.events.OnMapClickEvent;
import com.ddscanner.events.OpenAddDiveSpotActivity;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.screens.divespots.list.DiveSpotsListAdapter;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.ui.views.DiveSpotMapInfoView;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Helpers;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
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
import java.util.List;

public class MapListFragment extends Fragment implements View.OnClickListener, FragmentMapContract {

    private static final String TAG = MapListFragment.class.getName();

    private View view;
    private View diveSpotsMapView;
    private View diveSpotsListView;

    // Map mode member fields
    private boolean isMapShown = true; // true - map mode, false - list mode

    private GoogleMap mGoogleMap;
    private DiveSpotsClusterManager diveSpotsClusterManager;
    MapView mMapView;
    private Location userLocationOnFragmentStart;

    private RelativeLayout toast;
    private ProgressBar progressBar;
    private FloatingActionButton mapListFAB;
    private FloatingActionButton addDsFab;
    private DiveSpotMapInfoView diveSpotInfo;
    private ImageView zoomIn;
    private ImageView zoomOut;
    private ImageView goToMyLocation;
    private Long lastDiveSpotId;
    private RelativeLayout mapControlLayout;
    private Marker myLocationMarker;
    private Circle circle;
    private ProgressView progressBarMyLocation;
    private boolean isToastMessageVisible;

    public BaseAppCompatActivity baseAppCompatActivity;

    // List mode member fields
    private RecyclerView recyclerView;
    private RelativeLayout please;
    private DiveSpotsListAdapter diveSpotsListAdapter;
    private int diveSpotInfoHeight;

    private DDScannerRestClient.ResultListener<List<DiveSpotShort>> getDiveSpotsByAreaResultListener = new DDScannerRestClient.ResultListener<List<DiveSpotShort>>() {
        @Override
        public void onSuccess(List<DiveSpotShort> diveSpotShorts) {
            hideProgressBar();
            hideMessageToZoom();
            diveSpotsClusterManager.updateDiveSpots((ArrayList<DiveSpotShort>) diveSpotShorts);
            if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsListTutorialWatched()) {
                showListTutorial();
                DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsListTutorialWatched();
            }
//            parentFragment.fillDiveSpots(getVisibleMarkersList((ArrayList<DiveSpotShort>) diveSpotShorts));
        }

        @Override
        public void onConnectionFailure() {
            hideProgressBar();
            UserActionInfoDialogFragment.show(getFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed_while_getting_dive_spots, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            hideProgressBar();
            Helpers.handleUnexpectedServerError(getFragmentManager(), url, errorMessage, R.string.error_server_error_title, R.string.error_unexpected_error);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.show(getFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof BaseAppCompatActivity) {
            baseAppCompatActivity = (BaseAppCompatActivity) context;
        } else {
            throw new RuntimeException("MapListFragment: activity must extend BaseAppCompatActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onPause() {
        super.onPause();

        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        goToMyLocation.setVisibility(View.VISIBLE);
        progressBarMyLocation.setVisibility(View.GONE);
        mMapView.onResume();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);

        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maplist, container, false);
        diveSpotInfoHeight = Math.round(Helpers.convertDpToPixel(93, getContext()));
        diveSpotsListAdapter = new DiveSpotsListAdapter(getActivity());
        findViews();
        setMapView();
        Log.i(TAG, "MapListFragment getLocation 1");
        return view;
    }

    private void findViews() {
        diveSpotsMapView = view.findViewById(R.id.map_view);
        diveSpotsListView = view.findViewById(R.id.list_view);
        toast = view.findViewById(R.id.toast);
        progressBar = view.findViewById(R.id.request_progress);

        // Map mode
        diveSpotInfo = view.findViewById(R.id.dive_spot_info_layout);
        zoomIn = view.findViewById(R.id.zoom_plus);
        zoomOut = view.findViewById(R.id.zoom_minus);
        goToMyLocation = view.findViewById(R.id.go_to_my_location);
        mapListFAB = view.findViewById(R.id.map_list_fab);
        addDsFab = view.findViewById(R.id.add_ds_fab);
        progressBarMyLocation = view.findViewById(R.id.progressBar);

        // List mode
        recyclerView = view.findViewById(R.id.cv);
        recyclerView.setAdapter(diveSpotsListAdapter);
        please = view.findViewById(R.id.please);
        mapListFAB = view.findViewById(R.id.map_list_fab);
        addDsFab = view.findViewById(R.id.add_ds_fab);
        addDsFab.setOnClickListener(this);
        mapListFAB.setOnClickListener(this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        android.widget.Button continueShowMap = view.findViewById(R.id.showMapContinue);
        continueShowMap.setOnClickListener(this);
        diveSpotInfo.animate().translationY(diveSpotInfoHeight);
    }

    @SuppressLint("RestrictedApi")
    private void setMapView() {
        MapsInitializer.initialize(getActivity());
        addDsFab.setOnClickListener(this);
        mapListFAB.setOnClickListener(this);
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(new Bundle());
        mMapView.getMapAsync(googleMap -> {
            Log.i(TAG, "onMapReady, googleMap = " + googleMap);
            mGoogleMap = googleMap;
            mGoogleMap.setOnMapLoadedCallback(() -> {
                Log.i(TAG, "location check: onMapLoaded, userLocationOnFragmentStart = " + userLocationOnFragmentStart);
                diveSpotsClusterManager = new DiveSpotsClusterManager(getActivity(), mGoogleMap, toast, progressBar, MapListFragment.this, this);
                mGoogleMap.setOnMarkerClickListener(diveSpotsClusterManager);
                mGoogleMap.setOnCameraChangeListener(diveSpotsClusterManager);
                DDScannerApplication.bus.post(new MapViewInitializedEvent());
                if (userLocationOnFragmentStart != null) {
                    // this means location has already been received
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
                            new LatLng(userLocationOnFragmentStart.getLatitude() - 1, userLocationOnFragmentStart.getLongitude() - 1),
                            new LatLng(userLocationOnFragmentStart.getLatitude() + 1, userLocationOnFragmentStart.getLongitude() + 1)
                    ), 0));
                }
            });

        });
        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);
        goToMyLocation.setOnClickListener(this);
        diveSpotInfo.setOnClickListener(this);
        mapControlLayout = view.findViewById(R.id.map_control_layout);
    }

    public void showListTutorial() {
        DDScannerApplication.getInstance().getTutorialHelper().showListButtonTutorial(getActivity(), mapListFAB);
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
                if (isMapShown) {
                    fillDiveSpots();
                    if (diveSpotInfo.getVisibility() == View.VISIBLE) {
                        mapListFAB.setY(mapListFAB.getY() + diveSpotInfo.getHeight());
                        addDsFab.setY(addDsFab.getY() + diveSpotInfo.getHeight());
                    }
                    if (toast.getVisibility() == View.VISIBLE) {
                        isToastMessageVisible = true;
                    }
                    toast.setVisibility(View.GONE);
                    if (please.getVisibility() == View.VISIBLE) {
                        addDsFab.setVisibility(View.GONE);
                        mapListFAB.setVisibility(View.GONE);
                    }
                    diveSpotsMapView.setVisibility(View.GONE);
                    EventsTracker.trackDiveSpotListView();
                    diveSpotsListView.setVisibility(View.VISIBLE);
                    DDScannerApplication.bus.post(new ListOpenedEvent());
                    mapListFAB.setImageResource(R.drawable.ic_acb_map);
                } else {
                    diveSpotsListAdapter.clearData();
                    if (isToastMessageVisible) {
                        toast.setVisibility(View.VISIBLE);
                        isToastMessageVisible = false;
                    }
                    addDsFab.setVisibility(View.VISIBLE);
                    mapListFAB.setVisibility(View.VISIBLE);
                    if (diveSpotInfo.getVisibility() == View.VISIBLE) {
                        mapListFAB.setY(mapListFAB.getY() - diveSpotInfo.getHeight());
                        addDsFab.setY(addDsFab.getY() - diveSpotInfo.getHeight());
                    }
                    EventsTracker.trackDiveSpotMapView();
                    diveSpotsMapView.setVisibility(View.VISIBLE);
                    diveSpotsListView.setVisibility(View.GONE);
                    mapListFAB.setImageResource(R.drawable.ic_acb_list);
                }
                isMapShown = !isMapShown;
                break;
            case R.id.zoom_plus:
                if (diveSpotsClusterManager != null) {
                    diveSpotsClusterManager.mapZoomPlus();
                }
                break;
            case R.id.zoom_minus:
                if (diveSpotsClusterManager != null) {
                    diveSpotsClusterManager.mapZoomMinus();
                }
                break;
            case R.id.go_to_my_location:
                goToMyLocation.setVisibility(View.GONE);
                progressBarMyLocation.setVisibility(View.VISIBLE);
                Log.i(TAG, "MapListFragment getLocation 2");
                baseAppCompatActivity.getLocation(ActivitiesRequestCodes.REQUEST_CODE_MAP_LIST_FRAGMENT_GO_TO_CURRENT_LOCATION);
                break;
            case R.id.dive_spot_info_layout:
                DiveSpotDetailsActivity.show(getActivity(), String.valueOf(lastDiveSpotId), EventsTracker.SpotViewSource.FROM_MAP);
                break;
            case R.id.add_ds_fab:
                DDScannerApplication.bus.post(new OpenAddDiveSpotActivity());
                break;
            case R.id.showMapContinue:
                mapListFAB.performClick();
                break;
        }
    }

    @UiThread
    @Subscribe
    public void getDiveSpotInfow(MarkerClickEvent event) {
        DDScannerApplication.bus.post(new InfowWindowOpenedEvent(null));
        mapControlLayout.animate().translationY(-diveSpotInfoHeight);
        addDsFab.animate().translationY(-diveSpotInfoHeight);
        mapListFAB.animate().translationY(-diveSpotInfoHeight);
        lastDiveSpotId = event.getDiveSpotShort().getId();
        diveSpotInfo.show(event.getDiveSpotShort());
    }

    @Subscribe
    public void dismissInfoWindowWhenCameraZoomingOut(CloseInfoWindowEvent event) {
        if (diveSpotsClusterManager != null && diveSpotsClusterManager.getLastClickedMarker() != null) {
            try {
                if (diveSpotsClusterManager.isLastClickedMarkerNew()) {
                    diveSpotsClusterManager.getLastClickedMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds_new));
                } else {
                    diveSpotsClusterManager.getLastClickedMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
                }
            } catch (NullPointerException ignored) {

            } catch (IllegalArgumentException ignored) {

            }
        }
        mapControlLayout.animate().translationY(0);
        addDsFab.animate().translationY(0);
        mapListFAB.animate().translationY(0);
        diveSpotInfo.hide(diveSpotInfoHeight);
    }

    @Subscribe
    public void hideDiveSpotinfo(OnMapClickEvent event) {
        if (event.getMarker() != null) {
            try {

                if (event.getIsNew()) {
                    event.getMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds_new));
                } else {
                    event.getMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
                }
            } catch (NullPointerException ignored) {

            } catch (IllegalArgumentException ignored) {

            }
        }
        mapControlLayout.animate().translationY(0);
        addDsFab.animate().translationY(0);
        mapListFAB.animate().translationY(0);
        diveSpotInfo.hide(diveSpotInfoHeight);
    }

    public void fillDiveSpots() {
        if (diveSpotsClusterManager == null) {
            recyclerView.setVisibility(View.GONE);
            please.setVisibility(View.VISIBLE);
            return;
        }
        ArrayList<DiveSpotShort> visibleSpots = new ArrayList<>();
        visibleSpots = diveSpotsClusterManager.getVisibleMarkersList();
        if (visibleSpots == null || visibleSpots.isEmpty() || visibleSpots.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            please.setVisibility(View.VISIBLE);
            return;
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            please.setVisibility(View.GONE);
        }
        if (diveSpotsListAdapter == null) {
            diveSpotsListAdapter = new DiveSpotsListAdapter(getActivity());
            diveSpotsListAdapter.setData(visibleSpots);
        } else {
            diveSpotsListAdapter.setData(visibleSpots);
        }
        diveSpotsListAdapter.notifyDataSetChanged();

    }

    @Subscribe
    public void onLocationReady(LocationReadyEvent event) {
        Log.i(TAG, "location check: onLocationReady, request codes = " + event.getRequestCodes());
        for (Integer code : event.getRequestCodes()) {
            switch (code) {
                case ActivitiesRequestCodes.REQUEST_CODE_MAP_LIST_FRAGMENT_GO_TO_CURRENT_LOCATION:
                    LatLng myLocation = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(myLocation)
                            .zoom(12)
                            .build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                    goToMyLocation.setVisibility(View.VISIBLE);
                    progressBarMyLocation.setVisibility(View.GONE);
                    if (circle == null) {
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
                        if (diveSpotsClusterManager != null) {
                            diveSpotsClusterManager.setUserCurrentLocationMarker(myLocationMarker);
                        }
                    } else {
                        circle.setCenter(myLocation);
                        myLocationMarker.setPosition(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()));
                        diveSpotsClusterManager.setUserCurrentLocationMarker(myLocationMarker);
                    }
                    break;

                case ActivitiesRequestCodes.REQUEST_CODE_MAP_LIST_FRAGMENT_GET_LOCATION_ON_FRAGMENT_START:
                    Log.i(TAG, "location check: GET_LOCATION_ON_FRAGMENT_START: event.getLocation() = " + event.getLocation() + " diveSpotsClusterManager = " + diveSpotsClusterManager);
                    if (diveSpotsClusterManager == null) {
                        // this means map has not yet been initialized. we need to remember location.
                        userLocationOnFragmentStart = event.getLocation();
                    } else {
                        // this means map has already been initialized.
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
                                new LatLng(event.getLocation().getLatitude() - 1, event.getLocation().getLongitude() - 1),
                                new LatLng(event.getLocation().getLatitude() + 1, event.getLocation().getLongitude() + 1)
                        ), 0), 2000, null);
                    }
                    if (circle == null) {
                        myLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()))
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_me)));
                        CircleOptions circleOptions = new CircleOptions()
                                .center(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()))
                                .radius(200)
                                .strokeColor(android.R.color.transparent)
                                .fillColor(Color.parseColor("#1A0668a1"));
                        circle = mGoogleMap.addCircle(circleOptions);
                        if (diveSpotsClusterManager != null) {
                            diveSpotsClusterManager.setUserCurrentLocationMarker(myLocationMarker);
                        }
                    } else {
                        circle.setCenter(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()));
                        myLocationMarker.setPosition(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()));
                        if (diveSpotsClusterManager != null) {
                            diveSpotsClusterManager.setUserCurrentLocationMarker(myLocationMarker);
                        }
                    }
                    break;
            }
        }
    }

    public FloatingActionButton getAddDsFab() {
        return addDsFab;
    }

    @Subscribe
    public void showMap(CloseListEvent event) {
        mapListFAB.performClick();
    }

    @Override
    public void showDiveSpotInfo(Marker marker, DiveSpotShort diveSpotShort) {
        mapControlLayout.animate().translationY(-diveSpotInfoHeight);
        addDsFab.animate().translationY(-diveSpotInfoHeight);
        mapListFAB.animate().translationY(-diveSpotInfoHeight);
        lastDiveSpotId = diveSpotShort.getId();
        diveSpotInfo.show(diveSpotShort, marker);
    }

    @Override
    public void hideDiveSpotInfo() {
        mapControlLayout.animate().translationY(0);
        addDsFab.animate().translationY(0);
        mapListFAB.animate().translationY(0);
        diveSpotInfo.hide(diveSpotInfoHeight);
    }

    @Override
    public void showMessageToZoom() {
        toast.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMessageToZoom() {
        toast.setVisibility(View.GONE);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void loadDiveSpots(ArrayList<String> sealifes, DiveSpotsRequestMap diveSpotsRequestMap) {
        DDScannerApplication.getInstance().getDdScannerRestClient(getActivity()).getDiveSpotsByArea(sealifes, diveSpotsRequestMap, getDiveSpotsByAreaResultListener);
    }

    @Override
    public void showTutorialForMapListFab() {
        DDScannerApplication.getInstance().getTutorialHelper().showListButtonTutorial(getActivity(), mapListFAB);
    }
}
