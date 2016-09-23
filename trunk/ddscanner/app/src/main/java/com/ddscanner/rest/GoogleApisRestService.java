package com.ddscanner.rest;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApisRestService {

    @GET("/oauth2/v3/tokeninfo")
    Call<ResponseBody> getTokenInfo(@Query("id_token") String idToken);

}
