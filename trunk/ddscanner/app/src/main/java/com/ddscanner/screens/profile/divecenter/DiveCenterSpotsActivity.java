package com.ddscanner.screens.profile.divecenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.events.MarkerClickedEvent;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.events.InfowWindowOpenedEvent;
import com.ddscanner.events.ListOpenedEvent;
import com.ddscanner.events.LocationReadyEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.adapters.DiveSpotsListAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.ui.managers.DiveCenterSpotsClusterManager;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
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
import java.util.HashMap;
import java.util.Map;

public class DiveCenterSpotsActivity extends BaseAppCompatActivity implements View.OnClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, DialogClosedListener {

    private MaterialDialog materialDialog;
    private RelativeLayout toast;
    private ProgressBar progressBar;
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
    private RelativeLayout mainLayout;
    private TextView object;
    private RelativeLayout mapControlLayout;
    private Marker myLocationMarker;
    private Circle circle;
    private ProgressView progressBarMyLocation;
    private android.widget.Button continueShowMap;
    private boolean isToastMessageVisible;
    private MapView mapView;
    private View diveSpotsMapView;
    private View diveSpotsListView;
    private RecyclerView rc;
    private Map<String, Drawable> infoWindowBackgroundImages = new HashMap<>();
    private GoogleMap googleMap;
    private DiveCenterSpotsClusterManager diveCenterSpotsClusterManager;
    private Map<LatLng, DiveSpotShort> diveSpotsMap = new HashMap<>();
    private ArrayList<DiveSpotShort> diveSpots;
    private LatLng diveCenterLatLng;
    private DiveSpotsListAdapter diveSpotsListAdapter;
    private LatLngBounds mapLatLngBound;
    private double northeastLng;
    private double northeastLat;
    private double southwestLat;
    private double southWestLng;
    private Marker diveCenterMarker;
    private boolean isMapShown = true;
    private RelativeLayout please;
    private Marker lastClickeMarker;
    private String id;
    
