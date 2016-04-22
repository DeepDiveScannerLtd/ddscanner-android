package com.ddscanner.ui.managers;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.entities.DivespotsWrapper;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.events.MarkerClickEvent;
import com.ddscanner.events.OnMapClickEvent;
import com.ddscanner.events.PlaceChoosedEvent;
import com.ddscanner.rest.RestClient;
import com.ddscanner.services.GPSTracker;
import com.ddscanner.ui.adapters.MapListPagerAdapter;
import com.ddscanner.ui.fragments.DiveSpotsMapFragment;
import com.ddscanner.utils.EventTrackerHelper;
import com.ddscanner.utils.LogUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class DiveSpotsClusterManager extends ClusterManager<DiveSpot> implements ClusterManager.OnClusterClickListener<DiveSpot>, GoogleMap.OnMapClickListener {

    private static final String TAG = DiveSpotsClusterManager.class.getName();
    private static final int CAMERA_ANIMATION_DURATION = 300;
    private final IconGenerator clusterIconGenerator;
    private Context context;
    private GoogleMap googleMap;
    private DiveSpotsRequestMap diveSpotsRequestMap = new DiveSpotsRequestMap();
    private DivespotsWrapper divespotsWrapper;
    private ArrayList<DiveSpot> diveSpots = new ArrayList<>();
    private HashMap<LatLng, DiveSpot> diveSpotsMap = new HashMap<>();
    private Drawable clusterBackgroundDrawable;

    private String currents;
    private String level;
    private String object;
    private int rating = -1;
    private String visibility;

    private MapListPagerAdapter mapListPagerAdapter;

    private boolean isCanMakeRequest = false;

    private ProgressBar progressBar;
    private RelativeLayout toast;

    private Marker lastClickedMarker;

    public DiveSpotsClusterManager(Context context, GoogleMap googleMap, MapListPagerAdapter mapListPagerAdapter, RelativeLayout toast, ProgressBar progressBar) {
        super(context, googleMap);
        this.context = context;
        this.googleMap = googleMap;
        this.clusterBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.ic_number);
        this.clusterIconGenerator = new IconGenerator(context);
        this.clusterIconGenerator.setContentView(this.makeSquareTextView(context));
        this.clusterIconGenerator.setTextAppearance(com.google.maps.android.R.style.ClusterIcon_TextAppearance);
        this.clusterIconGenerator.setBackground(clusterBackgroundDrawable);
        this.toast = toast;
        this.progressBar = progressBar;
        this.mapListPagerAdapter = mapListPagerAdapter;
        googleMap.setOnMapClickListener(this);
        setAlgorithm(new GridBasedAlgorithm<DiveSpot>());
        setRenderer(new IconRenderer(context, googleMap, this));
        setOnClusterClickListener(this);
        if (checkArea(googleMap.getProjection().getVisibleRegion().latLngBounds.southwest, googleMap.getProjection().getVisibleRegion().latLngBounds.northeast)) {
            requestCityProducts();
        } else {
            showToast();
        }
        DDScannerApplication.bus.register(this);
    }

    private void showToast() {
        toast.setVisibility(View.VISIBLE);
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideToast();
            }
        }, 1700);
    }

    private void showPb() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hidePb() {
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, 1200);
    }

    private void hideToast() {
        toast.setVisibility(View.GONE);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (lastClickedMarker != null) {
           // lastClickedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
            DDScannerApplication.bus.post(new OnMapClickEvent(lastClickedMarker));
            lastClickedMarker = null;
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        super.onCameraChange(cameraPosition);

        LatLng southwest = googleMap.getProjection().getVisibleRegion().latLngBounds.southwest;
        LatLng northeast = googleMap.getProjection().getVisibleRegion().latLngBounds.northeast;
        mapListPagerAdapter.populateDiveSpotsList(changeListToListFragment(diveSpots));
        if (checkArea(southwest, northeast)) {
            if (isCanMakeRequest) {
                if (southwest.latitude <= diveSpotsRequestMap.getSouthWestLat() ||
                        southwest.longitude <= diveSpotsRequestMap.getSouthWestLng() ||
                        northeast.latitude >= diveSpotsRequestMap.getNorthEastLat() ||
                        northeast.longitude >= diveSpotsRequestMap.getNorthEastLng()) {
                    requestCityProducts();
                }
            } else {
                requestCityProducts();
            }
        } else {
            showToast();
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (super.onMarkerClick(marker)) {
            return true;
        }
        if (lastClickedMarker != null) {
            lastClickedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
        }
        lastClickedMarker = marker;
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds_selected));
        DDScannerApplication.bus.post(new MarkerClickEvent(diveSpotsMap.get(marker.getPosition())));
        AppsFlyerLib.getInstance().trackEvent(context, EventTrackerHelper
                .EVENT_MARKER_CLICK, new HashMap<String, Object>() {{
            put(EventTrackerHelper.PARAM_MARKER_CLICK_TYPE, "dive_site");
            put(EventTrackerHelper.PARAM_MARKER_CLICK_PLACE_ID, String.valueOf(diveSpotsMap.get(marker.getPosition()).getId()));
        }});
        return true;
    }

    @Override
    public boolean onClusterClick(Cluster<DiveSpot> cluster) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (DiveSpot diveSpot : cluster.getItems()) {
            builder.include(diveSpot.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
        googleMap.animateCamera(cu, CAMERA_ANIMATION_DURATION, null);
        return true;
    }

    private SquareTextView makeSquareTextView(Context context) {
        SquareTextView squareTextView = new SquareTextView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        squareTextView.setLayoutParams(layoutParams);
        squareTextView.setId(com.google.maps.android.R.id.text);
        return squareTextView;
    }

    public void updateDiveSpots(ArrayList<DiveSpot> diveSpots) {
        LogUtils.i(TAG, "incoming dive spots size = " + diveSpots.size());
        final ArrayList<DiveSpot> newDiveSpots = new ArrayList<>();
        newDiveSpots.addAll(diveSpots);
        newDiveSpots.removeAll(this.diveSpots);
        final ArrayList<DiveSpot> deletedDiveSpots = new ArrayList<>();
        deletedDiveSpots.addAll(this.diveSpots);
        deletedDiveSpots.removeAll(diveSpots);
        LogUtils.i(TAG, "removing " + deletedDiveSpots.size() + " dive spots");
        for (DiveSpot diveSpot : deletedDiveSpots) {
            removeDiveSpot(diveSpot);
        }
        LogUtils.i(TAG, "adding " + newDiveSpots.size() + " dive spots");
        for (DiveSpot diveSpot : newDiveSpots) {
            addNewDiveSpot(diveSpot);
        }
        cluster();
    }

    private void addNewDiveSpot(DiveSpot diveSpot) {
        if (diveSpot.getPosition() == null) {
            LogUtils.i(TAG, "addNewDiveSpot diveSpot.getPosition() == null");
        } else {
            addItem(diveSpot);
            diveSpotsMap.put(diveSpot.getPosition(), diveSpot);
            diveSpots.add(diveSpot);
        }
    }

    private void removeDiveSpot(DiveSpot diveSpot) {
        removeItem(diveSpot);
        diveSpotsMap.remove(new LatLng(Double.valueOf(diveSpot.getLat()), Double.valueOf(diveSpot.getLng())));
        diveSpots.remove(diveSpot);
    }

    private boolean checkArea(LatLng southWest, LatLng northEast) {
        if (Math.abs(northEast.longitude - southWest.longitude) > 8 || Math.abs(northEast.latitude - southWest.latitude) > 8) {
            return false;
        }
        return true;
    }

    public void updateFilter(String currents, String level, String object, int rating, String visibility) {
        this.currents = currents;
        this.level = level;
        this.object = object;
        this.rating = rating;
        this.visibility = visibility;
    }


    public void requestCityProducts() {
        diveSpotsRequestMap.clear();
        LatLng southwest = googleMap.getProjection().getVisibleRegion().latLngBounds.southwest;
        LatLng northeast = googleMap.getProjection().getVisibleRegion().latLngBounds.northeast;
        if (checkArea(southwest, northeast)) {
            showPb();
            isCanMakeRequest = true;
            diveSpotsRequestMap.putSouthWestLat(southwest.latitude - Math.abs(northeast.latitude - southwest.latitude));
            diveSpotsRequestMap.putSouthWestLng(southwest.longitude - Math.abs(northeast.longitude - southwest.longitude));
            diveSpotsRequestMap.putNorthEastLat(northeast.latitude + Math.abs(northeast.latitude - southwest.latitude));
            diveSpotsRequestMap.putNorthEastLng(northeast.longitude + Math.abs(northeast.longitude - southwest.longitude));
            if (!TextUtils.isEmpty(currents)) {
                diveSpotsRequestMap.putCurrents(currents);
            }
            if (!TextUtils.isEmpty(level)) {
                diveSpotsRequestMap.putLevel(level);
            }
            if (!TextUtils.isEmpty(object)) {
                diveSpotsRequestMap.putObject(object);
            }
            if (rating != -1) {
                diveSpotsRequestMap.putRating(rating);
            }
            if (!TextUtils.isEmpty(visibility)) {
                diveSpotsRequestMap.putVisibility(visibility);
            }
            for (Map.Entry<String, Object> entry : diveSpotsRequestMap.entrySet()) {
                LogUtils.i(TAG, "get dive spots request parameter: " + entry.getKey() + " " + entry.getValue());
            }
            Call<ResponseBody> call = RestClient.getServiceInstance().getDivespots(diveSpotsRequestMap);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        String responseString = "";
                        try {
                            responseString = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        LogUtils.i(TAG, "response body is " + responseString);
                        divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                        updateDiveSpots((ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots());
                        mapListPagerAdapter.populateDiveSpotsList(changeListToListFragment((ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots()));
                        hidePb();
                    } else {
                        hidePb();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // TODO Handle errors
                }
            });
        }
    }

    private class IconRenderer extends DefaultClusterRenderer<DiveSpot> {

        public IconRenderer(Context context, GoogleMap map, ClusterManager<DiveSpot> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<DiveSpot> cluster, MarkerOptions markerOptions) {
            int bucket = this.getBucket(cluster);
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(clusterIconGenerator.makeIcon(this.getClusterText(bucket)));

            markerOptions.icon(descriptor);
        }

        @Override
        protected void onClusterItemRendered(DiveSpot diveSpot, final Marker marker) {
            super.onClusterItemRendered(diveSpot, marker);
            try {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
                if (lastClickedMarker != null && lastClickedMarker.getPosition().equals(marker.getPosition()) && lastClickedMarker.isInfoWindowShown()) {
                    //      marker.showInfoWindow();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<DiveSpot> changeListToListFragment(ArrayList<DiveSpot> oldList) {
        ArrayList<DiveSpot> newList = new ArrayList<>();
        for (DiveSpot diveSpot : oldList) {
            if (isSpotVisibleOnScreen(Float.valueOf(diveSpot.getLat()), Float.valueOf(diveSpot.getLng()))) {
                Log.i(TAG, "DiveSpotInVisibleRegion");
                newList.add(diveSpot);
            }
        }
        return newList;
    }

    private boolean isSpotVisibleOnScreen(float lat, float lng) {
        LatLng southwest = googleMap.getProjection().getVisibleRegion().latLngBounds.southwest;
        LatLng northeast = googleMap.getProjection().getVisibleRegion().latLngBounds.northeast;
        if (lat < northeast.latitude && lat > southwest.latitude && lng < northeast.longitude && lng > southwest.longitude) {
            Log.i(TAG, "Coordinates in visible region");
            return true;
        }
        return false;
    }

    public void mapZoomPlus() {
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    public void mapZoomMinus() {
        googleMap.animateCamera(CameraUpdateFactory.zoomOut());
    }

    public void goToMyLocation() {
        GPSTracker gpsTracker = new GPSTracker(context);
        if (gpsTracker.canGetLocation()) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 14.0f));
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    @Subscribe
    public void moveCameraByChosedLocation(PlaceChoosedEvent event) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(event.getLatLngBounds(),0));
    }

}
