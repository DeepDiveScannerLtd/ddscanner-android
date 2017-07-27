package com.ddscanner.screens.profile.edit.divecenter.search;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
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
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.BaseIdNamePhotoEntity;
import com.ddscanner.entities.DiveCenterSearchItem;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.interfaces.DiveCenterItemClickListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.adapters.BaseSearchAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

public class SearchDiveCenterActivity extends BaseAppCompatActivity implements SearchView.OnQueryTextListener, DialogClosedListener {

    public static void showForResult(Activity activity, int requestCode, boolean isForEditProfile) {
        Intent intent = new Intent(activity, SearchDiveCenterActivity.class);
        if (isForEditProfile) {
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setDivecenterSearchSource(SharedPreferenceHelper.SearchSourceType.PROFILE);
        } else {
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setDivecenterSearchSource(SharedPreferenceHelper.SearchSourceType.REGISTRATION);
        }
        intent.putExtra(ARG_ID_FOR_EDIT, isForEditProfile);
        activity.startActivityForResult(intent, requestCode);
    }

    private DDScannerRestClient.ResultListener<Void> addInstructorToDiveCenter = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            progressView.setVisibility(View.GONE);
            finish();
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.unexcepted_error_title, R.string.unexcepted_error_text, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }
    };

    private static final int PAGE_SIZE = 15;
    private static final String ARG_ID_FOR_EDIT = "is_for_edit";
    private RecyclerView recyclerView;
    private Menu menu;
    private MenuItem searchItem;
    private MaterialDialog materialDialog;
    private ProgressView progressView;
    private Handler handler = new Handler();
    private Runnable sendingSearchRequestRunnable;
    private RelativeLayout noResultsView;
    private SearchDiveCenterListAdapter searchDiveCenterListAdapter;
    private int currentPage = 0;
    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;
    private DiveCentersListResultListener paginationResultListener = new DiveCentersListResultListener(true);
    private DiveCentersListResultListener loadResultListener = new DiveCentersListResultListener(false);
    private String query;
    private DiveCenterItemClickListener diveCenterItemClickListener;
    private boolean isForEditProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dive_center);
        createClickListener();
        setupToolbar(R.string.choose_dc, R.id.toolbar);
        searchDiveCenterListAdapter = new SearchDiveCenterListAdapter(diveCenterItemClickListener);
        isForEditProfile = getIntent().getBooleanExtra(ARG_ID_FOR_EDIT, false);
        recyclerView = findViewById(R.id.dive_centers_list);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(searchDiveCenterListAdapter);
        progressView = findViewById(R.id.progress_bar);
        noResultsView = findViewById(R.id.no_results);
        materialDialog = Helpers.getMaterialDialog(this);
        TextView addDiveCenter = findViewById(R.id.add_spot);
        addDiveCenter.setOnClickListener(view -> CreateDiveCenterActivity.showForCreateDiveCenter(this, ActivitiesRequestCodes.REQUEST_CODE_SEARCH_DIVE_CENTER_ACTIVITY_ADD_NEW_DIVE_CENTER));
