package com.ddscanner.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Sealife;
import com.ddscanner.entities.Sealife;
import com.ddscanner.entities.SealifeResponseEntity;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.events.SealifeChoosedEvent;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.SealifeSearchAdapter;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 7.4.16.
 */
public class SearchSealifeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {

    private static final int RC_ADD_SEALIFE = 8001;

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private SealifeSearchAdapter mAdapter;
    private TextView results;
    private RelativeLayout notFoundLayout;
    private TextView textNotFound;
    private Button addManually;
    private Helpers helpers = new Helpers();
    private Menu menu;
    private ProgressView progressView;
    private RelativeLayout contentLayout;


    private List<Sealife> sealifes = new ArrayList<>();
    private SealifeResponseEntity sealifeResponseEntity = new SealifeResponseEntity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sealife);
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
        getAllSealifes();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        final List<Sealife> filteredModelList = filter(sealifes, query);
        mAdapter.animateTo(filteredModelList);
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Sealife> filter(List<Sealife> models, String query) {
        query = query.toLowerCase();

        final List<Sealife> filteredModelList = new ArrayList<>();
        for (Sealife model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        if (filteredModelList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            notFoundLayout.setVisibility(View.VISIBLE);
            textNotFound.setText("\"" + query + "\" not found");
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
                startActivityForResult(i, RC_ADD_SEALIFE);
                break;
        }
    }

    private void getAllSealifes() {
        Call<ResponseBody> call = RestClient.getServiceInstance().getSealifes();
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
                    sealifeResponseEntity = new Gson().fromJson(responseString, SealifeResponseEntity.class);
                    sealifes = sealifeResponseEntity.getSealifes();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mAdapter = new SealifeSearchAdapter(SearchSealifeActivity.this, sealifes);
                    progressView.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);
                    mRecyclerView.setAdapter(mAdapter);
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
                        helpers.showToast(SearchSealifeActivity.this, R.string.toast_server_error);
                        finish();
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(SearchSealifeActivity.this, R.string.toast_server_error);
                        finish();
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(SearchSealifeActivity.this, R.string.toast_server_error);
                        finish();
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(SearchSealifeActivity.this, R.string.toast_server_error);
                        finish();
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(SearchSealifeActivity.this, R.string.toast_server_error);
                        finish();
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(SearchSealifeActivity.this, R.string.toast_server_error);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
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
        if (requestCode == RC_ADD_SEALIFE) {
            if (resultCode == RESULT_OK) {
                Sealife sealife = (Sealife) data.getSerializableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE);
                Intent intent = new Intent();
                intent.putExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE, sealife);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}
