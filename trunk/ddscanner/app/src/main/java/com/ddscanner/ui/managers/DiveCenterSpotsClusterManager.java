package com.ddscanner.ui.managers;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.events.MarkerClickedEvent;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiveCenterSpotsClusterManager extends ClusterManager<DiveSpotShort> implements ClusterManager.OnClusterClickListener<DiveSpotShort>, GoogleMap.OnMarkerClickListener {

    private static final String TAG = DiveCentersClusterManager.class.getName();
    private static final int CAMERA_ANIMATION_DURATION = 300;

    private GoogleMap googleMap;

    private final IconGenerator clusterIconGenerator1Symbol;
    private final IconGenerator clusterIconGenerator2Symbols;
    private final IconGenerator clusterIconGenerator3Symbols;
    private ArrayList<Marker> markers = new ArrayList<>();
    private Map<LatLng, DiveSpotShort> diveSpotsMap = new HashMap<>();

    public DiveCenterSpotsClusterManager(FragmentActivity context, GoogleMap googleMap) {
        super(context, googleMap);
        this.googleMap = googleMap;
        //   this.clusterBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.ic_number);
        IconGenerator clusterIconGenerator = new IconGenerator(context);
        clusterIconGenerator.setContentView(this.makeSquareTextView(context));
        clusterIconGenerator.setTextAppearance(com.google.maps.android.R.style.ClusterIcon_TextAppearance);
        clusterIconGenerator.setBackground(null);

        View clusterView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        clusterIconGenerator1Symbol = new IconGenerator(context);
        clusterView = inflater.inflate(R.layout.cluster_dive_center_view_1_symbol, null);
        clusterView.findViewById(R.id.cluster_label).setId(com.google.maps.android.R.id.text);
        clusterIconGenerator1Symbol.setContentView(clusterView);
        clusterIconGenerator1Symbol.setBackground(null);
        clusterIconGenerator2Symbols = new IconGenerator(context);
        clusterView = inflater.inflate(R.layout.cluster_dive_center_view_2_symbol, null);
        clusterView.findViewById(R.id.cluster_label).setId(com.google.maps.android.R.id.text);
        clusterIconGenerator2Symbols.setContentView(clusterView);
        clusterIconGenerator2Symbols.setBackground(null);
        clusterIconGenerator3Symbols = new IconGenerator(context);
        clusterView = inflater.inflate(R.layout.cluster_dive_center_view_3_symbol, null);
        clusterView.findViewById(R.id.cluster_label).setId(com.google.maps.android.R.id.text);
        clusterIconGenerator3Symbols.setContentView(clusterView);
        clusterIconGenerator3Symbols.setBackground(null);

        setAlgorithm(new GridBasedAlgorithm<DiveSpotShort>());
        setRenderer(new IconRenderer(context, googleMap, this));
        setOnClusterClickListener(this);
    }

    @Override
    public boolean onClusterClick(Cluster<DiveSpotShort> cluster) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (DiveSpotShort diveCenter : cluster.getItems()) {
            builder.include(diveCenter.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150);
        googleMap.animateCamera(cu, CAMERA_ANIMATION_DURATION, null);
        return true;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (diveSpotsMap.get(marker.getPosition()) == null) {
            return super.onMarkerClick(marker);
        }
        DDScannerApplication.bus.post(new MarkerClickedEvent(marker));
        return false;
    }

    private SquareTextView makeSquareTextView(Context context) {
        SquareTextView squareTextView = new SquareTextView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        squareTextView.setLayoutParams(layoutParams);
        squareTextView.setId(com.google.maps.android.R.id.text);
        return squareTextView;
    }

    public void mapZoomPlus() {
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    public void mapZoomMinus() {
        googleMap.animateCamera(CameraUpdateFactory.zoomOut());
    }

    public void setDiveSpotsMap(Map<LatLng, DiveSpotShort> diveSpotsMap) {
        this.diveSpotsMap = diveSpotsMap;
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
        protected void onClusterItemRendered(DiveSpotShort diveSpot, final Marker marker) {
            super.onClusterItemRendered(diveSpot, marker);
            try {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
                markers.add(marker);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
