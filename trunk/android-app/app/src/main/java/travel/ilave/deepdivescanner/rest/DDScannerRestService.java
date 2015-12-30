package travel.ilave.deepdivescanner.rest;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Path;
import travel.ilave.deepdivescanner.entities.Booking;
import travel.ilave.deepdivescanner.entities.request.BookingRequest;
import travel.ilave.deepdivescanner.entities.request.TravelerRequest;

/**
 * Created by unight on 03.07.2015.
 */
public interface DDScannerRestService {

    @GET("/api/diving/cities")
    void getCities(Callback<Response> callback);

    @GET("/api/diving/products/city/{cityId}/license/{license}")
    void getCityProductsByLicense(@Path("cityId") String cityId, @Path("license") String licenseId, Callback<Response> callback);

    @GET("/api/diving/product/{id}")
    void getProductById(@Path("id") String id, Callback<Response> callback);

    @GET("/api/diving/product/{id}/date/{date}/options")
    void getProductOffers(@Path("id") String id, @Path("date") String date, Callback<Response> callback);

    @GET("/api/diving/option/{id}/conditions")
    void getOfferConditions(@Path("id") String id, Callback<Response> callback);

    @POST("/api/diving/booking")
    void booking(@Body BookingRequest bookingRequest,
                 Callback<Response> callback
    );

    @POST("/api/diving/traveler")
    void traveler(@Body TravelerRequest travelerRequest,
                 Callback<Response> callback
    );

//    @Headers({
//            "Content-Encoding: utf-8"
//    })
//    @POST("/sign/in")
//    void signIn(@Body CredentialsJsonEntity credentialsJsonEntity, Callback<Response> callback);
}
