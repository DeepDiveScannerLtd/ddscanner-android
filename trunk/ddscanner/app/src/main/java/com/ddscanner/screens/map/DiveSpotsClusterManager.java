package com.ddscanner.screens.map;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
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

public class DiveSpotsClusterManager extends ClusterManager<DiveSpotShort> implements ClusterManager.OnClusterClickListener<DiveSpotShort>, GoogleMap.OnMapClickListener, GoogleMap.OnCameraChangeListener{

    private static final String TAG = DiveSpotsClusterManager.class.getName();
    private static final int CAMERA_ANIMATION_DURATION = 300;
    private final IconGenerator clusterIconGenerator1Symbol;
    private final IconGenerator clusterIconGenerator2Symbols;
    private final IconGenerator clusterIconGenerator3Symbols;
    private final FragmentActivity context;
    private GoogleMap googleMap;
    private MapListFragment parentFragment;
    private DiveSpotsRequestMap diveSpotsRequestMap = new DiveSpotsRequestMap();
    private ArrayList<DiveSpotShort> diveSpotShorts = new ArrayList<>();
    private HashMap<LatLng, DiveSpotShort> diveSpotsMap = new HashMap<>();
    private float lastZoom;

    private boolean isCanMakeRequest = false;

    private ProgressBar progressBar;
    private RelativeLayout toast;

    private Marker lastClickedMarker;
    private Marker userCurrentLocationMarker;
    private LatLng lastKnownSouthWest;
    private LatLng lastKnownNorthEast;
    private LatLng newDiveSpotLatLng;
    private boolean isNewDiveSpotMarkerClicked;
    private ArrayList<DiveSpotShort> allDiveSpots = new ArrayList<>();
    private int newDiveSpotId = -1;
    private FirstTimeSpotsLoadedListener firstTimeSpotsLoadedListener;
    private FragmentMapContract fragmentMapContract;

