package com.ddscanner.ui.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.entities.DiveCentersResponseEntity;
import com.ddscanner.events.DiveCenterMarkerClickEvent;
import com.ddscanner.events.MarkerClickEvent;
import com.ddscanner.events.OnMapClickEvent;
import com.ddscanner.events.PutDiveCentersToListEvent;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.activities.DiveCenterDetailsActivity;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class DiveCentersClusterManager extends ClusterManager<DiveCenter> implements ClusterManager.OnClusterClickListener<DiveCenter>, GoogleMap.OnMapClickListener {

    private static final String TAG = DiveCentersClusterManager.class.getName();
    private static final int CAMERA_ANIMATION_DURATION = 300;

    private Context context;
    private GoogleMap googleMap;
    private Marker lastClickedMarker;
    private Marker diveSpotMarker;
    private HashMap<LatLng, DiveCenter> diveCentersMap = new HashMap<>();
    private Drawable clusterBackgroundDrawable;
    private final IconGenerator clusterIconGenerator;
    private HashMap<Marker, Bitmap> markerBitmapCache = new HashMap<>();
    private List<DiveCenter> diveCenters = new ArrayList<>();
    private String logoPath;
    private DiveCentersResponseEntity diveCentersResponseEntity;

    private Marker userCurrentLocationMarker;

    private final IconGenerator clusterIconGenerator1Symbol;
    private final IconGenerator clusterIconGenerator2Symbols;
    private final IconGenerator clusterIconGenerator3Symbols;

    public DiveCentersClusterManager(Context context, GoogleMap googleMap, LatLng diveSpotLatLng, String diveSpotName) {
        super(context, googleMap);
        this.context = context;
        this.googleMap = googleMap;
        this.clusterBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.ic_number);
        this.clusterIconGenerator = new IconGenerator(context);
        this.clusterIconGenerator.setContentView(this.makeSquareTextView(context));
        this.clusterIconGenerator.setTextAppearance(com.google.maps.android.R.style.ClusterIcon_TextAppearance);
        this.clusterIconGenerator.setBackground(clusterBackgroundDrawable);

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

        setAlgorithm(new GridBasedAlgorithm<DiveCenter>());
        setRenderer(new IconRenderer(context, googleMap, this));
        setOnClusterClickListener(this);
        this.googleMap.setOnMapClickListener(this);
        requestDiveCenters(diveSpotLatLng);

        for (DiveCenter diveCenter : diveCenters) {
            addItem(diveCenter);
            diveCentersMap.put(diveCenter.getPosition(), diveCenter);
        }
        // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//        diveSpotMarker = googleMap.addMarker(new MarkerOptions().position(diveSpotLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds)).title(diveSpotName));
        diveSpotMarker = googleMap.addMarker(new MarkerOptions().position(diveSpotLatLng).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_ds))).title(diveSpotName));
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        if (!marker.getPosition().equals(diveSpotMarker.getPosition())) {
            AppsFlyerLib.getInstance().trackEvent(context, EventTrackerHelper
                    .EVENT_INFOWINDOW_CLICK, new HashMap<String, Object>() {{
                put(EventTrackerHelper.PARAM_MARKER_CLICK_TYPE, "dive_center");
                put(EventTrackerHelper.PARAM_MARKER_CLICK_PLACE_ID, String.valueOf(diveCentersMap.get(marker.getPosition()).getId()));
            }});
            DiveCenterDetailsActivity.show(context, diveCentersMap.get(marker.getPosition()), logoPath);
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        super.onCameraChange(cameraPosition);
      //  diveCentersPagerAdapter.populateDiveCentersList(changeListToListFragment((ArrayList<DiveCenter>)diveCenters), logoPath);
        DDScannerApplication.bus.post(new PutDiveCentersToListEvent(changeListToListFragment((ArrayList<DiveCenter>)diveCenters), logoPath));
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (super.onMarkerClick(marker) || marker.equals(diveSpotMarker) || marker.equals(userCurrentLocationMarker)) {
            return true;
        }
        if (lastClickedMarker != null) {
            // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//                lastClickedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
            lastClickedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.pin_dc)));
        }
        lastClickedMarker = marker;
        // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds_selected));
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_dc_selected)));
        if (diveCentersMap.get(marker.getPosition())!= null) {
            DDScannerApplication.bus.post(new DiveCenterMarkerClickEvent(diveCentersMap.get(marker.getPosition()), logoPath));
        }
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (lastClickedMarker != null) {
            // lastClickedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
            DDScannerApplication.bus.post(new OnMapClickEvent(lastClickedMarker, false));
            lastClickedMarker = null;
        } else {
            DDScannerApplication.bus.post(new OnMapClickEvent(lastClickedMarker, false));
        }
    }

    @Override
    public boolean onClusterClick(Cluster<DiveCenter> cluster) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (DiveCenter diveCenter : cluster.getItems()) {
            builder.include(diveCenter.getPosition());
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

    private class IconRenderer extends DefaultClusterRenderer<DiveCenter> {

        public IconRenderer(Context context, GoogleMap map, ClusterManager<DiveCenter> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<DiveCenter> cluster, MarkerOptions markerOptions) {
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
        protected void onClusterItemRendered(DiveCenter diveSpot, final Marker marker) {
            super.onClusterItemRendered(diveSpot, marker);
            try {
                // TODO Change this after google fixes play services bug https://github.com/googlemaps/android-maps-utils/issues/276
//                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds));
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.pin_dc)));
                if (lastClickedMarker != null && lastClickedMarker.getPosition().equals(marker.getPosition()) && lastClickedMarker.isInfoWindowShown()) {
                    //      marker.showInfoWindow();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showingMarkers(List<DiveCenter> diveCenters) {
        for (DiveCenter diveCenter : diveCenters) {
            addItem(diveCenter);
            diveCentersMap.put(diveCenter.getPosition(), diveCenter);
        }
        cluster();
    }

    private ArrayList<DiveCenter> changeListToListFragment(ArrayList<DiveCenter> oldList) {
        ArrayList<DiveCenter> newList = new ArrayList<>();
        for (DiveCenter diveCenter : oldList) {
            if (isSpotVisibleOnScreen(Float.valueOf(diveCenter.getLat()), Float.valueOf(diveCenter.getLng()))) {
                Log.i(TAG, "DiveSpotInVisibleRegion");
                newList.add(diveCenter);
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

    public void requestDiveCenters(LatLng latLng) {

        Map<String, String> map = new HashMap<>();
        map.put("latLeft", String.valueOf(latLng.latitude - 2.0));
        map.put("lngLeft", String.valueOf(latLng.longitude - 2.0));
        map.put("lngRight", String.valueOf(latLng.longitude + 2.0));
        map.put("latRight", String.valueOf(latLng.latitude + 2.0));
        Call<ResponseBody> call = RestClient.getServiceInstance().getDiveCenters(map);
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
                    System.out.println(responseString);
                    diveCentersResponseEntity = new Gson().fromJson(responseString, DiveCentersResponseEntity.class);
                    logoPath = diveCentersResponseEntity.getLogoPath();
                    diveCenters = diveCentersResponseEntity.getDivecenters();
                    DDScannerApplication.bus.post(new PutDiveCentersToListEvent(changeListToListFragment((ArrayList<DiveCenter>)diveCenters), logoPath));
                 //   diveCentersPagerAdapter.populateDiveCentersList(changeListToListFragment((ArrayList<DiveCenter>) diveCenters), logoPath);
                    showingMarkers(diveCenters);
                } else {
                    // TODO Handle errors
//                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
//                        //   Toast.makeText(DiveCentersActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
//                    } else if (error.getKind().equals(RetrofitError.Kind.HTTP)) {
//                        //   Toast.makeText(DiveCentersActivity.this, "Server is not responsible, please try later", Toast.LENGTH_LONG).show();
//                    }
//                    if (error != null) {
//                        String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
//                        Log.i(TAG, json);
//                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // TODO Handle errors
            }
        });
    }

    public void setUserCurrentLocationMarker(Marker userCurrentLocationMarker) {
        this.userCurrentLocationMarker = userCurrentLocationMarker;
    }

}
