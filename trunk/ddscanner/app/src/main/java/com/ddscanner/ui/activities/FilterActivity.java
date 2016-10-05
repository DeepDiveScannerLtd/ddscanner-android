package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.events.FilterChosenEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.SpinnerItemsAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener, InfoDialogFragment.DialogClosedListener {

    private static final String TAG = FilterActivity.class.getSimpleName();

    private Toolbar toolbar;
    private FiltersResponseEntity filters = new FiltersResponseEntity();
    private Spinner objectSpinner;
    private Spinner levelSpinner;
    private Button save;
    private Map<String,String> objectsMap = new HashMap<>();
    private Map<String, String> levelsMap = new HashMap<>();
    private FilterChosenEvent filterChosedEvent = new FilterChosenEvent();
    private MaterialDialog materialDialog;
    private ProgressView progressView;
    private LinearLayout mainLayout;

    private DDScannerRestClient.ResultListener<FiltersResponseEntity> getFiltersResultListener = new DDScannerRestClient.ResultListener<FiltersResponseEntity>() {
        @Override
        public void onSuccess(FiltersResponseEntity result) {
            progressView.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);

            filters = result;

            if (filters.getObject() != null) {
                objectsMap = filters.getObject();
                setFilerGroup(objectSpinner, filters.getObject(), SharedPreferenceHelper.getCurrents());
            }
            if (filters.getLevel() != null) {
                levelsMap = filters.getLevel();
                setFilerGroup(levelSpinner, filters.getLevel(), SharedPreferenceHelper.getCurrents());
            }

            if (filters.getObject() == null || filters.getLevel() == null) {
                Toast.makeText(FilterActivity.this, R.string.toast_server_error, Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
                save.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_FILTER_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            EventsTracker.trackUnknownServerError(url, errorMessage);
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_FILTER_ACTIVITY_UNEXPECTED_ERROR, false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_filter);
        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_close);
        getSupportActionBar().setTitle("Filter");
        DDScannerApplication.getDdScannerRestClient().getFilters(getFiltersResultListener);
    }

    private void findViews() {
        materialDialog = Helpers.getMaterialDialog(this);
        progressView = (ProgressView) findViewById(R.id.progressBar);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        objectSpinner = (Spinner) findViewById(R.id.object_spinner);
        levelSpinner = (Spinner) findViewById(R.id.level_spinner);
        save = (Button) findViewById(R.id.applyFilters);
        save.setOnClickListener(this);
        save.setVisibility(View.GONE);
    }


    private void setFilerGroup(Spinner spinner, Map<String, String> values, String tag) {
        List<String> objects = new ArrayList<String>();
        objects.add("All");
        int selectedIndex = 0;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            objects.add(entry.getValue());
            if (entry.getKey().equals(SharedPreferenceHelper.getObject()) || entry.getKey().equals(SharedPreferenceHelper.getLevel())) {
                selectedIndex = objects.size() - 1;
            }
        }
        ArrayAdapter<String> adapter = new SpinnerItemsAdapter(this, R.layout.spinner_item, objects);
        spinner.setAdapter(adapter);
        spinner.setSelection(selectedIndex);
    }


    @Override
    public void onClick(View v) {
        if (objectSpinner.getSelectedItem().toString().equals("All")) {
            filterChosedEvent.setObject(null);
            SharedPreferenceHelper.setObject("");
        } else {
            filterChosedEvent.setObject(Helpers.getMirrorOfHashMap(objectsMap)
                    .get(objectSpinner.getSelectedItem().toString()));
            SharedPreferenceHelper.setObject(Helpers.getMirrorOfHashMap(objectsMap)
                    .get(objectSpinner.getSelectedItem().toString()));
        }
        if (levelSpinner.getSelectedItem().toString().equals("All")) {
            filterChosedEvent.setLevel(null);
            SharedPreferenceHelper.setLevel("");
        } else {
            filterChosedEvent.setLevel(Helpers.getMirrorOfHashMap(levelsMap)
                    .get(levelSpinner.getSelectedItem().toString()));
            SharedPreferenceHelper.setLevel(Helpers.getMirrorOfHashMap(levelsMap)
                    .get(levelSpinner.getSelectedItem().toString()));
        }
        DDScannerApplication.bus.post(filterChosedEvent);
     //   EventsTracker.trackFilterApplyied(Helpers.getMirrorOfHashMap(levelsMap).get(levelSpinner.getSelectedItem().toString()), Helpers.getMirrorOfHashMap(objectsMap).get(objectSpinner.getSelectedItem().toString()));
        finish();
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, FilterActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.reset_filters:
                filterChosedEvent.setLevel(null);
                filterChosedEvent.setObject(null);
                clearFilterSharedPrefences();
                DDScannerApplication.bus.post(filterChosedEvent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    private void clearFilterSharedPrefences() {
        SharedPreferenceHelper.setObject("");
        SharedPreferenceHelper.setLevel("");
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
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_FILTER_ACTIVITY_FAILED_TO_CONNECT:
            case DialogsRequestCodes.DRC_FILTER_ACTIVITY_UNEXPECTED_ERROR:
                finish();
                break;
        }
    }
}
