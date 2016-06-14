package com.ddscanner.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.events.CloseInfoWindowEvent;
import com.ddscanner.events.CloseListEvent;
import com.ddscanner.events.InfowWindowOpenedEvent;
import com.ddscanner.events.ListOpenedEvent;
import com.ddscanner.events.LocationReadyEvent;
import com.ddscanner.events.MarkerClickEvent;
import com.ddscanner.events.OnMapClickEvent;
import com.ddscanner.events.OpenAddDsActivityAfterLogin;
import com.ddscanner.ui.activities.AddDiveSpotActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.DiveSpotDetailsActivity;
import com.ddscanner.ui.adapters.ProductListAdapter;
import com.ddscanner.ui.managers.DiveSpotsClusterManager;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
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
import java.util.HashSet;
import java.util.Map;

/**
 * Created by lashket on 20.4.16.
 */
public class MapListFragment extends Fragment implements View.OnClickListener {

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
    private Marker clickedMarker;
    private boolean isMarkerNew;

    public BaseAppCompatActivity baseAppCompatActivity;

    private Map<String, Drawable> infoWindowBackgroundImages = new HashMap<>();

    // List mode member fields
    private RecyclerView rc;
    private RelativeLayout please;
    private ProductListAdapter productListAdapter;
    private Button btnGoToMap;
    private ViewPager viewPager;

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
        findViews();
        setMapView(savedInstanceState);
        baseAppCompatActivity.getLocation(Constants.REQUEST_CODE_MAP_LIST_FRAGMENT_GET_LOCATION_ON_FRAGMENT_START);
        return view;
    }

    private void findViews() {
        diveSpotsMapView = view.findViewById(R.id.map_view);
        diveSpotsListView = view.findViewById(R.id.list_view);
        toast = (RelativeLayout) view.findViewById(R.id.toast);
        progressBar = (ProgressBar) view.findViewById(R.id.request_progress);

        // Map mode
        diveSpotInfo = (RelativeLayout) view.findViewById(R.id.dive_spot_info_layout);
        diveSpotName = (TextView) view.findViewById(R.id.dive_spot_title);
        rating = (LinearLayout) view.findViewById(R.id.rating);
        zoomIn = (ImageView) view.findViewById(R.id.zoom_plus);
        zoomOut = (ImageView) view.findViewById(R.id.zoom_minus);
        object = (TextView) view.findViewById(R.id.divespot_type);
        goToMyLocation = (ImageView) view.findViewById(R.id.go_to_my_location);
        mapListFAB = (FloatingActionButton) view.findViewById(R.id.map_list_fab);
        addDsFab = (FloatingActionButton) view.findViewById(R.id.add_ds_fab);
        mainLayout = (RelativeLayout) view.findViewById(R.id.main_layout);
        diveSpotType = (TextView) view.findViewById(R.id.object);
        progressBarMyLocation = (ProgressView) view.findViewById(R.id.progressBar);

        // List mode
        rc = (RecyclerView) view.findViewById(R.id.cv);
        please = (RelativeLayout) view.findViewById(R.id.please);
        mapListFAB = (FloatingActionButton) view.findViewById(R.id.map_list_fab);
        addDsFab = (FloatingActionButton) view.findViewById(R.id.add_ds_fab);
        addDsFab.setOnClickListener(this);
        mapListFAB.setOnClickListener(this);
        rc.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rc.setLayoutManager(linearLayoutManager);
        rc.setItemAnimator(new DefaultItemAnimator());
    }

    private void setMapView(Bundle savedInstanceState) {
        MapsInitializer.initialize(getActivity());

        infoWindowBackgroundImages.put(Constants.OBJECT_TYPE_WRECK, AppCompatDrawableManager.get().getDrawable(getActivity(),
                R.drawable.iw_card_wreck, false));
        infoWindowBackgroundImages.put(Constants.OBJECT_TYPE_CAVE, AppCompatDrawableManager.get().getDrawable(getActivity(),
                R.drawable.iw_card_cave, false));
        infoWindowBackgroundImages.put(Constants.OBJECT_TYPE_REEF, AppCompatDrawableManager.get().getDrawable(getActivity(),
                R.drawable.iw_card_reef, false));
        infoWindowBackgroundImages.put(Constants.OBJECT_TYPE_OTHER, AppCompatDrawableManager.get().getDrawable(getActivity(),
                R.drawable.iw_card_other, false));

        addDsFab.setOnClickListener(this);
        mapListFAB.setOnClickListener(this);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        LogUtils.i(TAG, "mMapView inited");
        mMapView.onCreate(new Bundle());
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LogUtils.i(TAG, "onMapReady, googleMap = " + googleMap);
                mGoogleMap = googleMap;
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        LogUtils.i(TAG, "location check: onMapLoaded, userLocationOnFragmentStart = " + userLocationOnFragmentStart);
                        diveSpotsClusterManager = new DiveSpotsClusterManager(getActivity(), mGoogleMap, toast, progressBar, MapListFragment.this);
                        mGoogleMap.setOnMarkerClickListener(diveSpotsClusterManager);
                        mGoogleMap.setOnCameraChangeListener(diveSpotsClusterManager);
                        if (userLocationOnFragmentStart == null) {
                            // this means location has not yet been received. do nothing
                        } else {
                            // this means location has already been received
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
                                    new LatLng(userLocationOnFragmentStart.getLatitude() - 1, userLocationOnFragmentStart.getLongitude() - 1),
                                    new LatLng(userLocationOnFragmentStart.getLatitude() + 1, userLocationOnFragmentStart.getLongitude() + 1)
                            ), 0));
                        }
                    }
                });

            }
        });
        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);
        goToMyLocation.setOnClickListener(this);
        diveSpotInfo.setOnClickListener(this);
        mapControlLayout = (RelativeLayout) view.findViewById(R.id.map_control_layout);
    }

    private void setListView() {

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
                    if (diveSpotInfo.getVisibility() == View.VISIBLE) {
                        mapListFAB.setY(mapListFAB.getY() + diveSpotInfo.getHeight());
                        addDsFab.setY(addDsFab.getY() + diveSpotInfo.getHeight());
                    }
                    diveSpotsMapView.setVisibility(View.GONE);
                    diveSpotsListView.setVisibility(View.VISIBLE);
                    DDScannerApplication.bus.post(new ListOpenedEvent());
                    mapListFAB.setImageResource(R.drawable.ic_acb_map);
                } else {
                    if (diveSpotInfo.getVisibility() == View.VISIBLE) {
                        mapListFAB.setY(mapListFAB.getY() - diveSpotInfo.getHeight());
                        addDsFab.setY(addDsFab.getY() - diveSpotInfo.getHeight());
                    }
                    diveSpotsMapView.setVisibility(View.VISIBLE);
                    diveSpotsListView.setVisibility(View.GONE);
                    mapListFAB.setImageResource(R.drawable.ic_acb_list);
                }
                isMapShown = !isMapShown;
                break;
            case R.id.zoom_plus:
                diveSpotsClusterManager.mapZoomPlus();
                break;
            case R.id.zoom_minus:
                diveSpotsClusterManager.mapZoomMinus();
                break;
            case R.id.go_to_my_location:
                goToMyLocation.setVisibility(View.GONE);
                progressBarMyLocation.setVisibility(View.VISIBLE);
                baseAppCompatActivity.getLocation(Constants.REQUEST_CODE_MAP_LIST_FRAGMENT_GO_TO_CURRENT_LOCATION);
                break;
            case R.id.dive_spot_info_layout:
                DiveSpotDetailsActivity.show(getActivity(), String.valueOf(lastDiveSpotId));
                break;
            case R.id.add_ds_fab:
                if (SharedPreferenceHelper.getIsUserLogined()) {
                    AddDiveSpotActivity.show(getActivity());
                } else {
                    DDScannerApplication.bus.post(new OpenAddDsActivityAfterLogin());
                }
                break;
        }
    }

    @UiThread
    @Subscribe
    public void getDiveSpotInfow(MarkerClickEvent event) {
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
        diveSpotName.setText(event.getDiveSpot().getName());
        diveSpotType.setText(event.getDiveSpot().getObject());
        diveSpotInfo.setBackground(infoWindowBackgroundImages.get(event.getDiveSpot().getObject()));
        lastDiveSpotId = event.getDiveSpot().getId();
        rating.removeAllViews();
        for (int k = 0; k < Math.round(event.getDiveSpot().getRating()); k++) {
            ImageView iv = new ImageView(getActivity());
            iv.setImageResource(R.drawable.ic_iw_star_full);
            iv.setPadding(0, 0, 5, 0);
            rating.addView(iv);
        }
        for (int k = 0; k < 5 - Math.round(event.getDiveSpot().getRating()); k++) {
            ImageView iv = new ImageView(getActivity());
            iv.setImageResource(R.drawable.ic_iw_star_empty);
            iv.setPadding(0, 0, 5, 0);
            rating.addView(iv);
        }
    }

    @Subscribe
    public void dismissInfoWindowWhenCameraZoomingOut(CloseInfoWindowEvent event) {
        if (clickedMarker != null) {
            try {
                if (isMarkerNew) {
                    clickedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_ds_new)));
                } else {
                    clickedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_ds)));
                }
            } catch (NullPointerException e) {

            } catch (IllegalArgumentException e) {

            }
        }
        clickedMarker = null;
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
    }

    @Subscribe
    public void hideDiveSpotinfo(OnMapClickEvent event) {
        // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//                event.getMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
        if (event.getMarker() != null) {
            try {

                if (event.getIsNew()) {
                    event.getMarker().setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_ds_new)));
                } else {
                    event.getMarker().setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_ds)));
                }
            } catch (NullPointerException e) {

            } catch (IllegalArgumentException e) {

            }
        }
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
    }

    public void fillDiveSpots(ArrayList<DiveSpot> diveSpots) {
        if (productListAdapter == null) {
            productListAdapter = new ProductListAdapter(diveSpots, getActivity());
            rc.setAdapter(productListAdapter);
        } else {
            productListAdapter.setDiveSpots(diveSpots);
        }

        if (!diveSpots.isEmpty()) {
            rc.setVisibility(View.VISIBLE);
            please.setVisibility(View.GONE);
        } else {
            rc.setVisibility(View.GONE);
            please.setVisibility(View.VISIBLE);
        }

    }

    @Subscribe
    public void onLocationReady(LocationReadyEvent event) {
        LogUtils.i(TAG, "location check: onLocationReady, request codes = " + event.getRequestCodes());
        for (Integer code : event.getRequestCodes()) {
            switch (code) {
                case Constants.REQUEST_CODE_MAP_LIST_FRAGMENT_GO_TO_CURRENT_LOCATION:
                    LatLng myLocation = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(myLocation)
                            .zoom(12)
                            .build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                    goToMyLocation.setVisibility(View.VISIBLE);
                    progressBarMyLocation.setVisibility(View.GONE);
                    if (circle == null) {
                        // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//                myLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
//                        .position(myLocation)
//                        .anchor(0.5f, 0.5f)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_me)));
                        myLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
                                .position(myLocation)
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_pin_me))));
                        CircleOptions circleOptions = new CircleOptions()
                                .center(myLocation)
                                .radius(200)
                                .strokeColor(android.R.color.transparent)
                                .fillColor(Color.parseColor("#1A0668a1"));
                        circle = mGoogleMap.addCircle(circleOptions);
                        diveSpotsClusterManager.setUserCurrentLocationMarker(myLocationMarker);
                    } else {
                        circle.setCenter(myLocation);
                        myLocationMarker.setPosition(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()));
                        diveSpotsClusterManager.setUserCurrentLocationMarker(myLocationMarker);
                    }
                    break;

                case Constants.REQUEST_CODE_MAP_LIST_FRAGMENT_GET_LOCATION_ON_FRAGMENT_START:
                    LogUtils.i(TAG, "location check: GET_LOCATION_ON_FRAGMENT_START: event.getLocation() = " + event.getLocation() + " diveSpotsClusterManager = " + diveSpotsClusterManager);
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
                    break;
            }
        }
    }

    @Subscribe
    public void showMap(CloseListEvent event) {
        mapListFAB.performClick();
    }
}
