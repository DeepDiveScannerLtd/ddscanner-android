package com.ddscanner.screens.profile.edit.divecenter.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.BaseIdNamePhotoEntity;
import com.ddscanner.events.ObjectChosedEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.adapters.BaseSearchAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SearchDiveCenterActivity extends BaseAppCompatActivity implements SearchView.OnQueryTextListener{
    
    private ArrayList<BaseIdNamePhotoEntity> objects;
    private ProgressView progressView;
    private RecyclerView recyclerView;
    private BaseSearchAdapter diveCentersListAdapter;
    private Menu menu;
    private MaterialDialog materialDialog;
    private String diveCenterId;
    
    private DDScannerRestClient.ResultListener<ArrayList<BaseIdNamePhotoEntity>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<BaseIdNamePhotoEntity>>() {
        @Override
        public void onSuccess(ArrayList<BaseIdNamePhotoEntity> result) {
            objects = result;
            diveCentersListAdapter = new BaseSearchAdapter(result);
            recyclerView.setAdapter(diveCentersListAdapter);
            progressView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_SEARCH_DIVE_CENTER_ACTIVITY, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_SEARCH_DIVE_CENTER_ACTIVITY, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_SEARCH_DIVE_CENTER_ACTIVITY, false);
        }

    };

    public static void showForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, SearchDiveCenterActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupToolbar(R.string.choose_dc, R.id.toolbar);
        findViews();
        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveCentersList(resultListener);
    }

    private void findViews() {
        materialDialog = Helpers.getMaterialDialog(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressView = (ProgressView) findViewById(R.id.progress_bar);
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

    private List<BaseIdNamePhotoEntity> filter(List<BaseIdNamePhotoEntity> models, String query) {
        query = query.toLowerCase();
        if (query.isEmpty()) {
            return models;
        }
        final List<BaseIdNamePhotoEntity> filteredModelList = new ArrayList<>();
        for (BaseIdNamePhotoEntity model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<BaseIdNamePhotoEntity> filteredModelList = filter(objects, query);
        diveCentersListAdapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
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

    @Subscribe
    public void objectChsedEvent(ObjectChosedEvent event) {
        if (event.getBaseIdNamePhotoEntity().getId() != null) {
            diveCenterId = event.getBaseIdNamePhotoEntity().getId();
            Intent intent = new Intent();
            intent.putExtra("id", diveCenterId);
            setResult(RESULT_OK);
            finish();
        }
    }

}
