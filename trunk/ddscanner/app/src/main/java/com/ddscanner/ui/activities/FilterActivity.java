package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.events.FilterChosenEvent;
import com.ddscanner.ui.adapters.CharacteristicSpinnerItemsAdapter;
import com.ddscanner.ui.adapters.DiverLevelSpinnerAdapter;
import com.ddscanner.ui.adapters.SealifeListAddingDiveSpotAdapter;
import com.ddscanner.ui.adapters.SpinnerItemsAdapter;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterActivity extends BaseAppCompatActivity implements View.OnClickListener, DialogClosedListener {

    private static final String TAG = FilterActivity.class.getSimpleName();

    private Toolbar toolbar;
    private FiltersResponseEntity filters = new FiltersResponseEntity();
    private AppCompatSpinner objectSpinner;
    private AppCompatSpinner levelSpinner;
    private Button save;
    private Map<String,String> objectsMap = new HashMap<>();
    private Map<String, String> levelsMap = new HashMap<>();
    private FilterChosenEvent filterChosedEvent = new FilterChosenEvent();
    private MaterialDialog materialDialog;
    private ProgressView progressView;
    private LinearLayout mainLayout;
    private LinearLayout addSealifeButton;
    private RecyclerView sealifesRecyclerView;
    private ArrayList<SealifeShort> sealifes = new ArrayList<>();
    private SealifeListAddingDiveSpotAdapter sealifeListAddingDiveSpotAdapter;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_filter);
        sealifeListAddingDiveSpotAdapter = new SealifeListAddingDiveSpotAdapter(this);
        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_close);
        getSupportActionBar().setTitle("Filter");
     //   DDScannerApplication.getInstance().getDdScannerRestClient().getFilters(getFiltersResultListener);
    }

    private void findViews() {
        materialDialog = Helpers.getMaterialDialog(this);
        progressView = (ProgressView) findViewById(R.id.progressBar);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        objectSpinner = (AppCompatSpinner) findViewById(R.id.object_spinner);
        levelSpinner = (AppCompatSpinner) findViewById(R.id.level_spinner);
        save = (Button) findViewById(R.id.applyFilters);
        addSealifeButton = (LinearLayout) findViewById(R.id.btn_add_sealife);
        sealifesRecyclerView = (RecyclerView) findViewById(R.id.sealife_recycler_view);
        sealifesRecyclerView.setAdapter(sealifeListAddingDiveSpotAdapter);
        sealifesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        save.setOnClickListener(this);
        addSealifeButton.setOnClickListener(this);
        save.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);
        setAppCompatSpinnerValues(levelSpinner, Helpers.getDiveLevelTypes(), "Diver Level");
        setAppCompatSpinnerValues(objectSpinner, Helpers.getDiveSpotTypes(), "Object");
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().getObject().isEmpty()) {
            objectSpinner.setSelection(Integer.parseInt(DDScannerApplication.getInstance().getSharedPreferenceHelper().getObject()));
        }

        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().getLevel().isEmpty()) {
            levelSpinner.setSelection(Integer.parseInt(DDScannerApplication.getInstance().getSharedPreferenceHelper().getLevel()));
        }

        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getSealifesList() != null) {
            sealifeListAddingDiveSpotAdapter.addSealifesList(DDScannerApplication.getInstance().getSharedPreferenceHelper().getSealifesList());
        }

    }

    private void setAppCompatSpinnerValues(AppCompatSpinner spinner, List<String> values, String tag) {
        ArrayList<String> objects = new ArrayList<String>();
        objects.add(tag);
        objects.addAll(values);
        ArrayAdapter<String> adapter = new DiverLevelSpinnerAdapter(this, R.layout.spinner_item, objects, tag);
        spinner.setAdapter(adapter);
    }

    private void setFilerGroup(Spinner spinner, Map<String, String> values) {
        List<String> objects = new ArrayList<String>();
        objects.add("All");
        int selectedIndex = 0;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            objects.add(entry.getValue());
            if (entry.getKey().equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getObject()) || entry.getKey().equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getLevel())) {
                selectedIndex = objects.size() - 1;
            }
        }
        ArrayAdapter<String> adapter = new SpinnerItemsAdapter(this, R.layout.spinner_item, objects);
        spinner.setAdapter(adapter);
        spinner.setSelection(selectedIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.FILTER_ACTIVITY_PICK_SEALIFE:
                Helpers.hideKeyboard(this);
                if (resultCode == RESULT_OK) {
                    SealifeShort sealifeShort = (SealifeShort) data.getSerializableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE);

                    if (Helpers.checkIsSealifeAlsoInList(sealifes, sealifeShort.getId())) {
                        return;
                    }
                    sealifeListAddingDiveSpotAdapter.add(sealifeShort);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_sealife:
                Intent sealifeIntent = new Intent(FilterActivity.this, SearchSealifeActivity.class);
                startActivityForResult(sealifeIntent, ActivitiesRequestCodes.FILTER_ACTIVITY_PICK_SEALIFE);
                break;
            case R.id.applyFilters:
                boolean isFiltersApplied = false;
                if (objectSpinner.getSelectedItemPosition() == 0) {
                    filterChosedEvent.setObject(null);
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setObject("");
                } else {
                    isFiltersApplied = true;
                    filterChosedEvent.setObject(String.valueOf(objectSpinner.getSelectedItemPosition()));
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setObject(String.valueOf(objectSpinner.getSelectedItemPosition()));
                }

                if (levelSpinner.getSelectedItemPosition() == 0) {
                    filterChosedEvent.setLevel(null);
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setLevel("");
                } else {
                    isFiltersApplied = true;
                    filterChosedEvent.setLevel(String.valueOf(levelSpinner.getSelectedItemPosition()));
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setLevel(String.valueOf(levelSpinner.getSelectedItemPosition()));
                }

                if (sealifeListAddingDiveSpotAdapter.getSealifes().size() == 0) {
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setSealifesList(null);
                } else {
                    isFiltersApplied = true;
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setSealifesList(sealifeListAddingDiveSpotAdapter.getSealifes());
                }

                if (isFiltersApplied) {
                    setResult(RESULT_OK);
                } else {
                    setResult(RESULT_CODE_FILTERS_RESETED);
                }
                DDScannerApplication.bus.post(filterChosedEvent);
                //   EventsTracker.trackFilterApplyied(Helpers.getMirrorOfHashMap(levelsMap).get(levelSpinner.getSelectedItem().toString()), Helpers.getMirrorOfHashMap(objectsMap).get(objectSpinner.getSelectedItem().toString()));
                finish();
                break;
        }
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
                DDScannerApplication.getInstance().getSharedPreferenceHelper().clearFilters();
                DDScannerApplication.bus.post(filterChosedEvent);
                setResult(RESULT_CODE_FILTERS_RESETED);
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
