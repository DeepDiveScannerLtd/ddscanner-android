package com.ddscanner.screens.divecenters.map;


import com.ddscanner.entities.DiveCenter;

public interface DiveCentersMapContract {
    void showInfoWindow(DiveCenter diveCenter);
    void hideInfoWindow();
}
