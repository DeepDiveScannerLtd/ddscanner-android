package travel.ilave.deepdivescanner.rest;


import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by unight on 03.07.2015.
 */
public interface DDScannerRestService {

   /* @GET("/diving/cities")
    void getCities(Callback<Response> callback);*/

   /* @GET("/diving/divespots/{lat}/{lng}/{radius}")
    void getProductsByCoordinates(@Path("lat") String lat, @Path("lng") String lng, @Path("radius") String radius, Callback<Response> callback);
*/
   /* @GET("/diving/product/{id}")
    void getProductById(@Path("id") String id, Callback<Response> callback);*/

    @GET("/diving/filters")
    void getFilters(Callback<Response> callback);

    @GET("/diving/divecenters")
    void getDiveCenters(@QueryMap Map<String,String> map, Callback<Response> callback);

    @GET("/diving/divespot/{id}")
    void getDiveSpotById(@Path("id") String id, Callback<Response> callback);

   @Headers("Content-type: application/json")
    @GET("/diving/divespots")
    void getDivespots(@QueryMap Map<String,String> map, Callback<Response> callback);

//    @Headers({
//            "Content-Encoding: utf-8"
//    })
//    @POST("/sign/in")
//    void signIn(@Body CredentialsJsonEntity credentialsJsonEntity, Callback<Response> callback);
}
