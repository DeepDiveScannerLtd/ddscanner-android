package com.ddscanner.ui.managers;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.entities.DivespotsWrapper;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.events.CloseInfoWindowEvent;
import com.ddscanner.events.FilterChosedEvent;
import com.ddscanner.events.MarkerClickEvent;
import com.ddscanner.events.OnMapClickEvent;
import com.ddscanner.events.PlaceChoosedEvent;
import com.ddscanner.rest.BaseCallback;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.fragments.MapListFragment;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.LogUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class DiveSpotsClusterManager extends ClusterManager<DiveSpot> implements ClusterManager.OnClusterClickListener<DiveSpot>, GoogleMap.OnMapClickListener {

    private static final String TAG = DiveSpotsClusterManager.class.getName();
    private static final int CAMERA_ANIMATION_DURATION = 300;
    private final IconGenerator clusterIconGenerator1Symbol;
    private final IconGenerator clusterIconGenerator2Symbols;
    private final IconGenerator clusterIconGenerator3Symbols;
    private Context context;
    private GoogleMap googleMap;
    private MapListFragment parentFragment;
    private DiveSpotsRequestMap diveSpotsRequestMap = new DiveSpotsRequestMap();
    private DivespotsWrapper divespotsWrapper;
    private ArrayList<DiveSpot> diveSpots = new ArrayList<>();
    private HashMap<LatLng, DiveSpot> diveSpotsMap = new HashMap<>();
    private float lastZoom;

    private String currents;
    private String level;
    private String object;
    private int rating = -1;
    private String visibility;

    private boolean isCanMakeRequest = false;

    private ProgressBar progressBar;
    private RelativeLayout toast;

    private Marker lastClickedMarker;
    private Marker userCurrentLocationMarker;

    public DiveSpotsClusterManager(Context context, GoogleMap googleMap, RelativeLayout toast, ProgressBar progressBar, MapListFragment parentFragment) {
        super(context, googleMap);
        this.context = context;
        this.googleMap = googleMap;
        this.parentFragment = parentFragment;

        View clusterView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        clusterIconGenerator1Symbol = new IconGenerator(context);
        clusterView = inflater.inflate(R.layout.cluster_view_1_symbol, null);
        clusterView.findViewById(R.id.cluster_label).setId(com.google.maps.android.R.id.text);
        clusterIconGenerator1Symbol.setContentView(clusterView);
        clusterIconGenerator1Symbol.setBackground(null);
        clusterIconGenerator2Symbols = new IconGenerator(context);
        clusterView = inflater.inflate(R.layout.cluster_view_2_symbols, null);
        clusterView.findViewById(R.id.cluster_label).setId(com.google.maps.android.R.id.text);
        clusterIconGenerator2Symbols.setContentView(clusterView);
        clusterIconGenerator2Symbols.setBackground(null);
        clusterIconGenerator3Symbols = new IconGenerator(context);
        clusterView = inflater.inflate(R.layout.cluster_view_3_symbols, null);
        clusterView.findViewById(R.id.cluster_label).setId(com.google.maps.android.R.id.text);
        clusterIconGenerator3Symbols.setContentView(clusterView);
        clusterIconGenerator3Symbols.setBackground(null);

        this.toast = toast;
        this.progressBar = progressBar;
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
        lastClickedMarker = null;
        DDScannerApplication.bus.post(new CloseInfoWindowEvent());
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
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
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
            if (diveSpotsMap.get(lastClickedMarker.getPosition()) != null) {
                if (diveSpotsMap.get(lastClickedMarker.getPosition()).getStatus() != null) {
                    if (diveSpotsMap.get(lastClickedMarker.getPosition()).getStatus().equals("waiting")) {
                        DDScannerApplication.bus.post(new OnMapClickEvent(lastClickedMarker, true));
                    } else {
                        DDScannerApplication.bus.post(new OnMapClickEvent(lastClickedMarker, false));
                    }
                } else {
                    DDScannerApplication.bus.post(new OnMapClickEvent(lastClickedMarker, false));
                }
            } else {
                DDScannerApplication.bus.post(new OnMapClickEvent(lastClickedMarker, false));
            }
            lastClickedMarker = null;
        } else {
            DDScannerApplication.bus.post(new OnMapClickEvent(lastClickedMarker, false));
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        super.onCameraChange(cameraPosition);
        if (lastZoom != googleMap.getCameraPosition().zoom && lastClickedMarker != null) {
            lastZoom = googleMap.getCameraPosition().zoom;
            DDScannerApplication.bus.post(new OnMapClickEvent(null, false));
        }
        LatLng southwest = googleMap.getProjection().getVisibleRegion().latLngBounds.southwest;
        LatLng northeast = googleMap.getProjection().getVisibleRegion().latLngBounds.northeast;
        parentFragment.fillDiveSpots(getVisibleMarkersList(diveSpots));
        if (diveSpotsRequestMap.size() == 0) {
            diveSpotsRequestMap.putSouthWestLat(southwest.latitude - Math.abs(northeast.latitude - southwest.latitude));
            diveSpotsRequestMap.putSouthWestLng(southwest.longitude - Math.abs(northeast.longitude - southwest.longitude));
            diveSpotsRequestMap.putNorthEastLat(northeast.latitude + Math.abs(northeast.latitude - southwest.latitude));
            diveSpotsRequestMap.putNorthEastLng(northeast.longitude + Math.abs(northeast.longitude - southwest.longitude));
        }
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
        if (super.onMarkerClick(marker) || marker.equals(userCurrentLocationMarker)) {
            return true;
        }
        if (lastClickedMarker != null) {
            try {
                // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//                lastClickedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
                if (diveSpotsMap.get(marker.getPosition()).getStatus().equals("waiting")) {
                    lastClickedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_ds_new)));
                } else {
                    lastClickedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_ds)));
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        lastClickedMarker = marker;
        // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds_selected));
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_ds_selected)));
        if (diveSpotsMap.get(marker.getPosition()) != null) {
            DDScannerApplication.bus.post(new MarkerClickEvent(diveSpotsMap.get(marker.getPosition())));
        }
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

    public void updateFilter(String level, String object) {
        lastClickedMarker = null;
        if (level == null && object == null) {
            this.level = "";
            this.object = "";
            return;
        }
        this.level = level;
        this.object = object;
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
            Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDivespots(diveSpotsRequestMap);
            call.enqueue(new BaseCallback() {
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
                        parentFragment.fillDiveSpots(getVisibleMarkersList(diveSpots));
                        hidePb();
                    } else {
                        hidePb();
                    }
                }

                @Override
                public void onConnectionFailure() {
                    DialogUtils.showConnectionErrorDialog(context);
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
            BitmapDescriptor descriptor = null;

            int bucket = this.getBucket(cluster);
            String clusterLabel = getClusterText(bucket);
            int symbolsCount = clusterLabel.length();
            switch (symbolsCount) {
                case 0:
                case 1:
                    descriptor = BitmapDescriptorFactory.fromBitmap(clusterIconGenerator1Symbol.makeIcon(clusterLabel));
                    break;
                case 2:
                    descriptor = BitmapDescriptorFactory.fromBitmap(clusterIconGenerator2Symbols.makeIcon(clusterLabel));
                    break;
                case 3:
                    descriptor = BitmapDescriptorFactory.fromBitmap(clusterIconGenerator3Symbols.makeIcon(clusterLabel));
                    break;
                default:
                    clusterLabel = "99+";
                    descriptor = BitmapDescriptorFactory.fromBitmap(clusterIconGenerator3Symbols.makeIcon(clusterLabel));
                    break;
            }

            markerOptions.icon(descriptor);
        }

        @Override
        protected void onClusterItemRendered(DiveSpot diveSpot, final Marker marker) {
            super.onClusterItemRendered(diveSpot, marker);
            try {
                // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
                if (diveSpot.getStatus().equals("waiting")) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_ds_new)));
                } else {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_ds)));
                }
                if (lastClickedMarker != null && lastClickedMarker.getPosition().equals(marker.getPosition()) && lastClickedMarker.isInfoWindowShown()) {
                    //      marker.showInfoWindow();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<DiveSpot> getVisibleMarkersList(ArrayList<DiveSpot> oldList) {
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

    @Subscribe
    public void moveCameraByChosedLocation(PlaceChoosedEvent event) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(event.getLatLngBounds(), 0));
    }

    @Subscribe
    public void filterChosed(FilterChosedEvent event) {
        String level = null;
        String object = null;
        if (event.getLevel() != null) {
            level = event.getLevel();
        }
        if (event.getObject() != null) {
            object = event.getObject();
        }
        updateFilter(level, object);
        requestCityProducts();
    }

    public void setUserCurrentLocationMarker(Marker userCurrentLocationMarker) {
        this.userCurrentLocationMarker = userCurrentLocationMarker;
    }

    public Marker getLastClickedMarker() {
        return lastClickedMarker;
    }

    public boolean isLastClickedMarkerNew() {
        return diveSpotsMap.get(lastClickedMarker.getPosition()).getStatus().equals("waiting");
    }
}
