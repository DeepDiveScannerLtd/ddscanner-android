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
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import travel.ilave.deepdivescanner.R;
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
    private ClusterManager<MyItem> mClusterManager;
    private String logoPath;

    public DiveCentersInfoWindowAdapter(Context context, ArrayList<DiveCenter> diveCenters, GoogleMap googleMap, LatLng diveSiteCoordinates, String logoPath) {
        this.context = context;
        this.logoPath = logoPath;
        this.googleMap = googleMap;
        this.diveCenters = diveCenters;
        this.diveSiteCoordinates = diveSiteCoordinates;
        mClusterManager = new ClusterManager<MyItem>(context, googleMap);
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem myItem) {
                return false;
            }
        });
        googleMap.setOnCameraChangeListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        for(DiveCenter diveCenter : diveCenters) {
            LatLng latLng = new LatLng(Double.valueOf(diveCenter.getLat()), Double.valueOf(diveCenter.getLng()));
           // Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_dc)));
           // markersMap.put(marker, "false");
            MyItem offsetItem = new MyItem(latLng.latitude, latLng.longitude);
            markersMap.put(latLng, diveCenter);
            mClusterManager.addItem(offsetItem);


        }
        Marker diveSpotMarker = googleMap.addMarker(new MarkerOptions().position(diveSiteCoordinates).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pin)));


    }

    @Override
    public View getInfoWindow(Marker marker) {
        LayoutInflater  inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.info_window_divecenter, null);
        TextView dc_name = (TextView) view.findViewById(R.id.iw_dc_name);
        ImageView logo = (ImageView) view.findViewById(R.id.iw_dc_avatar);
        TextView dc_address = (TextView) view.findViewById(R.id.iw_dc_address);
        TextView dc_telephone = (TextView) view.findViewById(R.id.iw_dc_telefon);
        LinearLayout stars = (LinearLayout) view.findViewById(R.id.stars);
        DiveCenter dc = markersMap.get(marker.getPosition());
        if(dc.getLogo() != null) {
            String imageUrlPath = logoPath + dc.getLogo();
            Picasso.with(context).load(imageUrlPath).into(logo);
            // diveCentersListViewHolder.imgLogo.setImageURI(Uri.parse(imageUrl));
        }
        dc_name.setText(dc.getName());
        dc_address.setText(dc.getAddress());
        dc_telephone.setText(dc.getPhone());
        for (int i = 0; i < dc.getRating(); i++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_flag_full_small);
            iv.setPadding(5,0,0,0);
            stars.addView(iv);
        }
        for (int i = 0; i < 5 - dc.getRating(); i++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_flag_empty_small);
            iv.setPadding(5,0,0,0);
            stars.addView(iv);
        }

        return view;

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


}

