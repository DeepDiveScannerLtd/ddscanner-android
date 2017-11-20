package com.ddscanner.screens.divespots.map;


import com.ddscanner.entities.DiveCenter;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public interface DiveSpotMapFragmentController {

    void showDiveSpotInfo(Marker marker, DiveSpotShort diveSpotShort);

    void hideDiveSpotInfo();

    void showDiveCenterInfo(Marker marker, DiveCenter diveCenter);

    void hideDiveCenternfo();

    void showProgressView();

    void hideProgressView();

    void showZoomInMessage();

    void hideZoomInMessage();

    void requestDiveSpots(ArrayList<String> sealifes, DiveSpotsRequestMap diveSpotsRequestMap);

}
