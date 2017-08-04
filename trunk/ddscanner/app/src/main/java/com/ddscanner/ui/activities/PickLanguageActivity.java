package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Language;
import com.ddscanner.events.LanguageChosedEvent;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.LanguageSearchAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class PickLanguageActivity extends BaseAppCompatActivity implements SearchView.OnQueryTextListener, DialogClosedListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<Language> languagesList;
    private ProgressView progressView;
    private LanguageSearchAdapter languageSearchAdapter;
    private MenuItem searchItem;

    private DDScannerRestClient.ResultListener<ArrayList<Language>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<Language>>() {
        @Override
        public void onSuccess(ArrayList<Language> result) {
            languagesList = result;
            languageSearchAdapter = new LanguageSearchAdapter(PickLanguageActivity.this, result);
            recyclerView.setAdapter(languageSearchAdapter);
            progressView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            searchItem.setVisible(true);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_PICK_LANG_ACTIVITY_HIDE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_PICK_LANG_ACTIVITY_HIDE, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_PICK_LANG_ACTIVITY_HIDE, false);
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
        findViews();
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveSpotLanguages(resultListener);
    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressView = findViewById(R.id.progress_bar);
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
//        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
//        DDScannerApplication.bus.unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_sealife, menu);
        searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }
}
