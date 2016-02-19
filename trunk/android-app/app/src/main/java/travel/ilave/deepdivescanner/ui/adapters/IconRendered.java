package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by lashket on 12.2.16.
 */
public class IconRendered  extends DefaultClusterRenderer<MyItem> {

    public IconRendered(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
      //  markerOptions.icon(item.getIcon)
    }
}
