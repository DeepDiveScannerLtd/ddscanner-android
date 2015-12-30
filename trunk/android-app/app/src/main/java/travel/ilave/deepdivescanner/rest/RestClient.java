package travel.ilave.deepdivescanner.rest;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by unight on 03.07.2015.
 */
public abstract class RestClient {

    private static DDScannerRestService serviceInstance;

    public static DDScannerRestService getServiceInstance() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                request.addHeader("Accept","application/vnd.trizeri.v1+json");
            }
        };
        if (serviceInstance == null) {
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://api.trizeri.com").setRequestInterceptor(requestInterceptor).build();
            serviceInstance = restAdapter.create(DDScannerRestService.class);
        }
        return serviceInstance;
    }


}
