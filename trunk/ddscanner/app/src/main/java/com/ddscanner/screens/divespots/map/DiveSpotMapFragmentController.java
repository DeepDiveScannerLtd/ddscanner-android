package com.ddscanner.screens.divespots.map;


import com.ddscanner.entities.BaseMapEntity;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public interface DiveSpotMapFragmentController {

    void showDiveSpotInfo(Marker marker, BaseMapEntity diveSpotShort);

    void hideDiveSpotInfo();

    void showDiveCenterInfo(Marker marker, BaseMapEntity diveCenter);

    void hideDiveCenternfo();

    void showProgressView();

    void hideProgressView();

    void showZoomInMessage();

    void hideZoomInMessage();

    void requestDiveSpots(ArrayList<String> sealifes, DiveSpotsRequestMap diveSpotsRequestMap);

}
