package travel.ilave.deepdivescanner.rest;

import com.facebook.CallbackManager;

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
import travel.ilave.deepdivescanner.entities.Subscriber;
import travel.ilave.deepdivescanner.entities.request.BookingRequest;
import travel.ilave.deepdivescanner.entities.request.SubscribeRequest;
import travel.ilave.deepdivescanner.entities.request.TravelerRequest;

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

    @GET("/diving/divecentres")
    void getDiveCenters(@QueryMap Map<String,String> map, Callback<Response> callback);

    @GET("/diving/divespot/{id}")
    void getDiveSpotById(@Path("id") String id, Callback<Response> callback);

    @GET("/diving/product/{id}/date/{date}/options")
    void getProductOffers(@Path("id") String id, @Path("date") String date, Callback<Response> callback);

    @GET("/diving/option/{id}/conditions")
    void getOfferConditions(@Path("id") String id, Callback<Response> callback);

    @POST("/diving/subscribe")
    void subscribe(@Body SubscribeRequest subscribeRequest, Callback<Response> callback);


    @Headers("Content-type: application/json")
    @GET("/diving/divespots")
    void getDivespots(@QueryMap Map<String,String> map, Callback<Response> callback);


    @POST("/diving/booking")
    void booking(@Body BookingRequest bookingRequest,
                 Callback<Response> callback
    );

    @POST("/diving/traveler")
    void traveler(@Body TravelerRequest travelerRequest,
                 Callback<Response> callback
    );

//    @Headers({
//            "Content-Encoding: utf-8"
//    })
//    @POST("/sign/in")
//    void signIn(@Body CredentialsJsonEntity credentialsJsonEntity, Callback<Response> callback);
}
