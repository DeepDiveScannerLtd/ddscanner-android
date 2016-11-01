package com.ddscanner.rest;


import android.support.annotation.NonNull;

import com.ddscanner.entities.Sealife;

public interface DDScannerRestClientContract {
    void getSeaLifeDetails(String seaLifeId, @NonNull final DDScannerRestClient.ResultListener<Sealife> resultListener);
}
