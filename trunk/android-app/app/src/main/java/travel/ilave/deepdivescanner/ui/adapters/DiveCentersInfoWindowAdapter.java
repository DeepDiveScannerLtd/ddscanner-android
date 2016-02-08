package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
    private HashMap<Marker, String> markersMap = new HashMap<>();

    public DiveCentersInfoWindowAdapter(Context context, ArrayList<DiveCenter> diveCenters, GoogleMap googleMap, LatLng diveSiteCoordinates) {
        this.context = context;
        this.googleMap = googleMap;
        this.diveCenters = diveCenters;
        this.diveSiteCoordinates = diveSiteCoordinates;
        for(DiveCenter diveCenter : diveCenters) {
            LatLng latLng = new LatLng(Double.valueOf(diveCenter.getLat()), Double.valueOf(diveCenter.getLng()));
            Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_dc)));
            markersMap.put(marker, "false");
        }
        Marker diveSpotMarker = googleMap.addMarker(new MarkerOptions().position(diveSiteCoordinates).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pin)));
        markersMap.put(diveSpotMarker, "true");
    }

    @Override
    public View getInfoWindow(Marker marker) {
        LayoutInflater  inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.info_window_divecenter, null);
        return view;

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (markersMap.get(marker).equals("false")) {
            marker.showInfoWindow();
        }
        return true;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


}
