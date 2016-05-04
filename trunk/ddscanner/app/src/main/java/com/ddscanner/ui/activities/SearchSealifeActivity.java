package com.ddscanner.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.ExampleModel;
import com.ddscanner.ui.adapters.SealifeSearchAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 7.4.16.
 */
public class SearchSealifeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {

    private static final int RC_ADD_SEALIFE = 8001;

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private SealifeSearchAdapter mAdapter;
    private TextView results;
    private List<ExampleModel> mModels;
    private RelativeLayout notFoundLayout;
    private TextView textNotFound;
    private Button addManually;
    private static final String[] MOVIES = new String[]{
            "Octopus",
            "Huyopus",
            "Baracuda",
            "Evgeniy",
            "AXAXAXAXAXAXAX",
            "FUck",
            "Belarus",
            "Very frog",
            "Carilna"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sealife);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        notFoundLayout = (RelativeLayout) findViewById(R.id.not_found_layout);
        textNotFound = (TextView) findViewById(R.id.text_not_found);
        addManually = (Button) findViewById(R.id.add_manualy);
        addManually.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mModels = new ArrayList<>();

        for (String movie : MOVIES) {
            mModels.add(new ExampleModel(movie));
        }

        if (!mModels.isEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        mAdapter = new SealifeSearchAdapter(this, mModels);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_sealife, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<ExampleModel> filteredModelList = filter(mModels, query);
        mAdapter.animateTo(filteredModelList);
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<ExampleModel> filter(List<ExampleModel> models, String query) {
        query = query.toLowerCase();

        final List<ExampleModel> filteredModelList = new ArrayList<>();
        for (ExampleModel model : models) {
            final String text = model.getText().toLowerCase();
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
}
