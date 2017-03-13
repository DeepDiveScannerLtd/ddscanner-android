package com.ddscanner.ui.activities;

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
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.events.ObjectChosedEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.BaseSearchAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class PickCountryActivity extends BaseAppCompatActivity implements SearchView.OnQueryTextListener, DialogClosedListener {

    private ArrayList<BaseIdNamePhotoEntity> objects;
    private ProgressView progressView;
    private RecyclerView recyclerView;
    private BaseSearchAdapter diveCentersListAdapter;
    private Menu menu;
    private MaterialDialog materialDialog;

    private DDScannerRestClient.ResultListener<ArrayList<BaseIdNamePhotoEntity>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<BaseIdNamePhotoEntity>>() {
        @Override
        public void onSuccess(ArrayList<BaseIdNamePhotoEntity> result) {
            objects = result;
            diveCentersListAdapter = new BaseSearchAdapter(result, false);
            recyclerView.setAdapter(diveCentersListAdapter);
            progressView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_PICK_COUNTRY_ACTIVITY_HIDE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_PICK_COUNTRY_ACTIVITY_HIDE, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_PICK_COUNTRY_ACTIVITY_HIDE, false);
        }

    };

    public static void showForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, PickCountryActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupToolbar(R.string.chose_country, R.id.toolbar);
        findViews();
        DDScannerApplication.getInstance().getDdScannerRestClient().getListOfCountries(resultListener);
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
        Intent intent = new Intent();
        intent.putExtra("country", event.getBaseIdNamePhotoEntity());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }
}
