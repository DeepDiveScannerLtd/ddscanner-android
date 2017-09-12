package com.ddscanner.screens.map;


import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.mapbox.mapboxsdk.style.layers.Filter.all;
import static com.mapbox.mapboxsdk.style.layers.Filter.gte;
import static com.mapbox.mapboxsdk.style.layers.Filter.lt;
import static com.mapbox.mapboxsdk.style.layers.Filter.neq;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleBlur;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

public class MapFragmentManager implements MapboxMap.OnCameraIdleListener, MapboxMap.OnMarkerClickListener, MapboxMap.OnMapClickListener {

    private MapFragmentContract.View contract;
    private MapboxMap mapboxMap;
    private Map<LatLng, DiveSpotShort> markersMap = new HashMap<>();
    private DiveSpotsRequestMap diveSpotsRequestMap = new DiveSpotsRequestMap();
    private ArrayList<DiveSpotShort> diveSpotShorts = new ArrayList<>();

    public MapFragmentManager(MapboxMap mapboxMap, MapFragmentContract.View contract) {
        this.contract = contract;
        this.mapboxMap = mapboxMap;
        initMap();
    }

    private void initMap() {
        mapboxMap.setMyLocationEnabled(true);
        mapboxMap.getUiSettings().setRotateGesturesEnabled(false);
        mapboxMap.setOnCameraIdleListener(this);
        mapboxMap.setOnMarkerClickListener(this);
        mapboxMap.setOnMapClickListener(this);
    }



    @Override
    public void onCameraIdle() {
        updateSpots();
        LatLng southWest = mapboxMap.getProjection().getVisibleRegion().latLngBounds.getSouthWest();
        LatLng northEast = mapboxMap.getProjection().getVisibleRegion().latLngBounds.getNorthEast();
        if (diveSpotsRequestMap.size() == 0) {
            diveSpotsRequestMap.putSouthWestLat(southWest.getLatitude() - Math.abs(northEast.getLatitude() - southWest.getLatitude()));
            diveSpotsRequestMap.putSouthWestLng(southWest.getLongitude() - Math.abs(northEast.getLongitude() - southWest.getLongitude()));
            diveSpotsRequestMap.putNorthEastLat(northEast.getLatitude() + Math.abs(northEast.getLatitude() - southWest.getLatitude()));
            diveSpotsRequestMap.putNorthEastLng(northEast.getLongitude() + Math.abs(northEast.getLongitude() - southWest.getLongitude()));
        }
        if (checkArea(southWest, northEast)) {
            if (southWest.getLatitude() <= diveSpotsRequestMap.getSouthWestLat() ||
                    southWest.getLongitude() <= diveSpotsRequestMap.getSouthWestLng() ||
                    northEast.getLatitude() >= diveSpotsRequestMap.getNorthEastLat() ||
                    northEast.getLongitude() >= diveSpotsRequestMap.getNorthEastLng()) {
//                sendRequest(diveSpotsRequestMap);
                requestDiveSpots();
                this.diveSpotShorts = getVisibleSpotsList(this.diveSpotShorts);
            }
        }
    }

    private void updateSpots() {
        mapboxMap.clear();
        markersMap.clear();
        if (mapboxMap.getCameraPosition().zoom < 7) {
            contract.showErrorMessage();
            return;
        }
        ArrayList<DiveSpotShort> visibleSpots = new ArrayList<>();
        ArrayList<DiveSpotShort> spotsOnScreen = getVisibleSpotsList(diveSpotShorts);
        if (spotsOnScreen.size() > 50) {
            for (int i = 0; i < 50; i++) {
                visibleSpots.add(spotsOnScreen.get(i));
                contract.showErrorMessage();
            }
        } else {
            contract.hideErrorMessage();
            visibleSpots = new ArrayList<>(spotsOnScreen);
        }
        for (DiveSpotShort diveSpotShort : visibleSpots) {
            addNewDiveSpot(diveSpotShort);
        }
    }

    private ArrayList<DiveSpotShort> getVisibleSpotsList(ArrayList<DiveSpotShort> diveSpotShorts) {
        ArrayList<DiveSpotShort> newList = new ArrayList<>();
        for (DiveSpotShort diveSpotShort : diveSpotShorts) {
            if (isSpotVisibleOnScreen(Float.valueOf(diveSpotShort.getLat()), Float.valueOf(diveSpotShort.getLng()))) {
                newList.add(diveSpotShort);
            }
        }
        return newList;
    }

    private boolean isSpotVisibleOnScreen(float lat, float lng) {
        LatLng southwest = mapboxMap.getProjection().getVisibleRegion().latLngBounds.getSouthWest();
        LatLng northeast = mapboxMap.getProjection().getVisibleRegion().latLngBounds.getNorthEast();
        return lat < northeast.getLatitude() && lat > southwest.getLatitude() && lng < northeast.getLongitude() && lng > southwest.getLongitude();
    }

    private void requestDiveSpots() {
        diveSpotsRequestMap.clear();
        LatLng southwest = mapboxMap.getProjection().getVisibleRegion().latLngBounds.getSouthWest();
        LatLng northeast = mapboxMap.getProjection().getVisibleRegion().latLngBounds.getNorthEast();

        if (checkArea(southwest, northeast)) {
            sendRequest(northeast, southwest);
        }
    }
    private void sendRequest(LatLng northEast, LatLng southWest) {
        ArrayList<String> sealifesIds = null;
        diveSpotsRequestMap.clear();

        diveSpotsRequestMap.putSouthWestLat(southWest.getLatitude() - Math.abs(northEast.getLatitude() - southWest.getLatitude()));
        diveSpotsRequestMap.putSouthWestLng(southWest.getLongitude() - Math.abs(northEast.getLongitude() - southWest.getLongitude()));
        diveSpotsRequestMap.putNorthEastLat(northEast.getLatitude() + Math.abs(northEast.getLatitude() - southWest.getLatitude()));
        diveSpotsRequestMap.putNorthEastLng(northEast.getLongitude() + Math.abs(northEast.getLongitude() - southWest.getLongitude()));
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
        contract.loadData(diveSpotsRequestMap, sealifesIds);
        contract.showPogressView();
    }

    void updateDiveSpots(ArrayList<DiveSpotShort> diveSpotShorts) {
        this.diveSpotShorts.addAll(diveSpotShorts);
        contract.hidePogressView();
        updateSpots();
    }

    private void addNewDiveSpot(DiveSpotShort diveSpotShort) {
        if (diveSpotShort.getPosition() == null) {
            return;
        }
        if (markersMap.get(diveSpotShort.getNewPosition()) == null) {
            markersMap.put(diveSpotShort.getNewPosition(), diveSpotShort);
            diveSpotShorts.add(diveSpotShort);
            mapboxMap.addMarker(new MarkerOptions().position(diveSpotShort.getNewPosition()).icon(IconFactory.getInstance(DDScannerApplication.getInstance()).fromResource(R.drawable.ic_ds)));
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        contract.markerClicked(marker, markersMap.get(marker.getPosition()));
        return true;
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        contract.hideDiveSpotInfo();
    }

    private boolean checkArea(LatLng southWest, LatLng northEast) {
        return !(Math.abs(northEast.getLongitude() - southWest.getLongitude()) > 8 || Math.abs(northEast.getLatitude() - southWest.getLatitude()) > 8);
    }

}
