package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.DiveSpot;
import travel.ilave.deepdivescanner.ui.activities.DivePlaceActivity;

/**
 * Created by lashket on 10.12.15.
 */
public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {
    public static final String PRODUCT = "PRODUCT";

    private View view;
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<DiveSpot> divespots;
    private TextView title;
    private TextView from;
    private DiveSpot divespot;
    private GoogleMap googleMap;
    private HashMap<Marker, DiveSpot> markersMap = new HashMap<>();
    private TextView description;

    public InfoWindowAdapter(Context context, ArrayList<DiveSpot> prdct, GoogleMap map) {
        divespots = prdct;
        mContext = context;
        googleMap = map;
        googleMap.setOnInfoWindowClickListener(this);
        if (divespots != null) {
            for (DiveSpot divespot : divespots) {
                LatLng place = new LatLng(Double.valueOf(divespot.getLat()), Double.valueOf(divespot.getLng()));
                Marker marker = googleMap.addMarker(new MarkerOptions().position(place).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pin)));
                markersMap.put(marker, divespot);
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        divespot = markersMap.get(marker);
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.info_window, null);
        description = ((TextView) view.findViewById(R.id.description_popup));
        title = ((TextView)view.findViewById(R.id.popup_product_name));
       // from = ((TextView)view.findViewById(R.id.price1));
        LinearLayout stars = (LinearLayout) view.findViewById(R.id.stars);
        /*title.setText(divespot.getName());
        description.setText(divespot.getDescription());
        for (int i = 0; i < divespot.getRating(); i++) {
            ImageView iv = new ImageView(mContext);
            iv.setImageResource(R.drawable.ic_flag_full_small);
            iv.setPadding(5,0,0,0);
            stars.addView(iv);
        }
        for (int i = 0; i < 5 - divespot.getRating(); i++) {
            ImageView iv = new ImageView(mContext);
            iv.setImageResource(R.drawable.ic_flag_empty_small);
            iv.setPadding(5,0,0,0);
            stars.addView(iv);
        }
        String price = String.valueOf("15");
        from.setText(price);*/
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent i = new Intent(mContext, DivePlaceActivity.class);
        i.putExtra(PRODUCT, markersMap.get(marker));
        mContext.startActivity(i);
    }

}
