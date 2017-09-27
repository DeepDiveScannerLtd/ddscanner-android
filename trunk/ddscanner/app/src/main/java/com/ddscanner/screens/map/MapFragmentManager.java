package com.ddscanner.screens.map;


import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Point;
import com.mapbox.services.commons.models.Position;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class MapFragmentManager implements MapboxMap.OnCameraIdleListener, MapboxMap.OnMapClickListener {

    private MapFragmentContract.View contract;
    private MapboxMap mapboxMap;
    private Map<LatLng, DiveSpotShort> markersMap = new HashMap<>();
    private DiveSpotsRequestMap diveSpotsRequestMap = new DiveSpotsRequestMap();
    private ArrayList<DiveSpotShort> diveSpotShorts = new ArrayList<>();
    private boolean markerSelected = false;
    private Gson gson = new Gson();
    private LatLng createdSpotLocation = null;

    public MapFragmentManager(MapboxMap mapboxMap, MapFragmentContract.View contract) {
        this.contract = contract;
        this.mapboxMap = mapboxMap;
        contract.showErrorMessage();
        initMap();
    }

    private void initMap() {
        mapboxMap.setMyLocationEnabled(true);
        mapboxMap.getUiSettings().setRotateGesturesEnabled(false);
        mapboxMap.setOnCameraIdleListener(this);
        mapboxMap.setOnMapClickListener(this);
    }



    @Override
    public void onCameraIdle() {
//        updateSpots();
        if (mapboxMap.getCameraPosition().zoom < 12) {
            contract.showErrorMessage();
        } else {
            contract.hideErrorMessage();
        }
        LatLng southWest = mapboxMap.getProjection().getVisibleRegion().latLngBounds.getSouthWest();
        LatLng northEast = mapboxMap.getProjection().getVisibleRegion().latLngBounds.getNorthEast();
//        if (diveSpotsRequestMap.size() == 0) {
//            diveSpotsRequestMap.putSouthWestLat(southWest.getLatitude() - Math.abs(northEast.getLatitude() - southWest.getLatitude()));
//            diveSpotsRequestMap.putSouthWestLng(southWest.getLongitude() - Math.abs(northEast.getLongitude() - southWest.getLongitude()));
//            diveSpotsRequestMap.putNorthEastLat(northEast.getLatitude() + Math.abs(northEast.getLatitude() - southWest.getLatitude()));
//            diveSpotsRequestMap.putNorthEastLng(northEast.getLongitude() + Math.abs(northEast.getLongitude() - southWest.getLongitude()));
//        }
        if (checkArea(southWest, northEast)) {
            if (diveSpotsRequestMap.size() == 0 || southWest.getLatitude() <= diveSpotsRequestMap.getSouthWestLat() ||
                    southWest.getLongitude() <= diveSpotsRequestMap.getSouthWestLng() ||
                    northEast.getLatitude() >= diveSpotsRequestMap.getNorthEastLat() ||
                    northEast.getLongitude() >= diveSpotsRequestMap.getNorthEastLng()) {
//                sendRequest(diveSpotsRequestMap);
                requestDiveSpots();
//                this.diveSpotShorts = getVisibleSpotsList(this.diveSpotShorts);
            }
        }
    }

    public ArrayList<DiveSpotShort> getVisibleSpotsList() {
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
            mapboxMap.removeLayer("marker-layer");
            mapboxMap.removeLayer("selected-marker-layer");
            mapboxMap.removeSource("selected-marker");
            mapboxMap.removeSource("selected-marker");
            mapboxMap.removeSource("marker-source");

            contract.hidePogressView();
            markersMap.clear();
            List<Feature> features = new ArrayList<>();
            for (DiveSpotShort diveSpotShort : diveSpotShorts) {
                markersMap.put(diveSpotShort.getNewPosition(), diveSpotShort);
                Feature feature = Feature.fromGeometry(Point.fromCoordinates(Position.fromCoordinates(diveSpotShort.getDoubleLng(), diveSpotShort.getDoubleLat())));
                feature.addStringProperty("divespot", gson.toJson(diveSpotShort));
                features.add(feature);
            }
            FeatureCollection featureCollection = FeatureCollection.fromFeatures(features);
            Source geoJsonSource = new GeoJsonSource("marker-source", featureCollection);
            mapboxMap.addSource(geoJsonSource);
            Bitmap icon = BitmapFactory.decodeResource(DDScannerApplication.getInstance().getResources(), R.drawable.ic_ds);

            // Add the marker image to map
            mapboxMap.addImage("my-marker-image", icon);

            SymbolLayer markers = new SymbolLayer("marker-layer", "marker-source")
                    .withProperties(PropertyFactory.iconImage("my-marker-image"));
            markers.setMinZoom(4);
            mapboxMap.addLayer(markers);

            // Add the selected marker source and layer
            FeatureCollection emptySource = FeatureCollection.fromFeatures(new Feature[]{});
            Source selectedMarkerSource = new GeoJsonSource("selected-marker", emptySource);
            mapboxMap.addSource(selectedMarkerSource);

            SymbolLayer selectedMarker = new SymbolLayer("selected-marker-layer", "selected-marker")
                    .withProperties(PropertyFactory.iconImage("my-marker-image"));
            mapboxMap.addLayer(selectedMarker);
            if (createdSpotLocation != null) {
                onMapClick(createdSpotLocation);
                createdSpotLocation = null;
            }
//        updateSpots();
    }

    private void addNewDiveSpot(DiveSpotShort diveSpotShort) {
        if (diveSpotShort.getPosition() == null) {
            return;
        }
        if (markersMap.get(diveSpotShort.getNewPosition()) == null) {
            markersMap.put(diveSpotShort.getNewPosition(), diveSpotShort);
            diveSpotShorts.add(diveSpotShort);
//            mapboxMap.addMarker(new MarkerOptions().position(diveSpotShort.getNewPosition()).icon(IconFactory.getInstance(DDScannerApplication.getInstance()).fromResource(R.drawable.ic_ds)));
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {

        final SymbolLayer marker = (SymbolLayer) mapboxMap.getLayer("selected-marker-layer");

        final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> features = mapboxMap.queryRenderedFeatures(pixel, "marker-layer");
        List<Feature> selectedFeature = mapboxMap.queryRenderedFeatures(pixel, "selected-marker-layer");
        if (selectedFeature.size() > 0 && markerSelected) {
            return;
        }

        if (features.isEmpty()) {
            if (markerSelected) {
                deselectMarker(marker);
            }
            return;
        }

        FeatureCollection featureCollection = FeatureCollection.fromFeatures(
                new Feature[]{Feature.fromGeometry(features.get(0).getGeometry())});
        GeoJsonSource source = mapboxMap.getSourceAs("selected-marker");
        if (source != null) {
            source.setGeoJson(featureCollection);
        }

        if (markerSelected) {
            deselectMarker(marker);
        }
        if (features.size() > 0) {
            selectMarker(marker, features.get(0));
        }
    }

    private void selectMarker(final SymbolLayer marker, Feature feature) {
        String diveSpot = feature.getStringProperty("divespot");
        contract.markerClicked(gson.fromJson(diveSpot, DiveSpotShort.class));
        markerSelected = true;
    }

    private void deselectMarker(final SymbolLayer marker) {
        contract.hideDiveSpotInfo();
        markerSelected = false;
    }

    private boolean checkArea(LatLng southWest, LatLng northEast) {
        return !(Math.abs(northEast.getLongitude() - southWest.getLongitude()) > 8 || Math.abs(northEast.getLatitude() - southWest.getLatitude()) > 8);
    }

    public void zoomIn() {

    }

    public void goToMyLocation() {
        if(mapboxMap.getMyLocation() != null) { // Check to ensure coordinates aren't null, probably a better way of doing this...
            mapboxMap.setCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(mapboxMap.getMyLocation().getLatitude(), mapboxMap.getMyLocation().getLongitude()))
            .zoom(7.0).build());
        }
    }

    public void moveCameraToPosition(LatLngBounds latLngBounds) {
        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
    }

    public void diveSpotAdded(LatLng latLng) {
        createdSpotLocation = latLng;
        LatLngBounds latLngBounds = LatLngBounds.from(latLng.getLatitude() + 0.01, latLng.getLongitude() + 0.01, latLng.getLatitude() - 0.01, latLng.getLongitude() - 0.01);
        mapboxMap.setOnCameraIdleListener(null);
        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
        mapboxMap.setOnCameraIdleListener(this);
        requestDiveSpots();
    }

}
