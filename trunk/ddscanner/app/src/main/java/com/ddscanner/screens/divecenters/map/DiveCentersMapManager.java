package com.ddscanner.screens.divecenters.map;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.support.annotation.NonNull;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCenter;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Point;
import com.mapbox.services.commons.models.Position;

import java.util.ArrayList;
import java.util.List;

public class DiveCentersMapManager implements MapboxMap.OnMapClickListener {

    private MapboxMap mapboxMap;
    private Context context;
    private DiveCentersMapContract diveCentersMapContract;
    private Gson gson = new Gson();
    private boolean markerSelected = false;
    boolean isDiveCentersDrawed = false;

    public DiveCentersMapManager(MapboxMap mapboxMap, Context context, DiveCentersMapContract diveCentersMapContract) {
        this.mapboxMap = mapboxMap;
        this.context = context;
        this.diveCentersMapContract = diveCentersMapContract;
        initMap();
    }

    private void initMap() {
        mapboxMap.setOnMapClickListener(this);
        mapboxMap.getUiSettings().setRotateGesturesEnabled(false);
    }

    public void drawMarkers(ArrayList<DiveCenter> diveCenters) {
        mapboxMap.removeLayer("marker-layer");
        mapboxMap.removeLayer("selected-marker-layer");
        mapboxMap.removeSource("selected-marker");
        mapboxMap.removeSource("selected-marker");
        mapboxMap.removeSource("marker-source");
        isDiveCentersDrawed = true;
        List<Feature> features = new ArrayList<>();
        for (DiveCenter diveCenter : diveCenters) {
            Feature feature = Feature.fromGeometry(Point.fromCoordinates(Position.fromCoordinates(diveCenter.getDoubleLng(), diveCenter.getDoubleLat())));
            feature.addStringProperty("divecenter", gson.toJson(diveCenter));
            features.add(feature);
        }
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(features);
        Source geoJsonSource = new GeoJsonSource("marker-source", featureCollection);
        mapboxMap.addSource(geoJsonSource);
        Bitmap icon = BitmapFactory.decodeResource(DDScannerApplication.getInstance().getResources(), R.drawable.ic_dc);
        Bitmap selectedMarkerIcon = BitmapFactory.decodeResource(DDScannerApplication.getInstance().getResources(), R.drawable.ic_dc_selected);
        // Add the marker image to map
        mapboxMap.addImage("my-marker-image", icon);
        mapboxMap.addImage("selected-marker", selectedMarkerIcon);

        SymbolLayer markers = new SymbolLayer("marker-layer", "marker-source")
                .withProperties(PropertyFactory.iconImage("my-marker-image"));
        mapboxMap.addLayer(markers);

        // Add the selected marker source and layer
        FeatureCollection emptySource = FeatureCollection.fromFeatures(new Feature[]{});
        Source selectedMarkerSource = new GeoJsonSource("selected-marker", emptySource);
        mapboxMap.addSource(selectedMarkerSource);

        SymbolLayer selectedMarker = new SymbolLayer("selected-marker-layer", "selected-marker")
                .withProperties(PropertyFactory.iconImage("my-marker-image"));
        mapboxMap.addLayer(selectedMarker);
    }

    public boolean isDiveCentersDrawed() {
        return isDiveCentersDrawed;
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
        marker.setProperties(PropertyFactory.iconImage("selected-marker"));
        String diveSpot = feature.getStringProperty("divecenter");
        diveCentersMapContract.showInfoWindow(gson.fromJson(diveSpot, DiveCenter.class));
        markerSelected = true;
    }

    private void deselectMarker(final SymbolLayer marker) {
        marker.setProperties(PropertyFactory.iconImage("my-marker-image"));
        diveCentersMapContract.hideInfoWindow();
        markerSelected = false;
    }

}
