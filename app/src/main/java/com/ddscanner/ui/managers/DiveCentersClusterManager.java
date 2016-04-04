package com.ddscanner.ui.managers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.entities.DiveCentersResponseEntity;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.activities.DiveCenterDetailsActivity;
import com.ddscanner.ui.activities.DivePlaceActivity;
import com.ddscanner.ui.adapters.DiveCentersPagerAdapter;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DiveCentersClusterManager extends ClusterManager<DiveCenter> implements ClusterManager.OnClusterClickListener<DiveCenter> {

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
    private InfoWindowRefresher infoWindowRefresher;
    private List<DiveCenter> diveCenters = new ArrayList<>();
    private String logoPath;
    private DiveCentersResponseEntity diveCentersResponseEntity;
    private DiveCentersPagerAdapter diveCentersPagerAdapter;

    public DiveCentersClusterManager(Context context, GoogleMap googleMap, LatLng diveSpotLatLng, String diveSpotName, DiveCentersPagerAdapter diveCentersPagerAdapter) {
        super(context, googleMap);
        this.context = context;
        this.googleMap = googleMap;
        this.clusterBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.ic_number);
        this.diveCentersPagerAdapter = diveCentersPagerAdapter;
        this.clusterIconGenerator = new IconGenerator(context);
        this.clusterIconGenerator.setContentView(this.makeSquareTextView(context));
        this.clusterIconGenerator.setTextAppearance(com.google.maps.android.R.style.ClusterIcon_TextAppearance);
        this.clusterIconGenerator.setBackground(clusterBackgroundDrawable);

        setAlgorithm(new GridBasedAlgorithm<DiveCenter>());
        setRenderer(new IconRenderer(context, googleMap, this));
        setOnClusterClickListener(this);
        getMarkerCollection().setOnInfoWindowAdapter(new InfoWindowAdapter());
        requestDiveCenters(diveSpotLatLng);

        for (DiveCenter diveCenter : diveCenters) {
            addItem(diveCenter);
            diveCentersMap.put(diveCenter.getPosition(), diveCenter);
        }
        diveSpotMarker = googleMap.addMarker(new MarkerOptions().position(diveSpotLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ds)).title(diveSpotName));
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
        diveCentersPagerAdapter.populateDiveCentersList(changeListToListFragment((ArrayList<DiveCenter>)diveCenters), logoPath);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (super.onMarkerClick(marker)) {
            return true;
        }
        if (marker.getTitle() == null) {
            AppsFlyerLib.getInstance().trackEvent(context, EventTrackerHelper
                    .EVENT_MARKER_CLICK, new HashMap<String, Object>() {{
                put(EventTrackerHelper.PARAM_MARKER_CLICK_TYPE, "dive_center");
                put(EventTrackerHelper.PARAM_MARKER_CLICK_PLACE_ID, String.valueOf(diveCentersMap.get(marker.getPosition()).getId()));
            }});
        }
        marker.showInfoWindow();
        lastClickedMarker = marker;
        Projection projection = googleMap.getProjection();
        Point mapCenteringPoint = projection.toScreenLocation(marker.getPosition());
        mapCenteringPoint.y = mapCenteringPoint.y - DDScannerApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.info_window_height) / 2;
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(projection.fromScreenLocation(mapCenteringPoint)), CAMERA_ANIMATION_DURATION, null);
        return true;
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
            int bucket = this.getBucket(cluster);
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(clusterIconGenerator.makeIcon(this.getClusterText(bucket)));

            markerOptions.icon(descriptor);
        }

        @Override
        protected void onClusterItemRendered(DiveCenter diveCenter, final Marker marker) {
            super.onClusterItemRendered(diveCenter, marker);
            try {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dc));
                if (lastClickedMarker != null && lastClickedMarker.getPosition().equals(marker.getPosition()) && lastClickedMarker.isInfoWindowShown()) {
                    marker.showInfoWindow();
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
        RestClient.getServiceInstance().getDiveCenters(map, new retrofit.Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                System.out.println(responseString);
                diveCentersResponseEntity = new Gson().fromJson(responseString, DiveCentersResponseEntity.class);
                logoPath = diveCentersResponseEntity.getLogoPath();
                diveCenters = diveCentersResponseEntity.getDivecenters();
                diveCentersPagerAdapter.populateDiveCentersList(changeListToListFragment((ArrayList<DiveCenter>) diveCenters), logoPath);
                showingMarkers(diveCenters);
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                 //   Toast.makeText(DiveCentersActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error.getKind().equals(RetrofitError.Kind.HTTP)) {
                 //   Toast.makeText(DiveCentersActivity.this, "Server is not responsible, please try later", Toast.LENGTH_LONG).show();
                }
                if (error != null) {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                    Log.i(TAG, json);
                }
            }
        });
    }

    private class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            if (marker.getTitle() != null) {
                return null;
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.info_window_divecenter, null);
            TextView dc_name = (TextView) view.findViewById(R.id.iw_dc_name);
            ImageView logo = (ImageView) view.findViewById(R.id.iw_dc_avatar);
            TextView dc_address = (TextView) view.findViewById(R.id.iw_dc_address);
            LinearLayout stars = (LinearLayout) view.findViewById(R.id.stars);
            DiveCenter dc = diveCentersMap.get(marker.getPosition());
            Bitmap bitmap = markerBitmapCache.get(marker);
            if (dc.getLogo() != null) {
                if (bitmap == null) {
                    infoWindowRefresher = new InfoWindowRefresher(marker);
                    LogUtils.i(TAG, "getInfoWindow image=" + dc.getLogo());
                    Picasso.with(context).load(logoPath + dc.getLogo()).resize(60, 60).into(infoWindowRefresher);
                } else {
                    ImageView photo = (ImageView) view.findViewById(R.id.iw_dc_avatar);
                    photo.setAlpha(1f);
                    photo.setImageBitmap(bitmap);
                }
            }
            dc_name.setText(dc.getName());
            if (dc.getAddress() != null) {
                dc_address.setText(dc.getAddress());
                dc_address.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < dc.getRating(); i++) {
                ImageView iv = new ImageView(context);
                iv.setImageResource(R.drawable.ic_flag_full_small);
                iv.setPadding(2, 0, 0, 0);
                stars.addView(iv);
            }
            for (int i = 0; i < 5 - dc.getRating(); i++) {
                ImageView iv = new ImageView(context);
                iv.setImageResource(R.drawable.ic_flag_empty_small);
                iv.setPadding(2, 0, 0, 0);
                stars.addView(iv);
            }

            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

    }
    private class InfoWindowRefresher implements Target {
        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            LogUtils.i(TAG, "InfoWindowRefresher onSuccess markerToRefresh=" + markerToRefresh + " markerToRefresh.isInfoWindowShown()=" + markerToRefresh.isInfoWindowShown());
            if (markerToRefresh != null && markerToRefresh.isInfoWindowShown()) {
                markerBitmapCache.put(markerToRefresh, bitmap);
//                markerToRefresh.hideInfoWindow();
                markerToRefresh.showInfoWindow();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            LogUtils.i(getClass().getSimpleName(), "Error loading thumbnail!");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
}
