package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.BaseIdNamePhotoEntity;
import com.ddscanner.entities.CountryEntity;
import com.ddscanner.entities.DiveCenterCountry;
import com.ddscanner.entities.Language;
import com.ddscanner.events.LanguageChosedEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.LanguageSearchAdapter;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import org.apache.commons.codec.language.bm.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PickLanguageActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = PickLanguageActivity.class.getSimpleName();

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<Language> languagesList;
    private Menu menu;
    private ProgressView progressView;
    private LanguageSearchAdapter languageSearchAdapter;
    private boolean isPickLanguage;

    private DDScannerRestClient.ResultListener<ArrayList<Language>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<Language>>() {
        @Override
        public void onSuccess(ArrayList<Language> result) {
            languageSearchAdapter = new LanguageSearchAdapter(PickLanguageActivity.this, result);
            recyclerView.setAdapter(languageSearchAdapter);
            progressView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }
    };

    public static void showForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, PickLanguageActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        isPickLanguage = getIntent().getBooleanExtra("isLanguage", false);
        findViews();
        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveSpotLanguages(resultListener);
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressView = (ProgressView) findViewById(R.id.progress_bar);
        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.new_language);
    }

    @Override
    public void onStart() {
        super.onStart();
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_sealife, menu);
        this.menu = menu;
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private List<Language> filter(List<Language> models, String query) {
        query = query.toLowerCase();
        if (query.isEmpty()) {
            return models;
        }
        final List<Language> filteredModelList = new ArrayList<>();
        for (Language model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<Language> filteredModelList = filter(languagesList, query);
        languageSearchAdapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Subscribe
    public void languageChosed(LanguageChosedEvent event) {
        Intent intent = new Intent();
        intent.putExtra("language", event.getLanguageName());
        setResult(RESULT_OK, intent);
        finish();
    }

}
