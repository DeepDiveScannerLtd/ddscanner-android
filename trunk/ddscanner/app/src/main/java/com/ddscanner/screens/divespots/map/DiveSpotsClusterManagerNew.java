package com.ddscanner.screens.divespots.map;


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.BaseMapEntity;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.events.CloseInfoWindowEvent;
import com.ddscanner.events.FilterChosenEvent;
import com.ddscanner.events.MarkerClickEvent;
import com.ddscanner.events.NewDiveSpotAddedEvent;
import com.ddscanner.events.OnMapClickEvent;
import com.ddscanner.events.PlaceChoosedEvent;
import com.ddscanner.interfaces.FirstTimeSpotsLoadedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.Helpers;
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
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiveSpotsClusterManagerNew extends ClusterManager<BaseMapEntity> implements ClusterManager.OnClusterClickListener<BaseMapEntity>, GoogleMap.OnMapClickListener, GoogleMap.OnCameraChangeListener {


    private DiveSpotMapFragmentController diveSpotMapFragmentController;
    private static final String TAG = DiveSpotsClusterManagerNew.class.getName();
    private static final int CAMERA_ANIMATION_DURATION = 300;
    private final IconGenerator clusterIconGenerator1Symbol;
    private final IconGenerator clusterIconGenerator2Symbols;
    private final IconGenerator clusterIconGenerator3Symbols;
    private final FragmentActivity context;
    private GoogleMap googleMap;
    private DiveSpotsRequestMap diveSpotsRequestMap = new DiveSpotsRequestMap();
    private ArrayList<BaseMapEntity> diveSpotShorts = new ArrayList<>();
    private HashMap<LatLng, BaseMapEntity> itemsMap = new HashMap<>();
    private float lastZoom;

    private boolean isCanMakeRequest = false;

    private Marker lastClickedMarker;
    private Marker userCurrentLocationMarker;
    private LatLng lastKnownSouthWest;
    private LatLng lastKnownNorthEast;
    private LatLng newDiveSpotLatLng;
    private boolean isNewDiveSpotMarkerClicked;
    private ArrayList<BaseMapEntity> allDiveSpots = new ArrayList<>();
    private int newDiveSpotId = -1;
    private FirstTimeSpotsLoadedListener firstTimeSpotsLoadedListener;
    

    public DiveSpotsClusterManagerNew(FragmentActivity context, GoogleMap googleMap, DiveSpotMapFragmentController diveSpotMapFragmentController) {
        super(context, googleMap);
        this.context = context;
        this.googleMap = googleMap;
        this.diveSpotMapFragmentController = diveSpotMapFragmentController;
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

        googleMap.setOnMapClickListener(this);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.setOnCameraChangeListener(this);
        googleMap.setOnMarkerClickListener(this);
        setAlgorithm(new GridBasedAlgorithm<BaseMapEntity>());
        setRenderer(new DiveSpotsClusterManagerNew.IconRenderer(context, googleMap, this));
        setOnClusterClickListener(this);
        if (checkArea(googleMap.getProjection().getVisibleRegion().latLngBounds.southwest, googleMap.getProjection().getVisibleRegion().latLngBounds.northeast)) {
            requestDiveSpots(false);
        } else {
            showToastAndHideInfo();
        }
        DDScannerApplication.bus.register(this);
    }

    private void showToastAndHideInfo() {
        diveSpotMapFragmentController.hideDiveCenternfo();
        diveSpotMapFragmentController.hideDiveSpotInfo();
        diveSpotMapFragmentController.showZoomInMessage();
    }

    private void hideProgressBar() {
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(() -> diveSpotMapFragmentController.hideProgressView(), 1200);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        diveSpotMapFragmentController.hideDiveSpotInfo();
        diveSpotMapFragmentController.hideDiveCenternfo();
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
//        parentFragment.fillDiveSpots(getVisibleMarkersList(diveSpotShorts));
        if (diveSpotsRequestMap.size() == 0) {
            diveSpotsRequestMap.putSouthWestLat(southwest.latitude - Math.abs(northeast.latitude - southwest.latitude));
            diveSpotsRequestMap.putSouthWestLng(southwest.longitude - Math.abs(northeast.longitude - southwest.longitude));
            diveSpotsRequestMap.putNorthEastLat(northeast.latitude + Math.abs(northeast.latitude - southwest.latitude));
            diveSpotsRequestMap.putNorthEastLng(northeast.longitude + Math.abs(northeast.longitude - southwest.longitude));
        }
        if (checkArea(southwest, northeast)) {
            diveSpotMapFragmentController.hideZoomInMessage();
//            hideToast();
            if (isCanMakeRequest) {
                if (southwest.latitude <= diveSpotsRequestMap.getSouthWestLat() ||
                        southwest.longitude <= diveSpotsRequestMap.getSouthWestLng() ||
                        northeast.latitude >= diveSpotsRequestMap.getNorthEastLat() ||
                        northeast.longitude >= diveSpotsRequestMap.getNorthEastLng()) {
                    requestDiveSpots(false);
                }
            } else {
                requestDiveSpots(false);
            }
        } else {
            showToastAndHideInfo();
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        BaseMapEntity baseMapEntity = itemsMap.get(marker.getPosition());
        if (!baseMapEntity.isDiveCenter()) {
            diveSpotMapFragmentController.showDiveSpotInfo(marker, itemsMap.get(marker.getPosition()));
            return true;
        }
        diveSpotMapFragmentController.showDiveCenterInfo(marker, itemsMap.get(marker.getPosition()));
        return true;
    }

    @Override
    public boolean onClusterClick(Cluster<BaseMapEntity> cluster) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (BaseMapEntity baseMapEntity : cluster.getItems()) {
            builder.include(baseMapEntity.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
        googleMap.animateCamera(cu, CAMERA_ANIMATION_DURATION, null);
        return true;
    }

    public void updateDiveSpots(ArrayList<BaseMapEntity> diveSpotShorts) {
        this.allDiveSpots = new ArrayList<>(diveSpotShorts);
        Log.i(TAG, "incoming dive spots size = " + diveSpotShorts.size());
        final ArrayList<BaseMapEntity> newDiveSpotShorts = new ArrayList<>();
        newDiveSpotShorts.addAll(diveSpotShorts);
        newDiveSpotShorts.removeAll(this.diveSpotShorts);
        final ArrayList<BaseMapEntity> deletedDiveSpotShorts = new ArrayList<>();
        deletedDiveSpotShorts.addAll(this.diveSpotShorts);
        deletedDiveSpotShorts.removeAll(diveSpotShorts);
        Log.i(TAG, "removing " + deletedDiveSpotShorts.size() + " dive spots");
        for (BaseMapEntity baseMapEntity : deletedDiveSpotShorts) {
            removeDiveSpot(baseMapEntity);
        }
        Log.i(TAG, "adding " + newDiveSpotShorts.size() + " dive spots");
        for (BaseMapEntity baseMapEntity : newDiveSpotShorts) {
            addNewItem(baseMapEntity);
        }
        cluster();
    }

    private void addNewItem(BaseMapEntity baseMapEntity) {
        if (baseMapEntity.getPosition() == null) {
            Log.i(TAG, "addNewItem baseMapEntity.getPosition() == null");
        } else {
            addItem(baseMapEntity);
            itemsMap.put(baseMapEntity.getPosition(), baseMapEntity);
            diveSpotShorts.add(baseMapEntity);
        }
    }

    private void removeDiveSpot(BaseMapEntity baseMapEntity) {
        removeItem(baseMapEntity);
        itemsMap.remove(baseMapEntity.getPosition());
        diveSpotShorts.remove(baseMapEntity);
    }

    private boolean checkArea(LatLng southWest, LatLng northEast) {
        return !(Math.abs(northEast.longitude - southWest.longitude) > 8 || Math.abs(northEast.latitude - southWest.latitude) > 8);
    }

    private void requestDiveSpots(boolean isFromFilters) {
        diveSpotsRequestMap.clear();
        LatLng southwest = googleMap.getProjection().getVisibleRegion().latLngBounds.southwest;
        LatLng northeast = googleMap.getProjection().getVisibleRegion().latLngBounds.northeast;
        if (!checkArea(southwest, northeast) && isFromFilters && lastKnownNorthEast != null && lastKnownSouthWest != null) {
            southwest = lastKnownSouthWest;
            northeast = lastKnownNorthEast;
        }
        if (checkArea(southwest, northeast)) {
            sendRequest(northeast, southwest);
        }
    }

    private class IconRenderer extends DefaultClusterRenderer<BaseMapEntity> {

        IconRenderer(Context context, GoogleMap map, ClusterManager<BaseMapEntity> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<BaseMapEntity> cluster, MarkerOptions markerOptions) {
            BitmapDescriptor descriptor;

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
        protected void onClusterItemRendered(BaseMapEntity baseMapEntity, final Marker marker) {
            super.onClusterItemRendered(baseMapEntity, marker);
            try {
                if (baseMapEntity.isDiveCenter()) {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dc));
                } else {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
                }

                if (newDiveSpotId != -1) {
                    if (baseMapEntity.getId() == newDiveSpotId) {
                        onMarkerClick(marker);
                        newDiveSpotId = -1;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<BaseMapEntity> getVisibleMarkersList(boolean isDiveCenters) {
        ArrayList<BaseMapEntity> newList = new ArrayList<>();
        for (BaseMapEntity baseMapEntity : allDiveSpots) {
            if (isSpotVisibleOnScreen(baseMapEntity.getPosition().latitude, baseMapEntity.getPosition().longitude)) {
                if (baseMapEntity.isDiveCenter() == isDiveCenters) {
                    newList.add(baseMapEntity);
                }
            }
        }
        return newList;
    }

    private boolean isSpotVisibleOnScreen(double lat, double lng) {
        LatLng southwest = googleMap.getProjection().getVisibleRegion().latLngBounds.southwest;
        LatLng northeast = googleMap.getProjection().getVisibleRegion().latLngBounds.northeast;
        return lat < northeast.latitude && lat > southwest.latitude && lng < northeast.longitude && lng > southwest.longitude;
    }

    public void mapZoomPlus() {
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    public void mapZoomMinus() {
        googleMap.animateCamera(CameraUpdateFactory.zoomOut());
    }


    public void setUserCurrentLocationMarker(Marker userCurrentLocationMarker) {
        this.userCurrentLocationMarker = userCurrentLocationMarker;
    }

    public Marker getLastClickedMarker() {
        return lastClickedMarker;
    }

    private void sendRequest(LatLng northeast, LatLng southwest) {
        ArrayList<String> sealifesIds = null;
        diveSpotsRequestMap.clear();
        diveSpotMapFragmentController.showProgressView();
        isCanMakeRequest = true;
        lastKnownSouthWest = southwest;
        lastKnownNorthEast = northeast;
        diveSpotsRequestMap.putSouthWestLat(southwest.latitude - Math.abs(northeast.latitude - southwest.latitude));
        diveSpotsRequestMap.putSouthWestLng(southwest.longitude - Math.abs(northeast.longitude - southwest.longitude));
        diveSpotsRequestMap.putNorthEastLat(northeast.latitude + Math.abs(northeast.latitude - southwest.latitude));
        diveSpotsRequestMap.putNorthEastLng(northeast.longitude + Math.abs(northeast.longitude - southwest.longitude));
        if (!TextUtils.isEmpty(DDScannerApplication.getInstance().getSharedPreferenceHelper().getLevel())) {
            diveSpotsRequestMap.putLevel(DDScannerApplication.getInstance().getSharedPreferenceHelper().getLevel());
        }
        if (!TextUtils.isEmpty(DDScannerApplication.getInstance().getSharedPreferenceHelper().getObject())) {
            diveSpotsRequestMap.putObject(DDScannerApplication.getInstance().getSharedPreferenceHelper().getObject());
        }
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getSealifesList() != null) {
            ArrayList<SealifeShort> sealifeShorts = DDScannerApplication.getInstance().getSharedPreferenceHelper().getSealifesList();
            sealifesIds = new ArrayList<>();
            for (SealifeShort sealifeShort : sealifeShorts) {
                sealifesIds.add(sealifeShort.getId());
            }
        }
        diveSpotMapFragmentController.requestDiveSpots(sealifesIds, diveSpotsRequestMap);
    }

    @Subscribe
    public void newDiveSpotAdded(NewDiveSpotAddedEvent event) {
        googleMap.setOnCameraChangeListener(null);
        newDiveSpotId = Integer.parseInt(event.getDiveSpotId());
        lastClickedMarker = null;
        isNewDiveSpotMarkerClicked = true;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(new LatLng(event.getLatLng().latitude - 0.05, event.getLatLng().longitude - 0.05), new LatLng(event.getLatLng().latitude + 0.05, event.getLatLng().longitude + 0.05)),0));
        //sendRequest(new LatLng(event.getLatLng().latitude - 0.05, event.getLatLng().longitude - 0.05), new LatLng(event.getLatLng().latitude + 0.05, event.getLatLng().longitude + 0.05));
        googleMap.setOnCameraChangeListener(this);
        requestDiveSpots(true);
    }

    public void moveCamera(LatLngBounds latLngBounds) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
    }

}
