package travel.ilave.deepdivescanner.entities;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterItemImplementation implements ClusterItem {
    private final LatLng mPosition;

    public ClusterItemImplementation(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}