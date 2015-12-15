package travel.ilave.deepdivescanner.rest;

import retrofit.RestAdapter;

/**
 * Created by unight on 03.07.2015.
 */
public abstract class RestClient {

    private static DDScannerRestService serviceInstance;

    public static DDScannerRestService getServiceInstance() {
        if (serviceInstance == null) {
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://www.trizeri.com").build();
            serviceInstance = restAdapter.create(DDScannerRestService.class);
        }
        return serviceInstance;
    }


}
