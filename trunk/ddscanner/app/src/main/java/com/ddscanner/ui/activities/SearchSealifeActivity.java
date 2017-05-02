package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.SealifeListResponseEntity;
import com.ddscanner.entities.SealifeResponseEntity;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.events.SealifeChoosedEvent;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.sealife.add.AddSealifeActivity;
import com.ddscanner.ui.adapters.SealifeSearchAdapter;
import com.ddscanner.ui.adapters.SealifeSectionedRecyclerViewAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.google.android.gms.maps.model.LatLng;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SearchSealifeActivity extends BaseAppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener, DialogClosedListener {

    private static final String ARG_LOCATION = "LATLNG";

    private Toolbar toolbar;
    private RecyclerView sealifeList;
    private TextView results;
    private RelativeLayout notFoundLayout;
    private TextView textNotFound;
    private Button addManually;
    private Menu menu;
    private ProgressView progressView;
    private RelativeLayout contentLayout;
    private MenuItem searchMenuItem;
    private boolean isHasLocation = false;
    private LatLng diveSpotLocation;
    private SealifeSearchAdapter searchSealifeListAdapter;
    private SealifeSearchAdapter searchSealifeListAdapterWithSections;
    private boolean isSectioned = false;
    SealifeSectionedRecyclerViewAdapter sectionedRecyclerViewAdapter;

    private List<SealifeShort> sealifes = new ArrayList<>();
    private SealifeResponseEntity sealifeResponseEntity = new SealifeResponseEntity();

    private DDScannerRestClient.ResultListener<SealifeListResponseEntity> sealifeResponseEntityResultListener = new DDScannerRestClient.ResultListener<SealifeListResponseEntity>() {
        @Override
        public void onSuccess(SealifeListResponseEntity result) {
            sealifeList.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
            sealifeList.setAdapter(searchSealifeListAdapter);
            searchMenuItem.setVisible(true);
            setupList(result);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_SEARCH_SEALIFE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            EventsTracker.trackUnknownServerError(url, errorMessage);
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_SEARCH_SEALIFE_ACTIVITY_UNEXPECTED_ERROR, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_SEARCH_SEALIFE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sealife);
        EventsTracker.trackSearchSeaLife();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        sealifeList = (RecyclerView) findViewById(R.id.recyclerView);
        notFoundLayout = (RelativeLayout) findViewById(R.id.not_found_layout);
        textNotFound = (TextView) findViewById(R.id.text_not_found);
        addManually = (Button) findViewById(R.id.add_manualy);
        progressView = (ProgressView) findViewById(R.id.progressBarFull);
        contentLayout = (RelativeLayout) findViewById(R.id.content_layout);
        addManually.setOnClickListener(this);
        if (getIntent().getParcelableExtra(ARG_LOCATION) != null) {
            isHasLocation = true;
            diveSpotLocation = getIntent().getParcelableExtra(ARG_LOCATION);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.search_sealife);
        if (!isHasLocation) {
            DDScannerApplication.getInstance().getDdScannerRestClient().getAllSealifes(sealifeResponseEntityResultListener);
        } else {
            DDScannerApplication.getInstance().getDdScannerRestClient().getSealifesByLocation(sealifeResponseEntityResultListener, diveSpotLocation);
        }
    }

    private void setupList(SealifeListResponseEntity sealifeListResponseEntity) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        sealifeList.setLayoutManager(gridLayoutManager);
        if (isHasLocation) {
            if (sealifeListResponseEntity.getNearby() != null) {
                sealifes.addAll(sealifeListResponseEntity.getNearby());
            }
        }
        sealifes.addAll(sealifeListResponseEntity.getAll());
        searchSealifeListAdapter = new SealifeSearchAdapter(this, sealifes);
        if (isHasLocation && sealifeListResponseEntity.getNearby() != null) {
            //TODO implement sectioned search
            searchSealifeListAdapterWithSections = new SealifeSearchAdapter(this, sealifes);
            isSectioned = true;
            List<SealifeSectionedRecyclerViewAdapter.Section> sections = new ArrayList<SealifeSectionedRecyclerViewAdapter.Section>();
            sections.add(new SealifeSectionedRecyclerViewAdapter.Section(0, "Nearby"));
            sections.add(new SealifeSectionedRecyclerViewAdapter.Section(sealifeListResponseEntity.getNearby().size(), "All"));

            SealifeSectionedRecyclerViewAdapter.Section[] dummy = new SealifeSectionedRecyclerViewAdapter.Section[sections.size()];
            sectionedRecyclerViewAdapter = new SealifeSectionedRecyclerViewAdapter(this, R.layout.section_layout, R.id.section_title, sealifeList, searchSealifeListAdapterWithSections);
            sectionedRecyclerViewAdapter.setSections(sections.toArray(dummy));
            searchSealifeListAdapterWithSections.setSectionAdapter(sectionedRecyclerViewAdapter);
            sealifeList.setAdapter(sectionedRecyclerViewAdapter);
            return;
        }
        sealifeList.setAdapter(searchSealifeListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_sealife, menu);
        this.menu = menu;
        searchMenuItem = menu.findItem(R.id.action_search);
        searchMenuItem.setVisible(false);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<SealifeShort> filteredModelList = filter(sealifes, query);
        if (isSectioned) {
            searchSealifeListAdapter.setSectionAdapter(null);
            sealifeList.setLayoutManager(new GridLayoutManager(this, 2));
            sealifeList.setAdapter(searchSealifeListAdapter);
            isSectioned = false;
        }
        searchSealifeListAdapter.animateTo(filteredModelList);
        sealifeList.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<SealifeShort> filter(List<SealifeShort> models, String query) {
        query = query.toLowerCase();

        final List<SealifeShort> filteredModelList = new ArrayList<>();
        for (SealifeShort model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        if (filteredModelList.isEmpty()) {
            sealifeList.setVisibility(View.GONE);
            notFoundLayout.setVisibility(View.VISIBLE);
            Helpers.hideKeyboard(this);
        } else {
            sealifeList.setVisibility(View.VISIBLE);
            notFoundLayout.setVisibility(View.GONE);
        }
        return filteredModelList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_manualy:
                EventsTracker.trackSealifeCreation();
                Intent i = new Intent(SearchSealifeActivity.this, AddSealifeActivity.class);
                startActivityForResult(i, ActivitiesRequestCodes.REQUEST_CODE_SEARCH_SEALIFE_ACTIVITY_ADD_SEALIFE);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DDScannerApplication.activityResumed();
        if (!Helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
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

    @Subscribe
    public void setResult(SealifeChoosedEvent event) {
        Intent intent = new Intent();
        intent.putExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE, event.getSealife());
        setResult(RESULT_OK, intent);
        finish();
    }

    public static void showForResult(Activity context, int requestCode, LatLng latLng) {
        Intent intent = new Intent(context,SearchSealifeActivity.class);
        intent.putExtra(ARG_LOCATION, latLng);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_SEARCH_SEALIFE_ACTIVITY_ADD_SEALIFE:
                if (resultCode == RESULT_OK) {
                    SealifeShort sealife = (SealifeShort) data.getSerializableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE);
                    Intent intent = new Intent();
                    intent.putExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE, sealife);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_SEARCH_SEALIFE_ACTIVITY_UNEXPECTED_ERROR:
            case DialogsRequestCodes.DRC_SEARCH_SEALIFE_ACTIVITY_FAILED_TO_CONNECT:
                finish();
                break;
        }
    }
}
