package com.ddscanner.rest;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class RestClient {

    private static DDScannerRestService serviceInstance;

    public static DDScannerRestService getServiceInstance() {
        if (serviceInstance == null) {
            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    request = request.newBuilder()
                            .addHeader("Accept", "application/vnd.trizeri.v1+json")
                            .addHeader("Content-Type", "application/json;charset=utf-8")
                            .build();
                    Response response = chain.proceed(request);
                    return response;
                }
            };

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().add(interceptor);
            OkHttpClient client = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.trizeri.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            serviceInstance = retrofit.create(DDScannerRestService.class);
        }
        return serviceInstance;
    }


}
