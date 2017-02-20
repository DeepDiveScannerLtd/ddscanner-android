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
import com.ddscanner.entities.DialogClosedListener;
import com.ddscanner.entities.Sealife;
import com.ddscanner.entities.SealifeResponseEntity;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.events.SealifeChoosedEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.sealife.add.AddSealifeActivity;
import com.ddscanner.ui.adapters.SealifeSearchAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SearchSealifeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener, DialogClosedListener {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private SealifeSearchAdapter mAdapter;
    private TextView results;
    private RelativeLayout notFoundLayout;
    private TextView textNotFound;
    private Button addManually;
    private Menu menu;
    private ProgressView progressView;
    private RelativeLayout contentLayout;

    private List<SealifeShort> sealifes = new ArrayList<>();
    private SealifeResponseEntity sealifeResponseEntity = new SealifeResponseEntity();

    private DDScannerRestClient.ResultListener<ArrayList<SealifeShort>> sealifeResponseEntityResultListener = new DDScannerRestClient.ResultListener<ArrayList<SealifeShort>>() {
        @Override
        public void onSuccess(ArrayList<SealifeShort> result) {
            sealifes = result;
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new SealifeSearchAdapter(SearchSealifeActivity.this, sealifes);
            progressView.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setAdapter(mAdapter);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_SEARCH_SEALIFE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            EventsTracker.trackUnknownServerError(url, errorMessage);
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_SEARCH_SEALIFE_ACTIVITY_UNEXPECTED_ERROR, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_SEARCH_SEALIFE_ACTIVITY_FAILED_TO_CONNECT, false);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sealife);
        EventsTracker.trackSearchSeaLife();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        notFoundLayout = (RelativeLayout) findViewById(R.id.not_found_layout);
        textNotFound = (TextView) findViewById(R.id.text_not_found);
        addManually = (Button) findViewById(R.id.add_manualy);
        progressView = (ProgressView) findViewById(R.id.progressBarFull);
        contentLayout = (RelativeLayout) findViewById(R.id.content_layout);
        addManually.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.search_sealife);
        DDScannerApplication.getInstance().getDdScannerRestClient().getSealifesByQuery(sealifeResponseEntityResultListener);
        setupList();
    }

    private void setupList() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //TODO implement sectioned search
//        List<SealifeSectionedRecyclerViewAdapter.Section> sections = new ArrayList<SealifeSectionedRecyclerViewAdapter.Section>();
//        sections.add(new SealifeSectionedRecyclerViewAdapter.Section(0, "Newest"));
//        sections.add(new SealifeSectionedRecyclerViewAdapter.Section(5, "Older"));
//
//        SealifeSectionedRecyclerViewAdapter.Section[] dummy = new SealifeSectionedRecyclerViewAdapter.Section[sections.size()];
//        SealifeSectionedRecyclerViewAdapter sectionedRecyclerViewAdapter = new SealifeSectionedRecyclerViewAdapter(this, R.layout.section_layout, R.id.section_title, mRecyclerView, searchSealifeListAdapter);
//        sectionedRecyclerViewAdapter.setSections(sections.toArray(dummy));
//        searchSealifeListAdapter.setSectionAdapter(sectionedRecyclerViewAdapter);
//        mRecyclerView.setAdapter(sectionedRecyclerViewAdapter);
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

    @Override
    public boolean onQueryTextChange(String query) {
        final List<SealifeShort> filteredModelList = filter(sealifes, query);
        mAdapter.animateTo(filteredModelList);
        mRecyclerView.scrollToPosition(0);
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
            mRecyclerView.setVisibility(View.GONE);
            notFoundLayout.setVisibility(View.VISIBLE);
            Helpers.hideKeyboard(this);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            notFoundLayout.setVisibility(View.GONE);
        }
        return filteredModelList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_manualy:
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
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    @Subscribe
    public void setResult(SealifeChoosedEvent event) {
        Intent intent = new Intent();
        intent.putExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE, event.getSealife());
        setResult(RESULT_OK, intent);
        finish();
    }

    public static void showForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context,SearchSealifeActivity.class);
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
                    Sealife sealife = (Sealife) data.getSerializableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE);
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
