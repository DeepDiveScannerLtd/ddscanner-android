package travel.ilave.deepdivescanner.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

public class CityActivity extends AppCompatActivity implements PlacesPagerAdapter.OnProductSelectedListener, ProductInfoDialog.OnExploreClickListener {

    public static final String CITY = "CITY";
    public static final String LICENSE = "LICENSE";

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.place_sliding_tabs);
        placeViewPager = (ViewPager) findViewById(R.id.place_view_pager);

        city = (City) getIntent().getSerializableExtra(CITY);
        String license = getIntent().getStringExtra(LICENSE);
        requestCityProducts(city.getId(), license);

    }

    private void requestCityProducts(String cityId, String license) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        RestClient.getServiceInstance().getCityProductsByLicense(cityId, license, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                LogUtils.i("response code is " + s.getStatus());
                LogUtils.i("response body is " + responseString);
                // TODO Handle result handling when activity stopped
                productsWrapper = new Gson().fromJson(responseString, ProductsWrapper.class);
                populatePlaceViewpager();
                progressDialog.dismiss();
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

    private void populatePlaceViewpager() {
        getSupportActionBar().setTitle(city.getName());
        placesPagerAdapter = new PlacesPagerAdapter(this, getFragmentManager(), city, (ArrayList<Product>) productsWrapper.getProducts(), this);
        placeViewPager.setAdapter(placesPagerAdapter);
        placeViewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(placeViewPager);
    }

    public static void show(Context context, City city, String license) {
        Intent intent = new Intent(context, CityActivity.class);
        intent.putExtra(CITY, city);
        intent.putExtra(LICENSE, license);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
