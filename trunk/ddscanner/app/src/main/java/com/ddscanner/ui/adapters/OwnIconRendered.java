package com.ddscanner.ui.adapters;

import android.content.Context;

import com.ddscanner.entities.ClusterItemImplementation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by lashket on 17.2.16.
 */
public class OwnIconRendered extends DefaultClusterRenderer<ClusterItemImplementation> {

    public OwnIconRendered(Context context, GoogleMap map,
                           ClusterManager<ClusterItemImplementation> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onClusterItemRendered(ClusterItemImplementation clusterItem, final Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        try {
//                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_dc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}