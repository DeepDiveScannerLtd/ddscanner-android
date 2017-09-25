package com.ddscanner.rest;


import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.utils.SharedPreferenceHelper;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class RestClient {

    private static DDScannerRestService ddscannerServiceInstance;
    private static MapboxGeocodingApiRestService mapboxGeocodingApiRestService;
    private static GoogleMapsApiRestService googleMapsApiServiceInstance;

    public static MapboxGeocodingApiRestService getMapboxGeocodingApiRestService() {
        if (mapboxGeocodingApiRestService == null) {
            Interceptor interceptor = chain -> {
                Request request = chain.request();
                request = request.newBuilder().build();
                Response response = chain.proceed(request);
                return response;
            };

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().add(interceptor);
            builder.retryOnConnectionFailure(true);
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(1, TimeUnit.MINUTES);
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            builder.interceptors().add(logging);

            OkHttpClient client = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(DDScannerApplication.getInstance().getString(R.string.mapbox_server_api_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            mapboxGeocodingApiRestService = retrofit.create(MapboxGeocodingApiRestService.class);
        }
        return mapboxGeocodingApiRestService;
    }

    public static DDScannerRestService getDdscannerServiceInstance() {
        if (ddscannerServiceInstance == null) {
            Interceptor interceptor = chain -> {
                if (SharedPreferenceHelper.getIsUserSignedIn()) {
                    Request request = chain.request();
                    request = request.newBuilder()
                           // .addHeader("Accept", "application/vnd.trizeri.v1+json") // dev
                            //   .addHeader("Content-Type", "application/json;charset=utf-8")
//                            .addHeader("Accept-Language", Locale.getDefault().getLanguage())
                            .addHeader("Authorization", "Bearer " + SharedPreferenceHelper.getActiveUserToken())
                            .build();
                    Response response = chain.proceed(request);
                    return response;
                }
                Request request = chain.request();
                request = request.newBuilder()
//                            .addHeader("Accept", "application/vnd.trizeri.v1+json") // dev
                        //   .addHeader("Content-Type", "application/json;charset=utf-8")
//                        .addHeader("Accept-Language", Locale.getDefault().getLanguage())
                        .build();
                Response response = chain.proceed(request);
                return response;
            };

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().add(interceptor);
            builder.retryOnConnectionFailure(true);
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(1, TimeUnit.MINUTES);
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            builder.interceptors().add(logging);

            OkHttpClient client = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(DDScannerApplication.getInstance().getString(R.string.server_api_address))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            ddscannerServiceInstance = retrofit.create(DDScannerRestService.class);
        }
        return ddscannerServiceInstance;
    }


}
