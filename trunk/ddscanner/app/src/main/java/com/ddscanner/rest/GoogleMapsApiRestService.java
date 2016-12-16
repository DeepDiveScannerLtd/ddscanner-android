package com.ddscanner.rest;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface GoogleMapsApiRestService {

    @GET("maps/api/geocode/json")
    Call<ResponseBody> getCountryName(@Query("latlng") String latlng);

}
