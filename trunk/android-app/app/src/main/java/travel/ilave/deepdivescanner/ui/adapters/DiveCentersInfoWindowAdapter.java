package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import travel.ilave.deepdivescanner.entities.DiveSpot;

/**
 * Created by lashket on 5.2.16.
 */
public class DiveCentersInfoWindowAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnMarkerClickListener {

    public DiveCentersInfoWindowAdapter(Context context, ArrayList<DiveSpot> diveSpots, GoogleMap googleMap) {

    }

    @Override
    public View getInfoWindow(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        // onProductSelectedListener.onProductSelected(markersMap.get(marker));
        return true;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


}
