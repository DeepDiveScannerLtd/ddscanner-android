package travel.ilave.deepdivescanner.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Offer;
import travel.ilave.deepdivescanner.entities.OffersWrapper;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.ui.adapters.OffersAdapter;
import travel.ilave.deepdivescanner.utils.LogUtils;

public class OffersActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String PRODUCT_ID = "PRODUCT_ID";
    public static final String DATE = "DATE";

    private Toolbar toolbar;
    private OffersAdapter mAdapter;
    ListView mListView;
    private ProgressDialog progressDialog;

    private String productId;
    private String date;
    private String name;

    ArrayList<Offer> offers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.offers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.list_offers);

        productId = getIntent().getStringExtra(PRODUCT_ID);
        date = getIntent().getStringExtra(DATE);
        name = getIntent().getStringExtra(VoucherActivity.PLACE);

        requestProductOffers(productId, date);
    }

    private void requestProductOffers(String productId, String date) {
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        RestClient.getServiceInstance().getProductOffers(productId, date, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                LogUtils.i("response code is " + s.getStatus());
                LogUtils.i("response body is " + responseString);
                // TODO Handle result handling when activity stopped
                offers = (ArrayList<Offer>) new Gson().fromJson(responseString, OffersWrapper.class).getOptions();
                populateOffersList();
                progressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                LogUtils.i("failure Message is " + error.getMessage());
                LogUtils.i("failure body is " + error.getBody());
                // TODO Handle result handling when activity stopped
                // TODO Handle errors
            }
        });
    }

    private void populateOffersList() {
        mAdapter = new OffersAdapter(this, offers, new OffersAdapter.OnOptionSelectedListener() {
            @Override
            public void onOptionsSelected(Offer offer) {
                ConditionsActivity.show(OffersActivity.this, offer, date, name);
            }
        });
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!mAdapter.getItem(position).isExpanded()) {
            mAdapter.setItemsUnexpanded();
            mAdapter.getItem(position).setIsExpanded(true);
            mAdapter.notifyDataSetChanged();
        }
    }

    public static void show(Context context, String productId, String date, String name) {
        Intent intent = new Intent(context, OffersActivity.class);
        intent.putExtra(PRODUCT_ID, productId);
        intent.putExtra(DATE, date);
        intent.putExtra(VoucherActivity.PLACE, name);
        context.startActivity(intent);
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