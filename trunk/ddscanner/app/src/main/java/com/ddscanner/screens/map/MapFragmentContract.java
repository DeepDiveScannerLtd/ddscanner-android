package com.ddscanner.screens.map;


import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public interface MapFragmentContract {

    interface View {
        void loadData(DiveSpotsRequestMap diveSpotsRequestMap, ArrayList<String> sealifes);
        void markerClicked(Marker marker, DiveSpotShort diveSpotShort);
        void hideDiveSpotInfo();
        void showErrorMessage();
        void hideErrorMessage();
    }

}
