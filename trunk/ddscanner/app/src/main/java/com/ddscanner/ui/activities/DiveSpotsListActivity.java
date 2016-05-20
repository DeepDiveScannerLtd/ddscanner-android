package com.ddscanner.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.ddscanner.R;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.ui.adapters.ProductListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 20.5.16.
 */
public class DiveSpotsListActivity extends AppCompatActivity {

    private RecyclerView rc;
    private Toolbar toolbar;
    private List<DiveSpot> diveSpots = new ArrayList<>();
    private boolean isAdded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_dive_spots);
    }

    private void findViews() {
        rc = (RecyclerView) findViewById(R.id.divespots_rc);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Dive spots");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
    }

    private void setUi() {
        rc.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc.setLayoutManager(linearLayoutManager);
        rc.setAdapter(new ProductListAdapter((ArrayList<DiveSpot>) diveSpots, this));
    }

}