    public DiveSpotsClusterManager(FragmentActivity context, GoogleMap googleMap, RelativeLayout toast, ProgressBar progressBar, MapListFragment parentFragment, FragmentMapContract fragmentMapContract) {
        super(context, googleMap);
        this.context = context;
        this.googleMap = googleMap;
        this.parentFragment = parentFragment;
        this.fragmentMapContract = fragmentMapContract;

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
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        setAlgorithm(new GridBasedAlgorithm<DiveSpotShort>());
        setRenderer(new IconRenderer(context, googleMap, this));
        setOnClusterClickListener(this);
        if (checkArea(googleMap.getProjection().getVisibleRegion().latLngBounds.southwest, googleMap.getProjection().getVisibleRegion().latLngBounds.northeast)) {
            requestDiveSpots(false);
        } else {
            fragmentMapContract.showMessageToZoom();
        }
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        fragmentMapContract.hideDiveSpotInfo();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        super.onCameraChange(cameraPosition);
        if (lastZoom != googleMap.getCameraPosition().zoom && lastClickedMarker != null) {
            lastZoom = googleMap.getCameraPosition().zoom;
//            fragmentMapContract.hideDiveSpotInfo();
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
            fragmentMapContract.hideMessageToZoom();
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
            fragmentMapContract.showMessageToZoom();
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (super.onMarkerClick(marker) || marker.equals(userCurrentLocationMarker)) {
            return true;
        }
        fragmentMapContract.showDiveSpotInfo(marker, diveSpotsMap.get(marker.getPosition()));
        return true;
    }

    @Override
    public boolean onClusterClick(Cluster<DiveSpotShort> cluster) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (DiveSpotShort diveSpotShort : cluster.getItems()) {
            builder.include(diveSpotShort.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
        googleMap.animateCamera(cu, CAMERA_ANIMATION_DURATION, null);
        return true;
    }

    public void updateDiveSpots(ArrayList<DiveSpotShort> diveSpotShorts) {
        allDiveSpots = diveSpotShorts;
        Log.i(TAG, "incoming dive spots size = " + diveSpotShorts.size());
        final ArrayList<DiveSpotShort> newDiveSpotShorts = new ArrayList<>();
        newDiveSpotShorts.addAll(diveSpotShorts);
        newDiveSpotShorts.removeAll(this.diveSpotShorts);
        final ArrayList<DiveSpotShort> deletedDiveSpotShorts = new ArrayList<>();
        deletedDiveSpotShorts.addAll(this.diveSpotShorts);
        deletedDiveSpotShorts.removeAll(diveSpotShorts);
        Log.i(TAG, "removing " + deletedDiveSpotShorts.size() + " dive spots");
        for (DiveSpotShort diveSpotShort : deletedDiveSpotShorts) {
            removeDiveSpot(diveSpotShort);
        }
        Log.i(TAG, "adding " + newDiveSpotShorts.size() + " dive spots");
        for (DiveSpotShort diveSpotShort : newDiveSpotShorts) {
            addNewDiveSpot(diveSpotShort);
        }
        cluster();
    }

    private void addNewDiveSpot(DiveSpotShort diveSpotShort) {
        if (diveSpotShort.getPosition() == null) {
            Log.i(TAG, "addNewDiveSpot diveSpotShort.getPosition() == null");
        } else {
            addItem(diveSpotShort);
            diveSpotsMap.put(diveSpotShort.getPosition(), diveSpotShort);
            diveSpotShorts.add(diveSpotShort);
        }
    }

    private void removeDiveSpot(DiveSpotShort diveSpotShort) {
        removeItem(diveSpotShort);
        diveSpotsMap.remove(new LatLng(Double.valueOf(diveSpotShort.getLat()), Double.valueOf(diveSpotShort.getLng())));
        diveSpotShorts.remove(diveSpotShort);
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

    private class IconRenderer extends DefaultClusterRenderer<DiveSpotShort> {

        IconRenderer(Context context, GoogleMap map, ClusterManager<DiveSpotShort> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<DiveSpotShort> cluster, MarkerOptions markerOptions) {
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
        protected void onClusterItemRendered(DiveSpotShort diveSpotShort, final Marker marker) {
            super.onClusterItemRendered(diveSpotShort, marker);
            try {
                if (diveSpotShort.getIsNew()) {
                    if (lastClickedMarker == null || !lastClickedMarker.equals(marker)) {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds_new));
                    }
                } else {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
                }

                if (newDiveSpotId != -1) {
                    if (diveSpotShort.getId() == newDiveSpotId) {
                        onMarkerClick(marker);
                        newDiveSpotId = -1;
                    }
                }
                if (lastClickedMarker != null && lastClickedMarker.getPosition().equals(marker.getPosition()) && lastClickedMarker.isInfoWindowShown()) {
                    //      marker.showInfoWindow();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<DiveSpotShort> getVisibleMarkersList() {
        ArrayList<DiveSpotShort> newList = new ArrayList<>();
        for (DiveSpotShort diveSpotShort : allDiveSpots) {
            if (isSpotVisibleOnScreen(Float.valueOf(diveSpotShort.getLat()), Float.valueOf(diveSpotShort.getLng()))) {
                newList.add(diveSpotShort);
            }
        }
        return newList;
    }

    private boolean isSpotVisibleOnScreen(float lat, float lng) {
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

    @Subscribe
    public void moveCameraByChosenLocation(PlaceChoosedEvent event) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(event.getLatLngBounds(), 0));
    }

    @Subscribe
    public void filterChosen(FilterChosenEvent event) {
        lastClickedMarker = null;
        requestDiveSpots(true);
    }

    public void setUserCurrentLocationMarker(Marker userCurrentLocationMarker) {
        this.userCurrentLocationMarker = userCurrentLocationMarker;
    }

    public Marker getLastClickedMarker() {
        return lastClickedMarker;
    }

    public boolean isLastClickedMarkerNew() {
        return diveSpotsMap.get(lastClickedMarker.getPosition()).getIsNew();
    }

    private void sendRequest(LatLng northeast, LatLng southwest) {
        ArrayList<String> sealifesIds = null;
        diveSpotsRequestMap.clear();
        fragmentMapContract.showProgressBar();
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
        for (Map.Entry<String, Object> entry : diveSpotsRequestMap.entrySet()) {
            Log.i(TAG, "get dive spots request parameter: " + entry.getKey() + " " + entry.getValue());
        }
        fragmentMapContract.loadDiveSpots(sealifesIds, diveSpotsRequestMap);
//        DDScannerApplication.getInstance().getDdScannerRestClient(context).getDiveSpotsByArea(sealifesIds, diveSpotsRequestMap, getDiveSpotsByAreaResultListener);
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

}
