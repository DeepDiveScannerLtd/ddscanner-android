package com.ddscanner.screens.brands;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Brand;
import com.ddscanner.interfaces.ListItemClickListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandsActivity extends BaseAppCompatActivity implements ListItemClickListener<Brand> {

    public enum BrandSource {
        ALL, DIVECENTER
    }

    private static final String ARG_ID= "id";
    private static final String ARG_SOURCE = "source";

    DDScannerRestClient.ResultListener<ArrayList<Brand>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<Brand>>() {
        @Override
        public void onSuccess(ArrayList<Brand> result) {
            brandsAdapter.setBrands(result);
            progressView.setVisibility(View.GONE);
            searchList.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }

        @Override
        public void onInternetConnectionClosed() {

        }
    };

    @BindView(R.id.search_list)
    RecyclerView searchList;
    @BindView(R.id.progress_view)
    ProgressView progressView;

    BrandsAdapter brandsAdapter;

    BrandSource source;
    String diveCenterId;

    public static void showForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, BrandsActivity.class);
        intent.putExtra(ARG_SOURCE, BrandSource.ALL);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void show(Context context, String diveCenterId, BrandSource source) {
        Intent intent = new Intent(context, BrandsActivity.class);
        intent.putExtra(ARG_ID, diveCenterId);
        intent.putExtra(ARG_SOURCE, source);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_search);
        ButterKnife.bind(this);
        source = (BrandSource) getIntent().getSerializableExtra(ARG_SOURCE);
        setupToolbar(R.string.brands, R.id.toolbar);
        brandsAdapter = new BrandsAdapter();
        searchList.setLayoutManager(new LinearLayoutManager(this));
        searchList.setAdapter(brandsAdapter);
        switch (source) {
            case ALL:
                brandsAdapter.setListItemClickListener(this);
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getBrands(resultListener);
                break;
            case DIVECENTER:
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenterBrands(resultListener, getIntent().getStringExtra(ARG_ID));
                break;
        }

    }

    @Override
    public void onItemClick(Brand item) {
        Intent intent = new Intent();
        intent.putExtra("brand", new Gson().toJson(item));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
