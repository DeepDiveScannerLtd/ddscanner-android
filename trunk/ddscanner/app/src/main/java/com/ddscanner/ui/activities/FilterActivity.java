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

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.events.FilterChosedEvent;
import com.ddscanner.events.FiltersChosedEvent;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.SpinnerItemsAdapter;
import com.ddscanner.utils.EventTrackerHelper;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rey.material.widget.Button;
import com.rey.material.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 22.1.16.
 */
public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FilterActivity.class.getSimpleName();

    private Toolbar toolbar;
    private FiltersResponseEntity filters = new FiltersResponseEntity();
    private Spinner objectSpinner;
    private Spinner levelSpinner;
    private Button save;
    private Helpers helpers = new Helpers();
    private Map<String,String> objectsMap = new HashMap<>();
    private Map<String, String> levelsMap = new HashMap<>();
    private FilterChosedEvent filterChosedEvent = new FilterChosedEvent();

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_filter);
        AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                EventTrackerHelper.EVENT_FILTER_OPENED, new HashMap<String, Object>());
        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_close);
        getSupportActionBar().setTitle("Filter");
        request();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        objectSpinner = (Spinner) findViewById(R.id.object_spinner);
        levelSpinner = (Spinner) findViewById(R.id.level_spinner);
        save = (Button) findViewById(R.id.applyFilters);
        save.setOnClickListener(this);
    }


    private void setFilerGroup(Spinner spinner, Map<String, String> values, String tag) {
        List<String> objects = new ArrayList<String>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            objects.add(entry.getValue());
            if (entry.getKey().equals(tag)) {

            }
        }
        ArrayAdapter<String> adapter = new SpinnerItemsAdapter(this, R.layout.spinner_item, objects);
        spinner.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        filterChosedEvent.setObject(helpers.getMirrorOfHashMap(objectsMap)
                .get(objectSpinner.getSelectedItem().toString()));
        filterChosedEvent.setLevel(helpers.getMirrorOfHashMap(levelsMap)
                .get(levelSpinner.getSelectedItem().toString()));
        DDScannerApplication.bus.post(filterChosedEvent);
        finish();
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, FilterActivity.class);
        context.startActivity(intent);
    }

    private void request() {

        Call<ResponseBody> call = RestClient.getServiceInstance().getFilters();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    filters = new FiltersResponseEntity();

                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(responseString).getAsJsonObject();
                    JsonObject currentsJsonObject = jsonObject.getAsJsonObject("currents");
                    for (Map.Entry<String, JsonElement> elementEntry : currentsJsonObject.entrySet()) {
                        filters.getCurrents().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                    }
                    JsonObject levelJsonObject = jsonObject.getAsJsonObject("level");
                    for (Map.Entry<String, JsonElement> elementEntry : levelJsonObject.entrySet()) {
                        filters.getLevel().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                    }
                    JsonObject objectJsonObject = jsonObject.getAsJsonObject("object");
                    for (Map.Entry<String, JsonElement> elementEntry : objectJsonObject.entrySet()) {
                        filters.getObject().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                    }
                    JsonObject visibilityJsonObject = jsonObject.getAsJsonObject("visibility");
                    for (Map.Entry<String, JsonElement> elementEntry : visibilityJsonObject.entrySet()) {
                        filters.getVisibility().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                    }

                    if (jsonObject.get("rating") != null) {
                        Gson gson = new Gson();
                        filters.setRating(gson.fromJson(jsonObject.get("rating").getAsJsonArray(), int[].class));
                    }


                    Log.i(TAG, responseString);
                    objectsMap = filters.getObject();
                    levelsMap = filters.getLevel();
                    setFilerGroup(objectSpinner, filters.getObject(), SharedPreferenceHelper.getCurrents());
                    setFilerGroup(levelSpinner, filters.getLevel(), SharedPreferenceHelper.getCurrents());
                } else {
                    // TODO Handle errors
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // TODO Handle errors
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                        EventTrackerHelper.EVENT_FILTER_CANCELLED, new HashMap<String, Object>());
                onBackPressed();
                finish();
            case R.id.reset_filters:
                filterChosedEvent.setLevel(null);
                filterChosedEvent.setObject(null);
                DDScannerApplication.bus.post(filterChosedEvent);
                finish();
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
        if (!helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
    }

}
