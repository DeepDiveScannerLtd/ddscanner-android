package com.ddscanner.rest;


import java.io.IOException;
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

    public static DDScannerRestService getDdscannerServiceInstance() {
        if (ddscannerServiceInstance == null) {
            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    request = request.newBuilder()
                            .addHeader("Accept", "application/vnd.trizeri.v1+json") // dev
                            .addHeader("Content-Type", "application/json;charset=utf-8")
                            .build();
                    Response response = chain.proceed(request);
                    return response;
                }
            };

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().add(interceptor);
            builder.readTimeout(10, TimeUnit.MINUTES);
            builder.writeTimeout(10, TimeUnit.MINUTES);
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            builder.interceptors().add(logging);

            OkHttpClient client = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl("http://api.ddscanner.com")
                    .baseUrl("https://ddsapi.ilave.pro") // dev
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
}
