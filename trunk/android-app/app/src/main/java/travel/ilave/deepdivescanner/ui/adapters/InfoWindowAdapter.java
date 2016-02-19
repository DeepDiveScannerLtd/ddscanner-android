package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.DiveSpot;
import travel.ilave.deepdivescanner.ui.activities.DivePlaceActivity;

/**
 * Created by lashket on 10.12.15.
 */
public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    public static final String PRODUCT = "PRODUCT";

    private View view;
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<DiveSpot> divespots;
    private TextView title;
    private TextView from;
    private DiveSpot diveSpot;
    private GoogleMap googleMap;
    private HashMap<LatLng, DiveSpot> markersMap = new HashMap<>();
    private ImageView photo;
    private TextView description;
    private boolean not_first_time_showing_info_window;

    public InfoWindowAdapter(Context context, ArrayList<DiveSpot> diveSpots, GoogleMap map) {
        this.divespots = diveSpots;
        this.mContext = context;
        this.googleMap = map;
        googleMap.setOnInfoWindowClickListener(this);
        if (divespots != null) {
            for (DiveSpot divespot : divespots) {
                LatLng place = new LatLng(Double.valueOf(divespot.getLat()), Double.valueOf(divespot.getLng()));
                Marker marker = googleMap.addMarker(new MarkerOptions().position(place).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_ds)));
                markersMap.put(place, divespot);
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.info_window, null);
        diveSpot = new DiveSpot();
        diveSpot = markersMap.get(marker.getPosition());
        photo = (ImageView) view.findViewById(R.id.popup_photo);
        if (diveSpot.getImages() != null) {
            System.out.println(diveSpot.getImages().get(0));
            if (not_first_time_showing_info_window) {
                Picasso.with(mContext).load(diveSpot.getImages().get(0)).resize(260, 195).into(photo);
            } else {
                not_first_time_showing_info_window = true;
                Picasso.with(mContext).load(diveSpot.getImages().get(0)).resize(260, 195).into(photo, new InfoWindowRefresher(marker));
            }
        }
        description = ((TextView) view.findViewById(R.id.description_popup));
        title = ((TextView)view.findViewById(R.id.popup_product_name));
        title.setText(diveSpot.getName());
        description.setText(diveSpot.getDescription());
        // from = ((TextView)view.findViewById(R.id.price1));
        LinearLayout stars = (LinearLayout) view.findViewById(R.id.stars);
        stars.removeAllViews();
        for (int i = 0; i < diveSpot.getRating(); i++) {
            ImageView iv = new ImageView(mContext);
            iv.setImageResource(R.drawable.ic_flag_full_small);
            iv.setPadding(5,0,0,0);
            stars.addView(iv);
        }
        for (int i = 0; i < 5 - diveSpot.getRating(); i++) {
            ImageView iv = new ImageView(mContext);
            iv.setImageResource(R.drawable.ic_flag_empty_small);
            iv.setPadding(5,0,0,0);
            stars.addView(iv);
        }
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
      /*  final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                marker.showInfoWindow();
            }
        }, 200);*/
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent i = new Intent(mContext, DivePlaceActivity.class);
        i.putExtra(PRODUCT, markersMap.get(marker.getPosition()).getId());
        mContext.startActivity(i);
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
        public void onError() {}
    }

}
