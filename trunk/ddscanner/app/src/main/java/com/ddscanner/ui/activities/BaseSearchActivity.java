package com.ddscanner.ui.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import com.ddscanner.R;
import com.ddscanner.entities.BaseIdNamePhotoEntity;
import com.ddscanner.interfaces.ListItemClickListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.search.SearchAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rey.material.widget.ProgressView;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseSearchActivity extends BaseAppCompatActivity implements ListItemClickListener<BaseIdNamePhotoEntity> {

    DDScannerRestClient.ResultListener<ArrayList<BaseIdNamePhotoEntity>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<BaseIdNamePhotoEntity>>() {
        @Override
        public void onSuccess(ArrayList<BaseIdNamePhotoEntity> result) {
            entities = result;
            if (currentData.size() > 0) {
                for (BaseIdNamePhotoEntity oldEntity : currentData) {
                    for (BaseIdNamePhotoEntity newEntity : entities) {
                        if (oldEntity.getName().equals(newEntity.getName())) {
                            entities.get(entities.indexOf(newEntity)).setActive(true);
                        }
                    }
                }
            }
            searchAdapter.setObjectsList(entities);
            searchList.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.GONE);
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

    ArrayList<BaseIdNamePhotoEntity> entities = new ArrayList<>();
    ArrayList<BaseIdNamePhotoEntity> currentData = new ArrayList<>();
    private static final String ARG_SOURCE = "source";
    private static final String ARG_CURRENT_LIST = "list";
    private Gson gson = new Gson();
    private SearchAdapter searchAdapter;

    private SearchSource searchSource;

    public enum SearchSource {
        BRAND, ASSOCIATION
    }

    public static void showForResult(Activity activity, int requestCode, SearchSource source, String currentData) {
        Intent intent = new Intent(activity, BaseSearchActivity.class);
        intent.putExtra(ARG_SOURCE, source);
        if (currentData != null) {
            intent.putExtra(ARG_CURRENT_LIST, currentData);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_search);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra(ARG_CURRENT_LIST) != null) {
            Type listType = new TypeToken<ArrayList<BaseIdNamePhotoEntity>>(){}.getType();
            currentData = gson.fromJson(getIntent().getStringExtra(ARG_CURRENT_LIST), listType);
        }
        searchAdapter = new SearchAdapter(this);
        searchSource =(SearchSource) getIntent().getSerializableExtra(ARG_SOURCE);
        switch (searchSource) {
            case BRAND:

                break;
            case ASSOCIATION:
                setupToolbar(R.string.toolbar_choose_association, R.id.toolbar);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (entities.size() > 0) {
            getMenuInflater().inflate(R.menu.menu_search_save, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClick(BaseIdNamePhotoEntity item) {

    }
}
