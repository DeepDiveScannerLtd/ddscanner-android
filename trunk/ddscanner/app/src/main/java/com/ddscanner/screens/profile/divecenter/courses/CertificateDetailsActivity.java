package com.ddscanner.screens.profile.divecenter.courses;


import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ddscanner.entities.Certificate;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;

public class CertificateDetailsActivity extends BaseAppCompatActivity {

    DDScannerRestClient.ResultListener<Certificate> resultListener = new DDScannerRestClient.ResultListener<Certificate>() {
        @Override
        public void onSuccess(Certificate result) {

        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }

        @Override
        public void onInternetConnectionClosed() {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