//        tryToReloadData("%");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initializeListenerForHighVersions();
        } else {
            initilizeListenerForLowVersions();
        }
    }

    private void createClickListener() {
        diveCenterItemClickListener = item -> {
            switch (item.getDivCenterType()) {
                case LEGACY:
                    EventsTracker.trackInstructorRegistrationDcLegacyChosen();
                    if (item.isInvited()) {
                        if (!isForEditProfile) {
                            progressView.setVisibility(View.VISIBLE);
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).postAddInstructorToDiveCenter(addInstructorToDiveCenter, item.getId(), item.getIntegerType());
                            break;
                        }
                        Intent intent = new Intent();
                        intent.putExtra(Constants.ARG_DC_NAME, item.getName());
                        intent.putExtra(Constants.ARG_DC_TYPE, item.getIntegerType());
                        intent.putExtra(Constants.ARG_ID, item.getId());
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    }
                    CreateDiveCenterActivity.showForEditCurrentDiveCenter(this, ActivitiesRequestCodes.REQUEST_CODE_SEARCH_DIVE_CENTER_ACTIVITY_EDIT_CURRENT_LEGACY_DIVE_SPOT, new Gson().toJson(item));
                    break;
                case USER:
                    EventsTracker.trackInstructorRegistrationDcUserChosen();
                    if (!isForEditProfile) {
                        progressView.setVisibility(View.VISIBLE);
                        DDScannerApplication.getInstance().getDdScannerRestClient(this).postAddInstructorToDiveCenter(addInstructorToDiveCenter, item.getId(), item.getIntegerType());
                        break;
                    }
                    Intent intent = new Intent();
                    intent.putExtra(Constants.ARG_DC_NAME, item.getName());
                    intent.putExtra(Constants.ARG_DC_TYPE, item.getIntegerType());
                    intent.putExtra(Constants.ARG_ID, item.getId());
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case NEW:
                    EventsTracker.trackInstructorRegistrationAddNewChosen();
                    if (!isForEditProfile) {
                        progressView.setVisibility(View.VISIBLE);
                        DDScannerApplication.getInstance().getDdScannerRestClient(this).postAddInstructorToDiveCenter(addInstructorToDiveCenter, item.getId(), item.getIntegerType());
                        break;
                    }
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.ARG_DC_NAME, item.getName());
                    returnIntent.putExtra(Constants.ARG_DC_TYPE, item.getIntegerType());
                    returnIntent.putExtra(Constants.ARG_ID, item.getId());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                    break;
            }
        };
    }

    @TargetApi(23)
    private void initializeListenerForHighVersions() {
        RecyclerView.OnScrollChangeListener listener = (view, i, i1, i2, i3) -> loadMoreDiveCenters();
        recyclerView.setOnScrollChangeListener(listener);
    }

    @SuppressWarnings("deprecation")
    private void initilizeListenerForLowVersions() {
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                loadMoreDiveCenters();
            }
        };
        recyclerView.setOnScrollListener(scrollListener);
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

    private void loadMoreDiveCenters() {
        int visibleItemsCount = linearLayoutManager.getChildCount();
        int totalItemCount = linearLayoutManager.getItemCount();
        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        if (!isLoading) {
            if ((visibleItemsCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                    DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCentersByQuery(query, currentPage + 1, paginationResultListener);
                    searchDiveCenterListAdapter.startLoading();
                    isLoading = true;
            }
        }
    }

    private void tryToReloadData(String query) {
        this.query = query;
        currentPage = 0;
        handler.removeCallbacks(sendingSearchRequestRunnable);
        sendingSearchRequestRunnable = () -> {
            recyclerView.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
            if (!query.isEmpty()) {
                DDScannerApplication.getInstance().getDdScannerRestClient(SearchDiveCenterActivity.this).getDiveCentersByQuery(query, currentPage, loadResultListener);
            } else {
                DDScannerApplication.getInstance().getDdScannerRestClient(SearchDiveCenterActivity.this).getDiveCentersByQuery("%", currentPage, loadResultListener);
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

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_SEARCH_DIVE_CENTER_ACTIVITY_EDIT_CURRENT_LEGACY_DIVE_SPOT:
                if (resultCode == RESULT_OK) {
                    if (isForEditProfile) {
                        setResult(RESULT_OK, data);
                        finish();
                    } else {
                        progressView.setVisibility(View.VISIBLE);
                        DDScannerApplication.getInstance().getDdScannerRestClient(this).postAddInstructorToDiveCenter(addInstructorToDiveCenter, data.getIntExtra(Constants.ARG_ID, 0), data.getIntExtra(Constants.ARG_DC_TYPE, 0));
                    }
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_SEARCH_DIVE_CENTER_ACTIVITY_ADD_NEW_DIVE_CENTER:
                if (resultCode == RESULT_OK) {
                    EventsTracker.tracNewDcChosenEvent();
                    if (isForEditProfile) {
                        setResult(RESULT_OK, data);
                        finish();
                    } else {
                        progressView.setVisibility(View.VISIBLE);
                        DDScannerApplication.getInstance().getDdScannerRestClient(this).postAddInstructorToDiveCenter(addInstructorToDiveCenter, data.getIntExtra(Constants.ARG_ID, 0), data.getIntExtra(Constants.ARG_DC_TYPE, 0));
                    }
                }
                break;
        }
    }

    class DiveCentersListResultListener extends DDScannerRestClient.ResultListener<ArrayList<DiveCenterSearchItem>> {

        private boolean isFromPagination;

        public DiveCentersListResultListener(boolean isFromPagination) {
            this.isFromPagination = isFromPagination;
        }

        @Override
        public void onSuccess(ArrayList<DiveCenterSearchItem> result) {
            if (!isFromPagination) {
                noResultsView.setVisibility(View.GONE);
                progressView.setVisibility(View.GONE);
                searchDiveCenterListAdapter.setData(result);
                if (result.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    noResultsView.setVisibility(View.VISIBLE);
                }
                searchItem.setVisible(true);
                return;
            }
            if (!(result.size() < PAGE_SIZE)) {
                isLoading = false;
            }
            currentPage++;
            searchDiveCenterListAdapter.dataLoaded();
            searchDiveCenterListAdapter.addData(result);
        }

        @Override
        public void onConnectionFailure() {
            if (!isFromPagination) {
                UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_SEARCH_DIVE_CENTER_ACTIVITY, false);
                return;
            }
            isLoading = false;
            searchDiveCenterListAdapter.dataLoaded();
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            if (!isFromPagination) {
                UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_SEARCH_DIVE_CENTER_ACTIVITY, false);
                Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
                return;
            }
            isLoading = false;
            searchDiveCenterListAdapter.dataLoaded();
        }

        @Override
        public void onInternetConnectionClosed() {
            if (!isFromPagination) {
                UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_SEARCH_DIVE_CENTER_ACTIVITY, false);
                return;
            }
            isLoading = false;
            searchDiveCenterListAdapter.dataLoaded();
        }
    }

}
