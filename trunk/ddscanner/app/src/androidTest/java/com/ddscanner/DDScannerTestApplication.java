package com.ddscanner;


import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.utils.SharedPreferenceHelper;

public class DDScannerTestApplication extends DDScannerApplication {
    private DDScannerRestClient ddScannerTestRestClient;
    private SharedPreferenceHelper sharedPreferenceHelper;

    public void setDdScannerTestRestClient(DDScannerRestClient ddScannerTestRestClient) {
        this.ddScannerTestRestClient = ddScannerTestRestClient;
    }

    public void setSharedPreferenceHelper(SharedPreferenceHelper sharedPreferenceHelper) {
        this.sharedPreferenceHelper = sharedPreferenceHelper;
    }

    @Override
    public SharedPreferenceHelper getSharedPreferenceHelper() {
        return sharedPreferenceHelper;
    }
}
