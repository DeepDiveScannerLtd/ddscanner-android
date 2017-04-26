package com.ddscanner.rest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsApiRestService {

    @GET("maps/api/geocode/json")
    Call<ResponseBody> getCountryName(@Query("latlng") String latlng);

}
