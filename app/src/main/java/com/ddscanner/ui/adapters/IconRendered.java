package com.ddscanner.ui.adapters;

import android.content.Context;

import com.ddscanner.entities.ClusterItemImplementation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by lashket on 12.2.16.
 */
public class IconRendered  extends DefaultClusterRenderer<ClusterItemImplementation> {

    public IconRendered(Context context, GoogleMap map, ClusterManager<ClusterItemImplementation> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterItemImplementation item, MarkerOptions markerOptions) {
      //  markerOptions.icon(item.getIcon)
    }

    

}
