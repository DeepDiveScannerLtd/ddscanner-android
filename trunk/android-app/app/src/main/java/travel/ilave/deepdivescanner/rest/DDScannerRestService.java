package travel.ilave.deepdivescanner.rest;


import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import travel.ilave.deepdivescanner.entities.request.RegisterRequest;
import travel.ilave.deepdivescanner.entities.request.SendReviewRequest;

/**
 * Created by unight on 03.07.2015.
 */
public interface DDScannerRestService {

    @GET("/diving/filters")
    void getFilters(Callback<Response> callback);

    @GET("/diving/divecenters")
    void getDiveCenters(@QueryMap Map<String,String> map, Callback<Response> callback);

    @GET("/diving/divespot/{id}")
    void getDiveSpotById(@Path("id") String id, Callback<Response> callback);

    @Headers("Content-type: application/json")
    @GET("/diving/divespots")
    void getDivespots(@QueryMap Map<String, Object> map, Callback<Response> callback);

    @POST("/diving/register")
    void registerUser(@Body RegisterRequest registerRequest, Callback<Response> callback);

    @POST("/diving/comment")
    void addCOmmentToDiveSpot(@Body SendReviewRequest sendReviewRequest, Callback<Response> callback);

}
