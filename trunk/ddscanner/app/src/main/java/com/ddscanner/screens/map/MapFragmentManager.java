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
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
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

public class MapFragmentManager implements MapboxMap.OnCameraIdleListener, MapboxMap.OnMarkerClickListener, MapboxMap.OnMapClickListener {

    private MapFragmentContract.View contract;
    private MapboxMap mapboxMap;
    private Map<LatLng, DiveSpotShort> markersMap = new HashMap<>();
    private DiveSpotsRequestMap diveSpotsRequestMap = new DiveSpotsRequestMap();
    private ArrayList<DiveSpotShort> diveSpotShorts = new ArrayList<>();
    private Bitmap icon;
    List<Feature> markerCoordinates = new ArrayList<>();
    private boolean markerSelected = false;

    public MapFragmentManager(MapboxMap mapboxMap, MapFragmentContract.View contract) {
        this.contract = contract;
        this.mapboxMap = mapboxMap;
        icon = BitmapFactory.decodeResource(DDScannerApplication.getInstance().getResources(), R.drawable.ic_ds);
        initMap();
    }

    private void initMap() {
        mapboxMap.setMyLocationEnabled(true);
        mapboxMap.getUiSettings().setRotateGesturesEnabled(false);
        mapboxMap.setOnCameraIdleListener(this);
        mapboxMap.setOnMarkerClickListener(this);

        FeatureCollection featureCollection = FeatureCollection.fromFeatures(markerCoordinates);
        Source geoJsonSource = new GeoJsonSource("marker-source", featureCollection);
        mapboxMap.addSource(geoJsonSource);

        mapboxMap.addImage("my-marker-image", icon);

        SymbolLayer markers = new SymbolLayer("marker-layer", "marker-source").withProperties(PropertyFactory.iconImage("my-marker-image"));
        mapboxMap.addLayer(markers);
        FeatureCollection emptySource = FeatureCollection.fromFeatures(new Feature[]{});
        Source selectedMarkerSource = new GeoJsonSource("selected-marker", emptySource);
        mapboxMap.addSource(selectedMarkerSource);

        SymbolLayer selectedMarker = new SymbolLayer("selected-marker-layer", "selected-marker").withProperties(PropertyFactory.iconImage("my-marker-image"));
        mapboxMap.addLayer(selectedMarker);
        mapboxMap.setOnMapClickListener(this);

//        addClusteredGeoJsonSource();
    }



    @Override
    public void onCameraIdle() {
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
            }
        } else {
            contract.showErrorMessage();
        }
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
    }

    void updateDiveSpots(ArrayList<DiveSpotShort> diveSpotShorts) {
        this.diveSpotShorts.addAll(diveSpotShorts);
        for (DiveSpotShort diveSpotShort : diveSpotShorts) {
            markerCoordinates = new ArrayList<>();
            markerCoordinates.add(Feature.fromGeometry(
                    Point.fromCoordinates(Position.fromCoordinates(diveSpotShort.getPosition().latitude, diveSpotShort.getPosition().longitude))) // Boston Common Park
            );
            addNewDiveSpot(diveSpotShort);
        }
    }

    private void addNewDiveSpot(DiveSpotShort diveSpotShort) {
        if (diveSpotShort.getPosition() == null) {
            return;
        }
//        addItem(diveSpotShort);
        if (markersMap.get(diveSpotShort.getNewPosition()) == null) {
            markersMap.put(diveSpotShort.getNewPosition(), diveSpotShort);
            diveSpotShorts.add(diveSpotShort);
//            mapboxMap.addMarker(new MarkerOptions().position(diveSpotShort.getNewPosition()).icon(IconFactory.getInstance(DDScannerApplication.getInstance()).fromResource(R.drawable.ic_ds)));
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        contract.markerClicked(marker, markersMap.get(marker.getPosition()));
        return true;
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
//        contract.hideDiveSpotInfo();
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
            selectMarker(marker);
        }
    }

    private void selectMarker(final SymbolLayer marker) {
        ValueAnimator markerAnimator = new ValueAnimator();
        markerAnimator.setObjectValues(1f, 2f);
        markerAnimator.setDuration(300);
        markerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                marker.setProperties(
                        PropertyFactory.iconSize((float) animator.getAnimatedValue())
                );
            }
        });
        markerAnimator.start();
        markerSelected = true;
    }

    private void deselectMarker(final SymbolLayer marker) {
        ValueAnimator markerAnimator = new ValueAnimator();
        markerAnimator.setObjectValues(2f, 1f);
        markerAnimator.setDuration(300);
        markerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                marker.setProperties(
                        PropertyFactory.iconSize((float) animator.getAnimatedValue())
                );
            }
        });
        markerAnimator.start();
        markerSelected = false;
    }

    private boolean checkArea(LatLng southWest, LatLng northEast) {
        return !(Math.abs(northEast.getLongitude() - southWest.getLongitude()) > 8 || Math.abs(northEast.getLatitude() - southWest.getLatitude()) > 8);
    }

    private void addClusteredGeoJsonSource() {
        try {
            mapboxMap.removeSource("earthquakes");
        } catch (Exception ignored) {

        }
        // Add a new source from the GeoJSON data and set the 'cluster' option to true.
        try {
            mapboxMap.addSource(
                    // Point to GeoJSON data. This example visualizes all M1.0+ earthquakes from
                    // 12/22/15 to 1/21/16 as logged by USGS' Earthquake hazards program.
                    new GeoJsonSource("earthquakes",
                            FeatureCollection.fromFeatures(markerCoordinates),
                            new GeoJsonOptions()
                                    .withCluster(true)
                                    .withClusterMaxZoom(14)
                                    .withClusterRadius(50)
                    )
            );
        } catch (Exception ignored) {

        }


        // Use the earthquakes GeoJSON source to create three layers: One layer for each cluster category.
        // Each point range gets a different fill color.
        int[][] layers = new int[][] {
                new int[] {150, ContextCompat.getColor(DDScannerApplication.getInstance(), R.color.tw__composer_red)},
                new int[] {20, ContextCompat.getColor(DDScannerApplication.getInstance(), R.color.orange)},
                new int[] {0, ContextCompat.getColor(DDScannerApplication.getInstance(), R.color.mapbox_blue)}
        };

        //Creating a marker layer for single data points
        SymbolLayer unclustered = new SymbolLayer("unclustered-points", "earthquakes");
        unclustered.setProperties(iconImage("marker-15"));
        mapboxMap.addLayer(unclustered);

        for (int i = 0; i < layers.length; i++) {
            //Add clusters' circles
            CircleLayer circles = new CircleLayer("cluster-" + i, "earthquakes");
            circles.setProperties(
                    circleColor(layers[i][1]),
                    circleRadius(18f)
            );

            // Add a filter to the cluster layer that hides the circles based on "point_count"
            circles.setFilter(
                    i == 0
                            ? gte("point_count", layers[i][0]) :
                            all(gte("point_count", layers[i][0]), lt("point_count", layers[i - 1][0]))
            );
            mapboxMap.addLayer(circles);
        }

        //Add the count labels
        SymbolLayer count = new SymbolLayer("count", "earthquakes");
        count.setProperties(
                textField("{point_count}"),
                textSize(12f),
                textColor(Color.WHITE)
        );
        mapboxMap.addLayer(count);

    }

}
