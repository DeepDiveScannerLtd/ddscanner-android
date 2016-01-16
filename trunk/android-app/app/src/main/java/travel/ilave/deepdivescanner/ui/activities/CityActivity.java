package travel.ilave.deepdivescanner.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.City;
import travel.ilave.deepdivescanner.entities.Product;
import travel.ilave.deepdivescanner.entities.ProductsWrapper;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.ui.adapters.PlacesPagerAdapter;
import travel.ilave.deepdivescanner.ui.dialogs.ProductInfoDialog;
import travel.ilave.deepdivescanner.utils.LogUtils;

public class CityActivity extends AppCompatActivity implements PlacesPagerAdapter.OnProductSelectedListener, ProductInfoDialog.OnExploreClickListener, LocationListener {

    public static final String CITY = "CITY";
    public static final String LICENSE = "LICENSE";
    public static final String TAG = "CityActivity";
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private Toolbar toolbar;
    private ViewPager placeViewPager;
    private PlacesPagerAdapter placesPagerAdapter;
    private TabLayout tabLayout;
    private ProgressDialog progressDialog;

    private City city;
    private ProductsWrapper productsWrapper;
    Product selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_search_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.place_sliding_tabs);
        placeViewPager = (ViewPager) findViewById(R.id.place_view_pager);

        city = (City) getIntent().getSerializableExtra(CITY);
        requestCityProducts(city.getId());

    }

    private void requestCityProducts(String cityId) {
       LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.pleaseWait));
        progressDialog.show();
        RestClient.getServiceInstance().getCityProductsByLicense(cityId, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                System.out.println(responseString);
                LogUtils.i("response code is " + s.getStatus());
                LogUtils.i("response body is " + responseString);
                // TODO Handle result handling when activity stopped
                productsWrapper = new Gson().fromJson(responseString, ProductsWrapper.class);
               // populatePlaceViewpager();
            }

            @Override
            public void failure(RetrofitError error) {
                LogUtils.i("failure Message is " + error.getMessage());
                LogUtils.i("failure body is " + error.getBody());
                if (error.getCause() instanceof SocketTimeoutException) {
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                        Toast.makeText(CityActivity.this, R.string.errorConnection, Toast.LENGTH_LONG);
                    } else if (error.getKind().equals(RetrofitError.Kind.HTTP)) {
                        Toast.makeText(CityActivity.this, R.string.serverNotResp, Toast.LENGTH_LONG);
                    }
                }
                // TODO Handle result handling when activity stopped
                // TODO Handle errors
            }
        });
    }

    private void populatePlaceViewpager(LatLng latLng) {
        getSupportActionBar().setTitle(city.getName());
        placesPagerAdapter = new PlacesPagerAdapter(this, getFragmentManager(), city, (ArrayList<Product>) productsWrapper.getProducts(), latLng, this);
        placeViewPager.setAdapter(placesPagerAdapter);
        placeViewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(placeViewPager);
        progressDialog.dismiss();
    }

    public static void show(Context context, City city) {
        Intent intent = new Intent(context, CityActivity.class);
        intent.putExtra(CITY, city);
        context.startActivity(intent);

    }

    @Override
    public void onProductSelected(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
        ProductInfoDialog dialog = new ProductInfoDialog();
        Bundle args = new Bundle();
        args.putParcelable(ProductInfoDialog.PRODUCT, selectedProduct);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "");
    }

    @Override
    public void onExploreClicked() {
        DivePlaceActivity.show(this, selectedProduct);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_city, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                populatePlaceViewpager(place.getLatLng());
                getSupportActionBar().setTitle(place.getAddress());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                //SSLEngineResult.Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.i(TAG, e.toString());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.i(TAG, e.toString());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        populatePlaceViewpager(latLng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }
}
