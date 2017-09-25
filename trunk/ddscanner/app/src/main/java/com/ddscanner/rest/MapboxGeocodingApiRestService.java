package com.ddscanner.rest;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MapboxGeocodingApiRestService {

    @GET("geocoding/v5/mapbox.places/{query}.json")
    Call<ResponseBody> getPlacesForQuesry(@Path("query") String query, @Query("access_token") String token, @Query("language") String language);


}
