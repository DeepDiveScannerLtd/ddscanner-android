package travel.ilave.deepdivescanner.ui.managers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.DDScannerApplication;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.ClusterItemImplementation;
import travel.ilave.deepdivescanner.entities.DiveCenter;
import travel.ilave.deepdivescanner.entities.DiveSpot;
import travel.ilave.deepdivescanner.entities.DivespotsWrapper;
import travel.ilave.deepdivescanner.entities.request.DiveSpotsRequestMap;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.ui.activities.DivePlaceActivity;
import travel.ilave.deepdivescanner.ui.adapters.PlacesPagerAdapter;
import travel.ilave.deepdivescanner.utils.LogUtils;

public class DiveCentersClusterManager extends ClusterManager<DiveCenter> implements ClusterManager.OnClusterClickListener<DiveCenter> {

    private static final String TAG = DiveCentersClusterManager.class.getName();
    private static final int CAMERA_ANIMATION_DURATION = 300;

    private Context context;
    private GoogleMap googleMap;
    private Marker lastClickedMarker;
    private HashMap<LatLng, DiveCenter> diveCentersMap = new HashMap<>();
    private Drawable clusterBackgroundDrawable;
    private final IconGenerator clusterIconGenerator;
    private String logoPath;

    public DiveCentersClusterManager(Context context, GoogleMap googleMap, List<DiveCenter> diveCenters, LatLng diveSpotLatLng, String diveSpotName, String logoPath) {
        super(context, googleMap);
        this.context = context;
        this.googleMap = googleMap;
        this.logoPath = logoPath;
        this.clusterBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.ic_number);
        this.clusterIconGenerator = new IconGenerator(context);
        this.clusterIconGenerator.setContentView(this.makeSquareTextView(context));
        this.clusterIconGenerator.setTextAppearance(com.google.maps.android.R.style.ClusterIcon_TextAppearance);
        this.clusterIconGenerator.setBackground(clusterBackgroundDrawable);

        setAlgorithm(new GridBasedAlgorithm<DiveCenter>());
        setRenderer(new IconRenderer(context, googleMap, this));
        setOnClusterClickListener(this);
        getMarkerCollection().setOnInfoWindowAdapter(new InfoWindowAdapter());

        for (DiveCenter diveCenter : diveCenters) {
            addItem(diveCenter);
            diveCentersMap.put(diveCenter.getPosition(), diveCenter);
        }
        googleMap.addMarker(new MarkerOptions().position(diveSpotLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_ds)).title(diveSpotName));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (super.onMarkerClick(marker)) {
            return true;
        }
        marker.showInfoWindow();
        lastClickedMarker = marker;
        Projection projection = googleMap.getProjection();
        Point mapCenteringPoint = projection.toScreenLocation(marker.getPosition());
        mapCenteringPoint.y = mapCenteringPoint.y - DDScannerApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.info_window_height) / 2;
        LatLng newMarkerPosition = new LatLng(projection.fromScreenLocation(mapCenteringPoint).latitude, marker.getPosition().longitude);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(newMarkerPosition), CAMERA_ANIMATION_DURATION, null);
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

    private class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        public static final String PRODUCT = "PRODUCT";

        private boolean not_first_time_showing_info_window;

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
            TextView dc_telephone = (TextView) view.findViewById(R.id.iw_dc_telefon);
            LinearLayout stars = (LinearLayout) view.findViewById(R.id.stars);
            DiveCenter dc = diveCentersMap.get(marker.getPosition());
            if (dc.getLogo() != null) {
                String imageUrlPath = logoPath + dc.getLogo();
                if (not_first_time_showing_info_window) {
                    Picasso.with(context).load(imageUrlPath).into(logo);
                } else {
                    not_first_time_showing_info_window = true;
                    Picasso.with(context).load(imageUrlPath).into(logo, new InfoWindowRefresher(marker));
                }

                // diveCentersListViewHolder.imgLogo.setImageURI(Uri.parse(imageUrl));
            }
            dc_name.setText(dc.getName());
            if (dc.getAddress() != null) {
                dc_address.setText(dc.getAddress());
                dc_address.setVisibility(View.VISIBLE);
            }
            if (dc.getPhone() != null) {
                dc_telephone.setText(dc.getPhone());
                view.findViewById(R.id.phone_number).setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < dc.getRating(); i++) {
                ImageView iv = new ImageView(context);
                iv.setImageResource(R.drawable.ic_flag_full_small);
                iv.setPadding(5, 0, 0, 0);
                stars.addView(iv);
            }
            for (int i = 0; i < 5 - dc.getRating(); i++) {
                ImageView iv = new ImageView(context);
                iv.setImageResource(R.drawable.ic_flag_empty_small);
                iv.setPadding(5, 0, 0, 0);
                stars.addView(iv);
            }

            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        private class InfoWindowRefresher implements Callback {
            private Marker markerToRefresh;

            private InfoWindowRefresher(Marker markerToRefresh) {
                this.markerToRefresh = markerToRefresh;
            }

            @Override
            public void onSuccess() {
                markerToRefresh.showInfoWindow();
            }

            @Override
            public void onError() {
            }
        }
    }
}
