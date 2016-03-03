package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.ClusterItemImplementation;
import travel.ilave.deepdivescanner.entities.DiveCenter;

/**
 * Created by lashket on 5.2.16.
 */
public class DiveCentersInfoWindowAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnMarkerClickListener {

    private Context context;
    private GoogleMap googleMap;
    private ArrayList<DiveCenter> diveCenters;
    private LatLng diveSiteCoordinates;
    private HashMap<LatLng, DiveCenter> markersMap = new HashMap<>();
    private ClusterManager<ClusterItemImplementation> mClusterManager;
    private String logoPath;
    private Marker diveSpotMarker;
    private boolean not_first_time_showing_info_window = false;
    private LatLng diveSpotLatLng;
    private String dsName;


    public DiveCentersInfoWindowAdapter(Context context, ArrayList<DiveCenter> diveCenters, GoogleMap googleMap, LatLng diveSiteCoordinates, String logoPath, String dsName) {
        this.context = context;
        this.logoPath = logoPath;
        this.googleMap = googleMap;
        this.diveCenters = diveCenters;
        this.dsName = dsName;
        this.diveSiteCoordinates = diveSiteCoordinates;
        mClusterManager = new ClusterManager<>(context, googleMap);
        mClusterManager.setRenderer(new OwnIconRendered(context, googleMap, mClusterManager));
        googleMap.setOnCameraChangeListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        for (DiveCenter diveCenter : diveCenters) {
            LatLng latLng = new LatLng(Double.valueOf(diveCenter.getLat()), Double.valueOf(diveCenter.getLng()));
            // Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_dc)));
            // markersMap.put(marker, "false");
            ClusterItemImplementation offsetItem = new ClusterItemImplementation(latLng.latitude, latLng.longitude);
            markersMap.put(latLng, diveCenter);
            mClusterManager.addItem(offsetItem);


        }
        diveSpotMarker = googleMap.addMarker(new MarkerOptions().position(diveSiteCoordinates).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_ds)).title(dsName));
        diveSpotLatLng = diveSpotMarker.getPosition();

    }

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
        DiveCenter dc = markersMap.get(marker.getPosition());
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
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle() != null && marker.getTitle().equals("DS")) {
            return false;
        }
        marker.showInfoWindow();

        return true;
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

