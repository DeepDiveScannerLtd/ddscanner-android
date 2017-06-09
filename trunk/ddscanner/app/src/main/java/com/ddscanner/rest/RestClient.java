package com.ddscanner.rest;


import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;

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
    private static GoogleApisRestService googleApisServiceInstance;
    private static GoogleMapsApiRestService googleMapsApiServiceInstance;

    public static DDScannerRestService getDdscannerServiceInstance() {
        if (ddscannerServiceInstance == null) {
            Interceptor interceptor = chain -> {
                if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                    Request request = chain.request();
                    request = request.newBuilder()
                           // .addHeader("Accept", "application/vnd.trizeri.v1+json") // dev
                            //   .addHeader("Content-Type", "application/json;charset=utf-8")
                            .addHeader("Accept-Language", Locale.getDefault().getLanguage())
                            .addHeader("Authorization", "Bearer " + DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserToken())
                            .build();
                    Response response = chain.proceed(request);
                    return response;
                }
                Request request = chain.request();
                request = request.newBuilder()
//                            .addHeader("Accept", "application/vnd.trizeri.v1+json") // dev
                        //   .addHeader("Content-Type", "application/json;charset=utf-8")
                        .addHeader("Accept-Language", Locale.getDefault().getLanguage())
                        .build();
                Response response = chain.proceed(request);
                return response;
            };

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().add(interceptor);
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

    public static GoogleApisRestService getGoogleApisServiceInstance() {
        if (googleApisServiceInstance == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.readTimeout(10, TimeUnit.SECONDS);
            builder.writeTimeout(10, TimeUnit.SECONDS);
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            builder.interceptors().add(logging);

            OkHttpClient client = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.googleapis.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            googleApisServiceInstance = retrofit.create(GoogleApisRestService.class);
        }
        return googleApisServiceInstance;
    }

    public static GoogleMapsApiRestService getGoogleMapsApiService() {
        if (googleMapsApiServiceInstance == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            builder.interceptors().add(logging);

            OkHttpClient client = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://maps.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            googleMapsApiServiceInstance = retrofit.create(GoogleMapsApiRestService.class);
        }
        return googleMapsApiServiceInstance;
    }

}