    private DDScannerRestClient.ResultListener<ArrayList<DiveSpotShort>> diveSpotsResultListener = new DDScannerRestClient.ResultListener<ArrayList<DiveSpotShort>>() {
        @Override
        public void onSuccess(ArrayList<DiveSpotShort> result) {
            diveSpots = result;
            drawMarkers();
            materialDialog.dismiss();
            rc.setAdapter(new DiveSpotsListAdapter(result, DiveCenterSpotsActivity.this, EventsTracker.SpotViewSource.FROM_PROFILE_CREATED));
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
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_DIVE_CENTER_SPOTS_ACTIVITY_HIDE, false);
        }

    };

    public static void show(Context context, String id, LatLng latLng) {
        Intent intent = new Intent(context, DiveCenterSpotsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("place", latLng);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_center_dive_spots);
        setupToolbar(R.string.diespors, R.id.toolbar);
        if (getIntent().getParcelableExtra("place") != null) {
            diveCenterLatLng = getIntent().getParcelableExtra("place");
        }
        materialDialog = Helpers.getMaterialDialog(this);
        materialDialog.show();
        id = getIntent().getStringExtra("id");
        findViews();
        setMapView(savedInstanceState);
    }

    private void findViews() {
        diveSpotsMapView = findViewById(R.id.map_view);
        diveSpotsListView = findViewById(R.id.list_view);
        toast = (RelativeLayout) findViewById(R.id.toast);
        progressBar = (ProgressBar) findViewById(R.id.request_progress);

        // Map mode
        diveSpotInfo = (RelativeLayout) findViewById(R.id.dive_spot_info_layout);
        diveSpotName = (TextView) findViewById(R.id.dive_spot_title);
        rating = (LinearLayout) findViewById(R.id.rating);
        zoomIn = (ImageView) findViewById(R.id.zoom_plus);
        zoomOut = (ImageView) findViewById(R.id.zoom_minus);
        object = (TextView) findViewById(R.id.divespot_type);
        goToMyLocation = (ImageView) findViewById(R.id.go_to_my_location);
        mapListFAB = (FloatingActionButton) findViewById(R.id.map_list_fab);
        addDsFab = (FloatingActionButton) findViewById(R.id.add_ds_fab);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        diveSpotType = (TextView) findViewById(R.id.object);
        progressBarMyLocation = (ProgressView) findViewById(R.id.progressBar);
        addDsFab.setVisibility(View.GONE);
        // List mode
        please = (RelativeLayout) findViewById(R.id.please);
        please.setVisibility(View.GONE);
        rc = (RecyclerView) findViewById(R.id.cv);
        rc.setVisibility(View.VISIBLE);
        mapListFAB = (FloatingActionButton) findViewById(R.id.map_list_fab);
        addDsFab = (FloatingActionButton) findViewById(R.id.add_ds_fab);
        addDsFab.setOnClickListener(this);
        mapListFAB.setOnClickListener(this);
        rc.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DDScannerApplication.bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
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
        goToMyLocation.setVisibility(View.VISIBLE);
        progressBarMyLocation.setVisibility(View.GONE);
        mapView.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_list_fab:
                if (isMapShown) {
                    if (diveSpotInfo.getVisibility() == View.VISIBLE) {
                        mapListFAB.setY(mapListFAB.getY() + diveSpotInfo.getHeight());
                    }
                    toast.setVisibility(View.GONE);
                    diveSpotsMapView.setVisibility(View.GONE);
                    EventsTracker.trackDiveSpotListView();
                    diveSpotsListView.setVisibility(View.VISIBLE);
                    DDScannerApplication.bus.post(new ListOpenedEvent());
                    mapListFAB.setImageResource(R.drawable.ic_acb_map);
                } else {
                    mapListFAB.setVisibility(View.VISIBLE);
                    if (diveSpotInfo.getVisibility() == View.VISIBLE) {
                        mapListFAB.setY(mapListFAB.getY() - diveSpotInfo.getHeight());
                    }
                    diveSpotsMapView.setVisibility(View.VISIBLE);
                    diveSpotsListView.setVisibility(View.GONE);
                    mapListFAB.setImageResource(R.drawable.ic_acb_list);
                }
                isMapShown = !isMapShown;
                break;
            case R.id.zoom_minus:
                diveCenterSpotsClusterManager.mapZoomMinus();
                break;
            case R.id.zoom_plus:
                diveCenterSpotsClusterManager.mapZoomPlus();
                break;
            case R.id.go_to_my_location:
                getLocation(ActivitiesRequestCodes.REQUEST_CODE_DIVE_CENTER_SPOTS_ACTIVITY_GO_TO_MY_LOCATION);
                break;
            case R.id.dive_spot_info_layout:
                DiveSpotDetailsActivity.show(this, String.valueOf(lastDiveSpotId), EventsTracker.SpotViewSource.UNKNOWN);
                break;
        }
    }

    @Subscribe
    public void onLocationReady(LocationReadyEvent event) {
        for (Integer code : event.getRequestCodes()) {
            switch (code) {
                case ActivitiesRequestCodes.REQUEST_CODE_DIVE_CENTER_SPOTS_ACTIVITY_GO_TO_MY_LOCATION:
                    LatLng myLocation = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(myLocation)
                            .zoom(12)
                            .build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                    goToMyLocation.setVisibility(View.VISIBLE);
                    progressBarMyLocation.setVisibility(View.GONE);
                    if (circle == null) {
                        myLocationMarker = googleMap.addMarker(new MarkerOptions()
                                .position(myLocation)
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_me)));
                        CircleOptions circleOptions = new CircleOptions()
                                .center(myLocation)
                                .radius(200)
                                .strokeColor(android.R.color.transparent)
                                .fillColor(Color.parseColor("#1A0668a1"));
                        circle = googleMap.addCircle(circleOptions);
                      //  diveSpotsClusterManager.setUserCurrentLocationMarker(myLocationMarker);
                    }
                    break;

                case ActivitiesRequestCodes.REQUEST_CODE_MAP_LIST_FRAGMENT_GET_LOCATION_ON_FRAGMENT_START:
                    if (diveCenterSpotsClusterManager != null) {
                        // this means map has already been initialized.
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
                                new LatLng(event.getLocation().getLatitude() - 1, event.getLocation().getLongitude() - 1),
                                new LatLng(event.getLocation().getLatitude() + 1, event.getLocation().getLongitude() + 1)
                        ), 0), 2000, null);
                    }
                    break;
            }
        }
    }


    private void setMapView(Bundle savedInstanceState) {
        MapsInitializer.initialize(this);

        infoWindowBackgroundImages.put(Constants.OBJECT_TYPE_WRECK, AppCompatDrawableManager.get().getDrawable(this,
                R.drawable.iw_card_wreck));
        infoWindowBackgroundImages.put(Constants.OBJECT_TYPE_CAVE, AppCompatDrawableManager.get().getDrawable(this,
                R.drawable.iw_card_cave));
        infoWindowBackgroundImages.put(Constants.OBJECT_TYPE_REEF, AppCompatDrawableManager.get().getDrawable(this,
                R.drawable.iw_card_reef));
        infoWindowBackgroundImages.put(Constants.OBJECT_TYPE_OTHER, AppCompatDrawableManager.get().getDrawable(this,
                R.drawable.iw_card_other));

        addDsFab.setOnClickListener(this);
        mapListFAB.setOnClickListener(this);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(new Bundle());
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                DiveCenterSpotsActivity.this.googleMap = googleMap;
                googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
//                        Log.i(TAG, "onMapLoaded");
//                        CameraPosition cameraPosition = new CameraPosition.Builder()
//                                .target(diveSpotLatLng)
//                                .zoom(8)
//                                .build();
                     //   googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                     //   googleMap.moveCamera(CameraUpdateFactory.newLatLng(diveCenterLatLng));
                        diveCenterSpotsClusterManager = new DiveCenterSpotsClusterManager(DiveCenterSpotsActivity.this, googleMap);
                        googleMap.setOnMarkerClickListener(diveCenterSpotsClusterManager);
                        googleMap.setOnCameraChangeListener(diveCenterSpotsClusterManager);
                        googleMap.setOnMapClickListener(DiveCenterSpotsActivity.this);
                        googleMap.getUiSettings().setRotateGesturesEnabled(false);
                        googleMap.getUiSettings().setTiltGesturesEnabled(false);
                        if (diveCenterLatLng != null) {
                            diveCenterMarker = googleMap.addMarker(new MarkerOptions().position(diveCenterLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dc)));
                        }
                        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveCenterDiveSpotsList(diveSpotsResultListener, id);
                        //     diveSpotMarker = googleMap.addMarker(new MarkerOptions().position(diveSpotLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds)).title(diveSpotName));
                    }
                });
            }
        });
        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);
        goToMyLocation.setOnClickListener(this);
        diveSpotInfo.setOnClickListener(this);
        mapControlLayout = (RelativeLayout) findViewById(R.id.map_control_layout);
    }


    private void drawMarkers() {
        if (diveCenterSpotsClusterManager != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            if (diveCenterLatLng != null) {
                builder.include(diveCenterLatLng);
            }
            for (DiveSpotShort diveSpotShort : diveSpots) {
                diveCenterSpotsClusterManager.addItem(diveSpotShort);
                builder.include(diveSpotShort.getPosition());
                diveSpotsMap.put(diveSpotShort.getPosition(), diveSpotShort);
            }
            LatLngBounds bounds = builder.build();
            int padding = 100; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.moveCamera(cu);
            diveCenterSpotsClusterManager.cluster();
            diveCenterSpotsClusterManager.setDiveSpotsMap(diveSpotsMap);
        }

    }

    public void showDiveSpotInfo(DiveSpotShort diveSpotShort) {
        DDScannerApplication.bus.post(new InfowWindowOpenedEvent(null));
        mapControlLayout.animate().translationY(-diveSpotInfo.getHeight());
        addDsFab.animate().translationY(-diveSpotInfo.getHeight());
        mapListFAB.animate().translationY(-diveSpotInfo.getHeight());
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
        diveSpotName.setText(diveSpotShort.getName());
        diveSpotType.setText(diveSpotShort.getObject());
        diveSpotInfo.setBackground(infoWindowBackgroundImages.get(diveSpotShort.getObject()));
        lastDiveSpotId = diveSpotShort.getId();
        rating.removeAllViews();
        for (int k = 0; k < Math.round(diveSpotShort.getRating()); k++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_iw_star_full);
            iv.setPadding(0, 0, 5, 0);
            rating.addView(iv);
        }
        for (int k = 0; k < 5 - Math.round(diveSpotShort.getRating()); k++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_iw_star_empty);
            iv.setPadding(0, 0, 5, 0);
            rating.addView(iv);
        }
    }

    private void hideDiveSpotInfo() {
        if (diveSpotInfo.getVisibility() == View.VISIBLE) {
            mapControlLayout.animate().translationY(0);
            addDsFab.animate().translationY(0);
            mapListFAB.animate().translationY(0);
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
            lastClickeMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
            lastClickeMarker = null;
        }
    }


    @Override
    public void onMapClick(LatLng latLng) {
        hideDiveSpotInfo();
    }

    @Subscribe
    public void markerClicked(MarkerClickedEvent event) {
        if (diveSpotsMap.get(event.getMarker().getPosition()) != null && diveSpotsMap.get(event.getMarker().getPosition()).getName() != null) {
            onMarkerClick(event.getMarker());
        }
    }

    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(diveCenterMarker) || marker.equals(myLocationMarker)) {
            return false;
        }
        if (lastClickeMarker == null) {
            lastClickeMarker = marker;
        } else {
            lastClickeMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
        }
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds_selected));
        showDiveSpotInfo(diveSpotsMap.get(marker.getPosition()));
        return false;
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }
}
