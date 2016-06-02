package com.ddscanner.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import com.ddscanner.events.MarkerClickEvent;
import com.ddscanner.events.OnMapClickEvent;
import com.ddscanner.events.OpenAddDsActivityAfterLogin;
import com.ddscanner.services.GPSTracker;
import com.ddscanner.ui.activities.AddDiveSpotActivity;
import com.ddscanner.ui.activities.DiveSpotDetailsActivity;
import com.ddscanner.ui.adapters.ProductListAdapter;
import com.ddscanner.ui.managers.DiveSpotsClusterManager;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
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

    private Map<String, Drawable> infoWindowBackgroundImages = new HashMap<>();

    // List mode member fields
    private RecyclerView rc;
    private RelativeLayout please;
    private ProductListAdapter productListAdapter;
    private Button btnGoToMap;
    private ViewPager viewPager;

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
        infoWindowBackgroundImages.put("wreck", AppCompatDrawableManager.get().getDrawable(getActivity(),
                R.drawable.iw_card_wreck, false));
        infoWindowBackgroundImages.put("cave", AppCompatDrawableManager.get().getDrawable(getActivity(),
                R.drawable.iw_card_cave, false));
        infoWindowBackgroundImages.put("reef", AppCompatDrawableManager.get().getDrawable(getActivity(),
                R.drawable.iw_card_reef, false));
        infoWindowBackgroundImages.put("other", AppCompatDrawableManager.get().getDrawable(getActivity(),
                R.drawable.iw_card_other, false));

        addDsFab.setOnClickListener(this);
        mapListFAB.setOnClickListener(this);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        LogUtils.i(TAG, "mMapView inited");
        mMapView.onCreate(null);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LogUtils.i(TAG, "onMapReady, googleMap = " + googleMap);
                mGoogleMap = googleMap;
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        LogUtils.i(TAG, "onMapLoaded");
                        diveSpotsClusterManager = new DiveSpotsClusterManager(getActivity(), mGoogleMap, toast, progressBar, MapListFragment.this);
                        mGoogleMap.setOnMarkerClickListener(diveSpotsClusterManager);
                        mGoogleMap.setOnCameraChangeListener(diveSpotsClusterManager);
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
                    diveSpotsMapView.setVisibility(View.GONE);
                    diveSpotsListView.setVisibility(View.VISIBLE);
                    mapListFAB.setImageResource(R.drawable.ic_acb_map);
                } else {
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
                goToMyLocation();
                break;
            case R.id.dive_spot_info_layout:
                DiveSpotDetailsActivity.show(getActivity(), String.valueOf(lastDiveSpotId));
                break;
            case  R.id.add_ds_fab:
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
        lastDiveSpotId = event.getDiveSpot().getId();
        rating.removeAllViews();
        for (int k = 0; k < Math.round(event.getDiveSpot().getRating()); k++) {
            ImageView iv = new ImageView(getActivity());
            iv.setImageResource(R.drawable.ic_iw_star_full);
            iv.setPadding(0,0,5,0);
            rating.addView(iv);
        }
        for (int k = 0; k < 5 - Math.round(event.getDiveSpot().getRating()); k++) {
            ImageView iv = new ImageView(getActivity());
            iv.setImageResource(R.drawable.ic_iw_star_empty);
            iv.setPadding(0,0,5,0);
            rating.addView(iv);
        }
    }

    @Subscribe
    public void dismissInfoWindowWhenCameraZoomingOut(CloseInfoWindowEvent event) {
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
        event.getMarker().setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_ds)));
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

    public void goToMyLocation() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            LatLng myLocation = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14.0f));
            if (myLocationMarker == null) {
                // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//                myLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
//                        .position(myLocation)
//                        .anchor(0.5f, 0.5f)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_me)));
                myLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(myLocation)
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.pin_me))));
            } else {
                myLocationMarker.setPosition(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
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
}
