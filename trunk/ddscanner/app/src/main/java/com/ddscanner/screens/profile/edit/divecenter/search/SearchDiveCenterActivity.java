package com.ddscanner.screens.profile.edit.divecenter.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.BaseIdNamePhotoEntity;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.adapters.BaseSearchAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;
import java.util.ArrayList;

public class SearchDiveCenterActivity extends BaseAppCompatActivity implements SearchView.OnQueryTextListener{

    public static void showForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SearchDiveCenterActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    private RecyclerView recyclerView;
    private BaseSearchAdapter diveCentersListAdapter;
    private Menu menu;
    private MenuItem searchItem;
    private MaterialDialog materialDialog;
    private ProgressView progressView;
    private Handler handler = new Handler();
    private Runnable sendingSearchRequestRunnable;
    private RelativeLayout noResultsView;

    private DDScannerRestClient.ResultListener<Void> addInstructorResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            materialDialog.dismiss();
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }
    };

    private DDScannerRestClient.ResultListener<ArrayList<BaseIdNamePhotoEntity>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<BaseIdNamePhotoEntity>>() {
        @Override
        public void onSuccess(ArrayList<BaseIdNamePhotoEntity> result) {
            noResultsView.setVisibility(View.GONE);
            diveCentersListAdapter = new BaseSearchAdapter(result, true);
            recyclerView.setAdapter(diveCentersListAdapter);
            progressView.setVisibility(View.GONE);
            if (result.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                noResultsView.setVisibility(View.VISIBLE);
            }
            searchItem.setVisible(true);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_SEARCH_DIVE_CENTER_ACTIVITY, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_SEARCH_DIVE_CENTER_ACTIVITY, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_SEARCH_DIVE_CENTER_ACTIVITY, false);
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dive_center);
        setupToolbar(R.string.choose_dc, R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.dive_centers_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressView = (ProgressView) findViewById(R.id.progress_bar);
        noResultsView = (RelativeLayout) findViewById(R.id.no_results);
        materialDialog = Helpers.getMaterialDialog(this);
        TextView addDiveCenter = (TextView) findViewById(R.id.add_spot);
        addDiveCenter.setOnClickListener(view -> CreateDiveCenterActivity.showForCreateDiveCenter(this, ActivitiesRequestCodes.REQUEST_CODE_SEARCH_DIVE_CENTER_ACTIVITY_ADD_NEW_DIVE_CENTER));
        tryToReloadData("%");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_sealife, menu);
        this.menu = menu;
        searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(true);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private void tryToReloadData(String query) {
        handler.removeCallbacks(sendingSearchRequestRunnable);
        sendingSearchRequestRunnable = () -> {
            recyclerView.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
            if (!query.isEmpty()) {
                DDScannerApplication.getInstance().getDdScannerRestClient(SearchDiveCenterActivity.this).getDiveCentersList(resultListener, query);
            } else {
                DDScannerApplication.getInstance().getDdScannerRestClient(SearchDiveCenterActivity.this).getDiveCentersList(resultListener, "%");
            }
        };
        handler.postDelayed(sendingSearchRequestRunnable, 630);
    }

    @Override
    public boolean onQueryTextChange(String s) {
        tryToReloadData(s);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
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
}
