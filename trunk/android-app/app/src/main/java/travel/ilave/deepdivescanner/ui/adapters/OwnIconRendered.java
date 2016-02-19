package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import travel.ilave.deepdivescanner.R;

/**
 * Created by lashket on 17.2.16.
 */
public class OwnIconRendered extends DefaultClusterRenderer<MyItem> {

    public OwnIconRendered(Context context, GoogleMap map,
                           ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onClusterItemRendered(MyItem clusterItem, final Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        try {
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_dc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}