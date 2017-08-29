package com.ddscanner.screens.map;


import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public interface FragmentMapContract {

    void loadDiveSpots(ArrayList<String> sealifesIds, DiveSpotsRequestMap diveSpotsRequestMap);
    void showProgressBar();
    void hideProgressBar();
    void showDiveSpotInfo(Marker marker, DiveSpotShort diveSpotShort);
    void hideDiveSpotInfo();
    void showMessageToZoom();
    void hideMessageToZoom();
    void showTutorialForMapListFab();

}
