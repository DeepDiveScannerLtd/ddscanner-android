package com.ddscanner.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatDrawableManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.CloseInfoWindowEvent;
import com.ddscanner.events.MarkerClickEvent;
import com.ddscanner.events.OnMapClickEvent;
import com.ddscanner.events.OpenAddDsActivityAfterLogin;
import com.ddscanner.services.GPSTracker;
import com.ddscanner.ui.activities.AddDiveSpotActivity;
import com.ddscanner.ui.activities.DiveSpotDetailsActivity;
import com.ddscanner.ui.adapters.MapListPagerAdapter;
import com.ddscanner.ui.managers.DiveSpotsClusterManager;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lashket on 19.4.16.
 */
public class DiveSpotsMapFragment extends Fragment implements View.OnClickListener {

    private GoogleMap mGoogleMap;
    private DiveSpotsClusterManager diveSpotsClusterManager;
    MapView mMapView;

    private RelativeLayout toast;
    private ProgressBar progressBar;
    private ViewPager mapListViewPager;
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

    private Map<String, Drawable> map = new HashMap<>();

    private MapListPagerAdapter mapListPagerAdapter;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // super.onCreateView(inflater, container, savedInstanceState);
        map.put("wreck", AppCompatDrawableManager.get().getDrawable(getActivity(),
                R.drawable.iw_card_wreck, false));
        map.put("cave", AppCompatDrawableManager.get().getDrawable(getActivity(),
                R.drawable.iw_card_cave, false));
        map.put("reef", AppCompatDrawableManager.get().getDrawable(getActivity(),
                R.drawable.iw_card_reef, false));
        map.put("other", AppCompatDrawableManager.get().getDrawable(getActivity(),
                R.drawable.iw_card_other, false));
        v = inflater.inflate(R.layout.dive_sites_map_fragment, container, false);
        diveSpotInfo = (RelativeLayout) v.findViewById(R.id.dive_spot_info_layout);
        diveSpotName = (TextView) v.findViewById(R.id.dive_spot_title);
        rating = (LinearLayout) v.findViewById(R.id.rating);
        zoomIn = (ImageView) v.findViewById(R.id.zoom_plus);
        zoomOut = (ImageView) v.findViewById(R.id.zoom_minus);
        object = (TextView) v.findViewById(R.id.divespot_type);
        goToMyLocation = (ImageView) v.findViewById(R.id.go_to_my_location);
        mapListFAB = (FloatingActionButton) v.findViewById(R.id.map_list_fab);
        addDsFab = (FloatingActionButton) v.findViewById(R.id.add_ds_fab);
        mainLayout = (RelativeLayout) v.findViewById(R.id.main_layout);
        addDsFab.setOnClickListener(this);
        mapListFAB.setOnClickListener(this);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                diveSpotsClusterManager = new DiveSpotsClusterManager(getActivity(), mGoogleMap, mapListPagerAdapter ,toast, progressBar);
                mGoogleMap.setOnMarkerClickListener(diveSpotsClusterManager);
                mGoogleMap.setOnCameraChangeListener(diveSpotsClusterManager);

            }
        });
        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);
        goToMyLocation.setOnClickListener(this);
        diveSpotInfo.setOnClickListener(this);
        mapControlLayout = (RelativeLayout) v.findViewById(R.id.map_control_layout);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

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
                mapListViewPager.setCurrentItem(1, false);
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
        event.getMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
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

    public void setUI(RelativeLayout toast, ProgressBar progressBar, MapListPagerAdapter mapListPagerAdapter, ViewPager mapListViewPager) {
        this.mapListPagerAdapter = mapListPagerAdapter;
        this.toast = toast;
        this.progressBar = progressBar;
        this.mapListViewPager = mapListViewPager;
    }

    public void goToMyLocation() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            LatLng myLocation = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14.0f));
            if (myLocationMarker == null) {
                myLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(myLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_me)));
            } else {
                myLocationMarker.setPosition(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

}
