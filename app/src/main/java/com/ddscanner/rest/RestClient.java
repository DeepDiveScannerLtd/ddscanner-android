package com.ddscanner.rest;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public abstract class RestClient {

    private static DDScannerRestService serviceInstance;

    public static DDScannerRestService getServiceInstance() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                request.addHeader("Accept","application/vnd.trizeri.v1+json");
                request.addHeader("Content-Type", "application/json;charset=utf-8");
            }
        };
        if (serviceInstance == null) {
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://api.trizeri.com").setRequestInterceptor(requestInterceptor)
                    .build();
            serviceInstance = restAdapter.create(DDScannerRestService.class);
        }
        return serviceInstance;
    }


}
