package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.events.FilterChosedEvent;
import com.ddscanner.rest.BaseCallbackOld;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.SpinnerItemsAdapter;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FilterActivity.class.getSimpleName();

    private Toolbar toolbar;
    private FiltersResponseEntity filters = new FiltersResponseEntity();
    private Spinner objectSpinner;
    private Spinner levelSpinner;
    private Button save;
    private Map<String,String> objectsMap = new HashMap<>();
    private Map<String, String> levelsMap = new HashMap<>();
    private FilterChosedEvent filterChosedEvent = new FilterChosedEvent();
    private MaterialDialog materialDialog;
    private ProgressView progressView;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_filter);
        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_close);
        getSupportActionBar().setTitle("Filter");
        request();
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

    private void request() {
       // materialDialog.show();
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getFilters();
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            //    materialDialog.dismiss();
                if (response.isSuccessful()) {
                    progressView.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    filters = new FiltersResponseEntity();
                    filters = new Gson().fromJson(responseString, FiltersResponseEntity.class);

                    Log.i(TAG, responseString);
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
                if (!response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        Helpers.showToast(FilterActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(FilterActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(FilterActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(FilterActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(FilterActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(FilterActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(FilterActivity.this);
            }
        });
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

}
