package com.ddscanner.rest;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.FeatureSearchResponseEntity;
import com.ddscanner.entities.SearchFeature;
import com.ddscanner.utils.Helpers;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class MapboxRestClient {


    protected Gson gson = new Gson();

    public void getArrayListOfFeatures(DDScannerRestClient.ResultListener<FeatureSearchResponseEntity> resultListener, String query) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getMapboxGeocodingApiRestService().getPlacesForQuesry(query, DDScannerApplication.getInstance().getString(R.string.mapbox_api_key), "en");
        call.enqueue(new MapBoxResponseEntityCallback<FeatureSearchResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(DDScannerRestClient.ResultListener<FeatureSearchResponseEntity> resultListener, String responseString) throws JSONException {
                FeatureSearchResponseEntity searchResponseEntity = gson.fromJson(responseString, FeatureSearchResponseEntity.class);
                resultListener.onSuccess(searchResponseEntity);
            }
        });
    }

}
