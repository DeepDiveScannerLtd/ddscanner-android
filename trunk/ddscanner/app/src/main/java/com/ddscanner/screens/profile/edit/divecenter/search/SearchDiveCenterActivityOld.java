package com.ddscanner.screens.profile.edit.divecenter.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.ddscanner.events.DiveCenterCheckedEvent;
import com.ddscanner.events.ObjectChosedEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.adapters.BaseSearchAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SearchDiveCenterActivityOld extends BaseAppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {

    private static final String ARG_IS_AFTER_SIGN_UP = "isSignUp";
    private static final String TAG = SearchDiveCenterActivityOld.class.getSimpleName();

    private ArrayList<BaseIdNamePhotoEntity> objects;
    private ProgressView progressView;
    private RecyclerView recyclerView;
    private BaseSearchAdapter diveCentersListAdapter;
    private Menu menu;
    private MaterialDialog materialDialog;
    private String diveCenterId;
    private String diveCenterName;
    private boolean isAfterSignUp = false;
    private FloatingActionButton checkedFab;
    private MenuItem searchItem;
    private Handler handler = new Handler();
    private Runnable sendingSearchRequestRunnable;

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
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_SEARCH_DIVE_CENTER_ACTIVITY, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_SEARCH_DIVE_CENTER_ACTIVITY, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_SEARCH_DIVE_CENTER_ACTIVITY, false);
        }
    };

    private DDScannerRestClient.ResultListener<ArrayList<BaseIdNamePhotoEntity>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<BaseIdNamePhotoEntity>>() {
        @Override
        public void onSuccess(ArrayList<BaseIdNamePhotoEntity> result) {
            objects = result;
            diveCentersListAdapter = new BaseSearchAdapter(result, true);
            recyclerView.setAdapter(diveCentersListAdapter);
            progressView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
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

    public static void showForResult(Activity context, int requestCode, boolean isAfterSignUp) {
        Intent intent = new Intent(context, SearchDiveCenterActivityOld.class);
        intent.putExtra(ARG_IS_AFTER_SIGN_UP, isAfterSignUp);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupToolbar(R.string.choose_dc, R.id.toolbar);
        isAfterSignUp = getIntent().getBooleanExtra(ARG_IS_AFTER_SIGN_UP, false);
        findViews();
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCentersList(resultListener, "%");
    }

    private void findViews() {
        materialDialog = Helpers.getMaterialDialog(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressView = (ProgressView) findViewById(R.id.progress_bar);
        checkedFab = (FloatingActionButton) findViewById(R.id.checked_fab);
        checkedFab.setOnClickListener(this);
        checkedFab.hide();
    }

    private void tryToReloadData(String query) {
        handler.removeCallbacks(sendingSearchRequestRunnable);
        sendingSearchRequestRunnable = () -> {
            checkedFab.hide();
            recyclerView.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
            if (!query.isEmpty()) {
                DDScannerApplication.getInstance().getDdScannerRestClient(SearchDiveCenterActivityOld.this).getDiveCentersList(resultListener, query);
            } else {
                DDScannerApplication.getInstance().getDdScannerRestClient(SearchDiveCenterActivityOld.this).getDiveCentersList(resultListener, "%");
            }
        };
        handler.postDelayed(sendingSearchRequestRunnable, 630);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.checked_fab:
                if (isAfterSignUp) {
                    materialDialog.show();
//                    DDScannerApplication.getInstance().getDdScannerRestClient(this).postAddInstructorToDiveCenter(addInstructorResultListener, diveCenterId);
                    break;
                }
                Intent intent = new Intent();
                intent.putExtra("id", diveCenterId);
                intent.putExtra("name", diveCenterName);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_sealife, menu);
        this.menu = menu;
        searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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
        tryToReloadData(query);
//        final List<BaseIdNamePhotoEntity> filteredModelList = filter(objects, query);
//        diveCentersListAdapter.animateTo(filteredModelList);
//        recyclerView.scrollToPosition(0);
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
            if (!checkedFab.isShown()) {
                checkedFab.show();
            }
            diveCenterId = event.getBaseIdNamePhotoEntity().getId();
            diveCenterName = event.getBaseIdNamePhotoEntity().getName();
//            if (isAfterSignUp) {
//                materialDialog.show();
//                DDScannerApplication.getInstance().getDdScannerRestClient(this).postAddInstructorToDiveCenter(addInstructorResultListener, diveCenterId);
//            }
//            Intent intent = new Intent();
//            intent.putExtra("id", diveCenterId);
//            setResult(RESULT_OK, intent);
//            finish();
        }
    }

    @Subscribe
    public void diveCenterChosed(DiveCenterCheckedEvent event) {
        checkedFab.show();
    }

}
